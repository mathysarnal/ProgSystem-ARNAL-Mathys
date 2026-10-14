public class CalculParallele {
    private static final int[] DONNEES = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private static final int MULTIPLICATEUR = 1000;
    private static final int INDEX = 10;

    static Calcule[] threads = new Calcule[DONNEES.length];

    public static void main(String[] args) {
        System.out.println("=== Calcul Parallèle ===");

        long debut = System.nanoTime();
        long sommeTotal = 0;

        for (int i = 0; i < DONNEES.length; i++) {
            threads[i] = new Calcule(DONNEES[i], MULTIPLICATEUR, INDEX);
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long duree = (System.nanoTime() - debut) / 1_000_000;
        System.out.println("Résultat total : " + sommeTotal);
        System.out.println("Durée : " + duree + " ms");
    }
}