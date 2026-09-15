import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Image {

    private int width;
    private int height;

    // pixels[y][x][0] = rouge
    // pixels[y][x][1] = vert
    // pixels[y][x][2] = bleu
    private int[][][] pixels;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Constructeur
    public Image(int width, int height) {
        this.width = width;
        this.height = height;

        pixels = new int[height][width][3];
    }

    // Modifier un pixel
    public void setPixel(int x, int y, int r, int g, int b) {

        if (x >= 0 && x < width && y >= 0 && y < height) {

            pixels[y][x][0] = r;
            pixels[y][x][1] = g;
            pixels[y][x][2] = b;
        }
    }

    // Lire le rouge
    public int getRed(int x, int y) {
        return pixels[y][x][0];
    }

    // Lire le vert
    public int getGreen(int x, int y) {
        return pixels[y][x][1];
    }

    // Lire le bleu
    public int getBlue(int x, int y) {
        return pixels[y][x][2];
    }

    // Sauvegarder en PPM texte
    public void save(String filename) throws IOException {

        FileWriter writer = new FileWriter(filename);

        writer.write("P3\n");
        writer.write(width + " " + height + "\n");
        writer.write("255\n");

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                writer.write(
                    pixels[y][x][0] + " " +
                    pixels[y][x][1] + " " +
                    pixels[y][x][2] + " "
                );
            }

            writer.write("\n");
        }

        writer.close();
    }

    public void write_bin(String filename) throws IOException {

        FileOutputStream output = new FileOutputStream(filename);

        output.write("P6\n".getBytes());
        output.write((width + " " + height + "\n").getBytes());
        output.write("255\n".getBytes());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                output.write(pixels[y][x][0]);
                output.write(pixels[y][x][1]);
                output.write(pixels[y][x][2]);
            }
        }

        output.close();
    }

    public static Image read_bin(String filename) throws IOException {

        FileInputStream input = new FileInputStream(filename);

        // Lire le header
        String format = lireMot(input);
        int width = Integer.parseInt(lireMot(input));
        int height = Integer.parseInt(lireMot(input));
        int max = Integer.parseInt(lireMot(input));

        if (!format.equals("P6")) {
            input.close();
            throw new IOException("Le fichier n'est pas au format P6.");
        }

        if (max != 255) {
            input.close();
            throw new IOException("La valeur maximale doit être 255.");
        }

        Image image = new Image(width, height);

        // Lire les pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int r = input.read();
                int g = input.read();
                int b = input.read();

                image.setPixel(x, y, r, g, b);
            }
        }

        input.close();

        return image;
    }

    // Lire un élément du header
    private static String lireMot(FileInputStream input) throws IOException {

        String mot = "";
        int c;

        // Ignorer les espaces
        do {
            c = input.read();
        } while (c == ' ' || c == '\n' || c == '\r' || c == '\t');

        // Lire le mot
        while (c != ' ' && c != '\n' && c != '\r' && c != '\t' && c != -1) {
            mot += (char) c;
            c = input.read();
        }

        return mot;
    }
}
