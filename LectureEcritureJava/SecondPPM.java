public class SecondPPM {

    public static void main(String[] args) {

        try {

            Image img = Image.read_bin("gradient.ppm");

            System.out.println("Largeur : " + img.getWidth());
            System.out.println("Hauteur : " + img.getHeight());

            img.write_bin("copy.ppm");

            System.out.println("Copie créée !");

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}