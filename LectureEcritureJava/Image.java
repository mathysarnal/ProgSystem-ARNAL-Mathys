import java.io.FileWriter;
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
}
