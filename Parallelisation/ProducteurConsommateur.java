import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class ProducteurConsommateur {

    private static final int TAILLE_BUFFER = 5;
    private final Integer[] buffer = new Integer[TAILLE_BUFFER];

    // Le buffer est circulaire : on garde où écrire et où lire
    private int indexEcriture = 0;
    private int indexLecture = 0;

    // Compteurs pour le monitoring
    private int nombreProduits = 0;
    private int nombreConsommes = 0;

    private final Semaphore placesLibres = new Semaphore(TAILLE_BUFFER);
    private final Semaphore elementsDisponibles = new Semaphore(0);
    private final Semaphore mutexBuffer = new Semaphore(1); // quel erreur ici ? vous pouvez le résoudre plus tard
                                                            // Inutile d'utiliser un sémaphore

    private static int genererProduit() {
        return (int) (Math.random() * 100);
    }

    // Appelée uniquement quand on possède mutexBuffer
    private void ajouterElement(int produit) {
        buffer[indexEcriture] = produit;
        indexEcriture = (indexEcriture + 1) % TAILLE_BUFFER;
        nombreProduits++;
    }

    // Appelée uniquement quand on possède mutexBuffer
    private int retirerElement() {
        Integer produit = buffer[indexLecture];
        buffer[indexLecture] = null;
        indexLecture = (indexLecture + 1) % TAILLE_BUFFER;
        nombreConsommes++;
        return produit;
    }

    // Appelée uniquement quand on possède mutexBuffer
    private String etatBuffer() {
        return Arrays.toString(buffer) + " (produits : " + nombreProduits
                + ", consommés : " + nombreConsommes + ")";
    }

    public int getNombreProduits() {
        mutexBuffer.acquireUninterruptibly();
        int resultat = nombreProduits;
        mutexBuffer.release();
        return resultat;
    }

    public int getNombreConsommes() {
        mutexBuffer.acquireUninterruptibly();
        int resultat = nombreConsommes;
        mutexBuffer.release();
        return resultat;
    }

    static class Producteur implements Runnable {

        private final String nom;
        private final ProducteurConsommateur pc;
        private final int nombreItems;

        public Producteur(String nom, ProducteurConsommateur pc, int nombreItems) {
            this.nom = nom;
            this.pc = pc;
            this.nombreItems = nombreItems;
        }

        @Override
        public void run() {
            for (int i = 0; i < nombreItems; i++) {
                int produit = genererProduit();

                try {
                    // Attendre une place libre
                    pc.placesLibres.acquire();
                    // Acquérir l'accès exclusif au buffer
                    pc.mutexBuffer.acquire();

                    // Ajouter l'élément au buffer
                    pc.ajouterElement(produit);
                    System.out.println(nom + " produit " + produit + " -> " + pc.etatBuffer());

                    // Libérer l'accès au buffer
                    pc.mutexBuffer.release();
                    // Signaler qu'un élément est disponible
                    pc.elementsDisponibles.release();

                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    static class Consommateur implements Runnable {

        private final String nom;
        private final ProducteurConsommateur pc;
        private final int nombreItems;

        public Consommateur(String nom, ProducteurConsommateur pc, int nombreItems) {
            this.nom = nom;
            this.pc = pc;
            this.nombreItems = nombreItems;
        }

        @Override
        public void run() {
            for (int i = 0; i < nombreItems; i++) {
                try {
                    // Attendre qu'un élément soit disponible
                    pc.elementsDisponibles.acquire();
                    // Acquérir l'accès exclusif au buffer
                    pc.mutexBuffer.acquire();

                    // Retirer un élément du buffer
                    int produit = pc.retirerElement();
                    System.out.println(nom + " consomme " + produit + " -> " + pc.etatBuffer());

                    // Libérer l'accès au buffer
                    pc.mutexBuffer.release();
                    // Signaler qu'une place est libre
                    pc.placesLibres.release();

                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}