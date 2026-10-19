public class TestMutex {

    public static void main(String[] args) {

        final int nombreTests = 50;
        final int nombreThreads = 9;
        final int incrementsParThread = 1_111_111;

        final int resultatAttendu = nombreThreads * incrementsParThread;

        int nombreReussites = 0;
        int minimum = Integer.MAX_VALUE;
        int maximum = Integer.MIN_VALUE;

        for (int test = 1; test <= nombreTests; test++) {

            CompteurSecurise.resetCompteur();

            Thread[] threads = new Thread[nombreThreads];

            for (int i = 0; i < nombreThreads; i++) {

                threads[i] = new Thread(
                        new CompteurSecurise.Incrementeur("Thread-" + i,
                                                        incrementsParThread)
                );
            }

            for (Thread thread : threads) {
                thread.start();
            }

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            int resultat = CompteurSecurise.getCompteur();

            if (resultat == resultatAttendu) {
                nombreReussites++;
            }

            if (resultat < minimum) {
                minimum = resultat;
            }

            if (resultat > maximum) {
                maximum = resultat;
            }

            System.out.println("Test " + test + " : " + resultat);
        }

        double pourcentage = nombreReussites * 100.0 / nombreTests;

        System.out.println();
        System.out.println("=== Statistiques Mutex ===");
        System.out.println("Résultat attendu : " + resultatAttendu);
        System.out.println("Tests corrects : " + nombreReussites + "/" 
                            + nombreTests);
        System.out.println("Pourcentage de réussite : " + pourcentage + " %");
        System.out.println("Minimum : " + minimum);
        System.out.println("Maximum : " + maximum);
    }
}