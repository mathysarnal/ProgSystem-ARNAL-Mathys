import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.Queue;

private static final int TAILLE_BUFFER = 5;
private final Integer[] buffer = new Integer[TAILLE_BUFFER];

// TODO: Déclarer 3 sémaphores avec les bonnes valeurs initiales
private final Semaphore placesLibres = new Semaphore(TAILLE_BUFFER);
private final Semaphore elementsDisponibles = new Semaphore(0);
private final Semaphore mutexBuffer = new Semaphore(1); // quel erreur ici ? vous pouvez le résoudre plus tard
                                                        // Inutile d'utiliser un sémaphore

class Producteur implements Runnable {
    public void run() {
        for (int i = 0; i < 10; i++) {
            int produit = genererProduit();

            placesLibres.acquire();
            elementsDisponibles.acquire();
            mutexBuffer.acquire();
            // TODO: Acquérir l'accès exclusif au buffer
            // TODO: Ajouter l'élément au buffer
            // TODO: Libérer l'accès au buffer
            // TODO: Signaler qu'un élément est disponible
        }
    }
}