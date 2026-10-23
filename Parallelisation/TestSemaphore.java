public class TestSemaphore {

    public static void main(String[] args) {

        // 2 x 15 = 30 produits, 3 x 10 = 30 consommés : tout s'équilibre
        System.out.println("=== Scénario équilibré ===");
        lancerScenario(2, 15, 3, 10);

        System.out.println();

        // 5 x 10 = 50 produits, 2 x 7 = 14 consommés : les producteurs vont bloquer
        System.out.println("=== Scénario déséquilibré ===");
        lancerScenario(5, 10, 2, 7);
    }

    private static void lancerScenario(int nombreProducteurs, int itemsParProducteur,
                                       int nombreConsommateurs, int itemsParConsommateur) {

        ProducteurConsommateur pc = new ProducteurConsommateur();

        Thread[] producteurs = new Thread[nombreProducteurs];
        Thread[] consommateurs = new Thread[nombreConsommateurs];

        for (int i = 0; i < nombreProducteurs; i++) {
            producteurs[i] = new Thread(
                    new ProducteurConsommateur.Producteur("Producteur-" + i, pc,
                                                        itemsParProducteur)
            );
            // daemon : un producteur bloqué n'empêche pas le programme de se terminer
            producteurs[i].setDaemon(true);
        }

        for (int i = 0; i < nombreConsommateurs; i++) {
            consommateurs[i] = new Thread(
                    new ProducteurConsommateur.Consommateur("Consommateur-" + i, pc,
                                                        itemsParConsommateur)
            );
            consommateurs[i].setDaemon(true);
        }

        for (Thread thread : producteurs) {
            thread.start();
        }
        for (Thread thread : consommateurs) {
            thread.start();
        }

        // On attend les consommateurs, puis les producteurs (1 seconde max chacun)
        for (Thread thread : consommateurs) {
            try {
                thread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        for (Thread thread : producteurs) {
            try {
                thread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        int producteursBloques = 0;
        for (Thread thread : producteurs) {
            if (thread.isAlive()) {
                producteursBloques++;
            }
        }

        System.out.println();
        System.out.println("=== Statistiques ===");
        System.out.println("Produits : " + pc.getNombreProduits()
                            + " / " + (nombreProducteurs * itemsParProducteur));
        System.out.println("Consommés : " + pc.getNombreConsommes()
                            + " / " + (nombreConsommateurs * itemsParConsommateur));
        System.out.println("Producteurs encore bloqués : " + producteursBloques);
    }
}