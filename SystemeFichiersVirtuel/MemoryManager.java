import java.io.*;

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
        reserveSystemBlocks();
    }

    private void reserveSystemBlocks() {
        // Blocs 0 à 128 inclus = 129 blocs réservés au système
        for (int block = 0; block <= 128; block++) {
            setBlockUsed(block);
        }
    }

    private void setBlockUsed(int blockNumber) {
        int byteIndex = blockNumber / 8;   // quel octet du bitmap
        int bitIndex = blockNumber % 8;    // quel bit dans cet octet

        int mask = 1 << (7 - bitIndex); // bit de poids fort = bloc 0 de l'octet
        memory[BITMAP_OFFSET + byteIndex] |= (byte) mask;
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
