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

    private void initializeFilesystem() {
        writeSuperblock();

        // Réserver les blocs système 0 à 128.
        allocateBlock();
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
            // Positionner le bit à 1.
            memory[offset] |= masque;
        } else {
            // Positionner le bit à 0.
            memory[offset] &= ~masque;
            // ou memory[offset] ^= masque;
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

        // TODO:
        // Parcourir les blocs de données :
        // 129 .. NUM_BLOCKS - 1.
        //
        // Retourner le premier bloc libre.
        // Le marquer immédiatement comme utilisé.

        for (int bloc = 129; bloc < NUM_BLOCKS; bloc++) {
            if (isBlockUsed(bloc) == 0) {
                setBlockUsed(bloc, true);
                return bloc;
            }
        }

        return -1;
    }

    private void writeSuperblock() {
        // TODO:
        // Utiliser Utils pour écrire les métadonnées.

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
}
