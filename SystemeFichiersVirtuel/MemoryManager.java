public class MemoryManager {

    public static final int BLOCK_SIZE = 512;
    public static final int TOTAL_MEMORY = 1024 * 1024;
    public static final int NUM_BLOCKS =
            TOTAL_MEMORY / BLOCK_SIZE;

    public static final int SUPERBLOCK_OFFSET = 0;
    public static final int BITMAP_OFFSET = BLOCK_SIZE;
    public static final int INODE_TABLE_OFFSET =
            2 * BLOCK_SIZE;
    public static final int DATA_OFFSET =
            129 * BLOCK_SIZE;

    public static final int INODE_SIZE = 128;

    public static final int INODE_TABLE_SIZE =
            DATA_OFFSET - INODE_TABLE_OFFSET;

    public static final int MAX_INODES =
            INODE_TABLE_SIZE / INODE_SIZE;

    private byte[] memory;

    public MemoryManager() {
        this.memory = new byte[TOTAL_MEMORY];
        initializeFilesystem();
    }

    public int findFreeBlock() {
        for (int block = 129; block < NUM_BLOCKS; block++) {
            if (isBlockUsed(block) == 0) {
                return block;
            }
        }

        return -1;
    }

    public int getBlockOffset(int blockNum) {
        if (blockNum < 0 || blockNum >= NUM_BLOCKS) {
            return -1;
        }

        return blockNum * BLOCK_SIZE;
    }

    private void initializeFilesystem() {
        writeSuperblock();

        // Les blocs 0 à 128 sont réservés par le layout.
        // Ils ne doivent jamais être proposés par allocateBlock().
    }

    public boolean setBlockUsed(int blockNumber, boolean used) {

        if (blockNumber < 0 ||
                blockNumber >= NUM_BLOCKS) {
            return false;
        }

        int byteIndex = blockNumber / 8;
        int bitPosition = blockNumber % 8;
        int offset = BITMAP_OFFSET + byteIndex;

        int masque = 1 << bitPosition;

        if (used) {
            memory[offset] |= masque;
        } else {
            memory[offset] &= ~masque;
        }

        return true;
    }

    public int isBlockUsed(int blockNumber) {

        if (blockNumber < 0 ||
                blockNumber >= NUM_BLOCKS) {
            return -1;
        }

        int byteIndex = blockNumber / 8;
        int bitPosition = blockNumber % 8;
        int offset = BITMAP_OFFSET + byteIndex;

        int masque = 1 << bitPosition;

        return (memory[offset] & masque) != 0 ? 1 : 0;
    }

    public int allocateBlock() {

        for (int block = 129; block < NUM_BLOCKS; block++) {

            if (isBlockUsed(block) == 0) {
                setBlockUsed(block, true);
                return block;
            }
        }

        return -1;
    }

    private void writeSuperblock() {

        Utils.writeString(
                memory,
                SUPERBLOCK_OFFSET,
                "MYFS1.0",
                16);

        Utils.writeInt(
                memory,
                SUPERBLOCK_OFFSET + 16,
                BLOCK_SIZE);

        Utils.writeInt(
                memory,
                SUPERBLOCK_OFFSET + 20,
                TOTAL_MEMORY);

        Utils.writeInt(
                memory,
                SUPERBLOCK_OFFSET + 24,
                NUM_BLOCKS);

        Utils.writeInt(
                memory,
                SUPERBLOCK_OFFSET + 28,
                MAX_INODES);
    }

    public byte[] getFilesystemMemory() {
        return memory;
    }

    public int allocateInode(int fileType) {

        for (int i = 0; i < MAX_INODES; i++) {

            Inode inode = new Inode(this, i);

            if (inode.getFileType() == 0) {

                int[] emptyPointers =
                        new int[Inode.DIRECT_POINTERS];

                for (int p = 0;
                     p < emptyPointers.length;
                     p++) {

                    emptyPointers[p] = -1;
                }

                inode.writeInode(
                        fileType,
                        0,
                        emptyPointers);

                return i;
            }
        }

        return -1;
    }
}