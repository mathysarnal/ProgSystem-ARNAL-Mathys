public class Calcule extends Thread {

    private long resultat;
    private int valeur;
    private int multiplicateur;
    private int index;

    public Calcule(int valeur, int multiplicateur, int index) {
        this.valeur = valeur;
        this.multiplicateur = multiplicateur;
        this.index = index;
    }

    @Override
    public void run() {
        // Calcul intensif simulé par des opérations mathématiques
        resultat = 0;
        for (int j = 0; j < multiplicateur; j++) {
            resultat += valeur * valeur + valeur;
                            // on pourrait aussi mettre un sleep
        }
    }

    public long getResultat() {
        return resultat;
    }

    public static void main(String args[]) {
        (new Calcule(10, 1000, 10)).start();
    }
}