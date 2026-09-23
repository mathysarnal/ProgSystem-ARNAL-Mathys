public class Utils {

    public static int writeInt (byte[] memory, int offset, int value) {
        memory[offset] = (byte) (value >> 24);
        memory[offset + 1] = (byte) (value >> 16);
        memory[offset + 2] = (byte) (value >> 8);
        memory[offset + 3] = (byte) (value);

        return 4;
    }

    public static int readInt(byte[] memory, int offset) {
        int v1 = (memory[offset] & 0xFF) << 24;
        int v2 = (memory[offset + 1] & 0xFF) << 16;
        int v3 = (memory[offset + 2] & 0xFF) << 8;
        int v4 = (memory[offset + 3] & 0xFF);

        return v1 | v2 | v3 | v4;
    }

    public static int writeShort(byte[] memory, int offset, short value) {
        memory[offset] = (byte) (value >> 8);
        memory[offset + 1] = (byte) (value);

        return 2;
    }

    public static short readShort(byte[] memory, int offset) {
        int v1 = (memory[offset] & 0xFF) << 8;
        int v2 = (memory[offset + 1] & 0xFF);

        return (short) (v1 | v2);
    }

    public static int writeLong(byte[] memory, int offset, long value) {
        memory[offset] = (byte) (value >> 56);
        memory[offset + 1] = (byte) (value >> 48);
        memory[offset + 2] = (byte) (value >> 40);
        memory[offset + 3] = (byte) (value >> 32);
        memory[offset + 4] = (byte) (value >> 24);
        memory[offset + 5] = (byte) (value >> 16);
        memory[offset + 6] = (byte) (value >> 8);
        memory[offset + 7] = (byte) (value);

        return 8;
    }

    public static long readLong(byte[] memory, int offset) {
        long v1 = ((long) memory[offset] & 0xFF) << 56;
        long v2 = ((long) memory[offset + 1] & 0xFF) << 48;
        long v3 = ((long) memory[offset + 2] & 0xFF) << 40;
        long v4 = ((long) memory[offset + 3] & 0xFF) << 32;
        long v5 = ((long) memory[offset + 4] & 0xFF) << 24;
        long v6 = ((long) memory[offset + 5] & 0xFF) << 16;
        long v7 = ((long) memory[offset + 6] & 0xFF) << 8;
        long v8 = ((long) memory[offset + 7] & 0xFF);

        return v1 | v2 | v3 | v4 | v5 | v6 | v7 | v8;
    }

    public static int writeString(
        byte[] memory,
        int offset,
        String str,
        int maxLength) {

        // TODO:
        // 1. Convertir la chaîne en octets.
        // 2. Copier les octets sans dépasser maxLength.
        // 3. Nettoyer le reste de la zone avec des zéros.

        return maxLength;
    }

    public static String readString(
            byte[] memory,
            int offset,
            int maxLength) {

        // TODO:
        // Lire jusqu'au premier octet nul
        // ou jusqu'à maxLength.

        return "";
    }
}