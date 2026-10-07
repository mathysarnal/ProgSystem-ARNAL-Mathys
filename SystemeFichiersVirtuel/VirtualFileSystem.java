import java.util.*;

public class VirtualFileSystem {

    private MemoryManager memoryManager;

    public VirtualFileSystem() {
        this.memoryManager = new MemoryManager();
    }

    private int allocateInode() {

        for (int i = 0;
             i < MemoryManager.MAX_INODES;
             i++) {

            Inode inode =
                    new Inode(memoryManager, i);

            if (inode.getFileType() == 0) {
                return i;
            }
        }

        return -1;
    }

    public boolean createFile(
            String directory,
            String filename) {

        int inodeNum =
                allocateInode();

        if (inodeNum == -1) {
            return false;
        }

        Inode inode =
                new Inode(memoryManager, inodeNum);

        long now =
                System.currentTimeMillis();

        int[] emptyPointers =
                new int[Inode.DIRECT_POINTERS];

        Arrays.fill(
                emptyPointers,
                -1);

        inode.writeToMemory(
                1,
                0,
                now,
                now,
                emptyPointers,
                -1,
                (short) 0644,
                1);

        return true;
    }

    public boolean writeFile(
            int inodeNum,
            byte[] data) {

        Inode inode = new Inode(memoryManager, inodeNum);

        if (inode.getFileType() == 0) {
        return false;
        }

        int blocksNeeded =
                (data.length
                + MemoryManager.BLOCK_SIZE - 1)
                / MemoryManager.BLOCK_SIZE;

        if (blocksNeeded >
                Inode.DIRECT_POINTERS) {

            return false;
        }

        int[] blockPointers =
                new int[Inode.DIRECT_POINTERS];

        Arrays.fill(blockPointers, -1);

        int[] oldPointers = inode.getDirectPointers();

        for (int blockNum : oldPointers) {
            if (blockNum >= 129 && blockNum < MemoryManager.NUM_BLOCKS) {
                memoryManager.setBlockUsed(blockNum, false);
            }
        }

        // Allocation des blocs
        for (int i = 0;
             i < blocksNeeded;
             i++) {

            int blockNum =
                    memoryManager.allocateBlock();

            if (blockNum == -1) {

                // On libère les blocs déjà alloués.
                for (int j = 0; j < i; j++) {
                    memoryManager.setBlockUsed(
                            blockPointers[j],
                            false);
                }

                return false;
            }

            blockPointers[i] =
                    blockNum;
        }

        byte[] memory =
                memoryManager.getFilesystemMemory();

        int bytesRemaining =
                data.length;

        int dataSrcOffset = 0;

        // Écriture des données dans les blocs
        for (int i = 0;
             i < blocksNeeded;
             i++) {

            int blockNum =
                    blockPointers[i];

            int physOffset =
                    memoryManager.getBlockOffset(
                            blockNum);

            int bytesToCopy =
                    Math.min(
                            bytesRemaining,
                            MemoryManager.BLOCK_SIZE);

            System.arraycopy(
                    data,
                    dataSrcOffset,
                    memory,
                    physOffset,
                    bytesToCopy);

            dataSrcOffset +=
                    bytesToCopy;

            bytesRemaining -=
                    bytesToCopy;
        }

        long now =
                System.currentTimeMillis();

        inode.writeToMemory(
                1,
                data.length,
                inode.getCreationTime(),
                now,
                blockPointers,
                -1,
                (short) 0644,
                1);

        return true;
    }

    public byte[] readFile(
            int inodeNum) {

        Inode inode =
                new Inode(
                        memoryManager,
                        inodeNum);

        int fileSize =
                inode.getFileSize();

        if (fileSize == 0) {
            return new byte[0];
        }

        byte[] fileData =
                new byte[fileSize];

        byte[] memory =
                memoryManager.getFilesystemMemory();

        int[] blockPointers =
                inode.getDirectPointers();

        int bytesRemaining =
                fileSize;

        int destOffset = 0;

        for (int blockNum :
                blockPointers) {

            if (blockNum == -1 ||
                    bytesRemaining <= 0) {
                break;
            }

            int physOffset =
                    memoryManager.getBlockOffset(
                            blockNum);

            int bytesToCopy =
                    Math.min(
                            bytesRemaining,
                            MemoryManager.BLOCK_SIZE);

            System.arraycopy(
                    memory,
                    physOffset,
                    fileData,
                    destOffset,
                    bytesToCopy);

            destOffset +=
                    bytesToCopy;

            bytesRemaining -=
                    bytesToCopy;
        }

        return fileData;
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }

    public boolean deleteFile(int inodeNum) {

        if (inodeNum < 0 ||
                inodeNum >= MemoryManager.MAX_INODES) {
                return false;
        }

        Inode inode =
                new Inode(memoryManager, inodeNum);

        if (inode.getFileType() == 0) {
                return false;
        }

        int[] pointers =
                inode.getDirectPointers();

        for (int blockNum : pointers) {

                if (blockNum >= 129 &&
                        blockNum < MemoryManager.NUM_BLOCKS) {

                memoryManager.setBlockUsed(
                        blockNum,
                        false);
                }
        }

        int[] emptyPointers =
                new int[Inode.DIRECT_POINTERS];

        Arrays.fill(
                emptyPointers,
                -1);

        inode.writeToMemory(
                0,
                0,
                0L,
                0L,
                emptyPointers,
                -1,
                (short) 0,
                0
        );

        return true;
    }
}