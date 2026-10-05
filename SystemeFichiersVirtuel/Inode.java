public class Inode {

    private MemoryManager memoryManager;
    private int inodeNumber;

    public static final int INODE_SIZE = 128;
    public static final int DIRECT_POINTERS = 10;

    // Offsets de la structure binaire
    public static final int OFFSET_INODE_ID = 0;
    public static final int OFFSET_FILE_TYPE = 4;
    public static final int OFFSET_FILE_SIZE = 8;
    public static final int OFFSET_CREATION_TIME = 12;
    public static final int OFFSET_MODIFICATION_TIME = 20;
    public static final int OFFSET_POINTERS = 28;
    public static final int OFFSET_INDIRECT_POINTER = 68;
    public static final int OFFSET_PERMISSIONS = 72;
    public static final int OFFSET_LINK_COUNT = 74;

    public Inode(
            MemoryManager memoryManager,
            int inodeNumber) {

        this.memoryManager = memoryManager;
        this.inodeNumber = inodeNumber;
    }

    public int getInodeNumber() {
        return inodeNumber;
    }

    public int getInodeOffset() {

        return MemoryManager.INODE_TABLE_OFFSET
                + (this.inodeNumber * INODE_SIZE);
    }

    public int getFileType() {

        int offset = getInodeOffset();

        return Utils.readInt(
                memoryManager.getFilesystemMemory(),
                offset + OFFSET_FILE_TYPE);
    }

    public int getFileSize() {

        int offset = getInodeOffset();

        return Utils.readInt(
                memoryManager.getFilesystemMemory(),
                offset + OFFSET_FILE_SIZE);
    }

    public long getCreationTime() {

        return Utils.readLong(
                memoryManager.getFilesystemMemory(),
                getInodeOffset() + OFFSET_CREATION_TIME);
    }

    public int[] getDirectPointers() {

        byte[] memory =
                memoryManager.getFilesystemMemory();

        int[] pointers =
                new int[DIRECT_POINTERS];

        int baseOffset =
                getInodeOffset() + OFFSET_POINTERS;

        for (int i = 0;
             i < DIRECT_POINTERS;
             i++) {

            pointers[i] =
                    Utils.readInt(
                            memory,
                            baseOffset + (i * 4));
        }

        return pointers;
    }

    public void writeInode(
            int fileType,
            int fileSize,
            int[] directPointers) {

        long now = System.currentTimeMillis();

        writeToMemory(
                fileType,
                fileSize,
                now,
                now,
                directPointers,
                -1,
                (short) 0644,
                1);
    }

    public void writeToMemory(
            int fileType,
            int fileSize,
            long creationTime,
            long modificationTime,
            int[] directPointers,
            int indirectPointer,
            short permissions,
            int linkCount) {

        byte[] memory =
                memoryManager.getFilesystemMemory();

        int offset =
                getInodeOffset();

        Utils.writeInt(
                memory,
                offset + OFFSET_INODE_ID,
                inodeNumber);

        Utils.writeInt(
                memory,
                offset + OFFSET_FILE_TYPE,
                fileType);

        Utils.writeInt(
                memory,
                offset + OFFSET_FILE_SIZE,
                fileSize);

        Utils.writeLong(
                memory,
                offset + OFFSET_CREATION_TIME,
                creationTime);

        Utils.writeLong(
                memory,
                offset + OFFSET_MODIFICATION_TIME,
                modificationTime);

        for (int i = 0;
             i < DIRECT_POINTERS;
             i++) {

            int ptr =
                    (directPointers != null
                    && i < directPointers.length)
                    ? directPointers[i]
                    : -1;

            Utils.writeInt(
                    memory,
                    offset + OFFSET_POINTERS + (i * 4),
                    ptr);
        }

        Utils.writeInt(
                memory,
                offset + OFFSET_INDIRECT_POINTER,
                indirectPointer);

        Utils.writeShort(
                memory,
                offset + OFFSET_PERMISSIONS,
                permissions);

        Utils.writeInt(
                memory,
                offset + OFFSET_LINK_COUNT,
                linkCount);
    }
}