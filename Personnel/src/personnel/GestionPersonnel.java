package personnel;

import java.io.Serializable;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class GestionPersonnel implements Serializable {

    private static final long serialVersionUID = -105283113987886425L;
    private static GestionPersonnel gestionPersonnel = null;
    private SortedSet<Ligue> ligues;
    private Employe root;

    public final static int SERIALIZATION = 1, JDBC = 2, TYPE_PASSERELLE = SERIALIZATION;
    private static Passerelle passerelle = TYPE_PASSERELLE == JDBC ? new jdbc.JDBC() : new serialisation.Serialization();

    private GestionPersonnel() {
        if (gestionPersonnel != null)
            throw new RuntimeException("Une seule instance autorisée.");

        ligues = new TreeSet<>();
        // Utiliser le constructeur spécial root
        root = new Employe(this, "root", "", "", "toor");

        gestionPersonnel = this;
    }

    public static GestionPersonnel getGestionPersonnel() {
        if (gestionPersonnel == null) {
            gestionPersonnel = passerelle.getGestionPersonnel();
            if (gestionPersonnel == null)
                gestionPersonnel = new GestionPersonnel();
        }
        return gestionPersonnel;
    }

    public Employe getRoot() { return root; }

    public SortedSet<Ligue> getLigues() {
        return Collections.unmodifiableSortedSet(ligues);
    }

    public Ligue addLigue(String nom) throws SauvegardeImpossible {
        Ligue ligue = new Ligue(this, nom);
        ligues.add(ligue);
        sauvegarder();
        return ligue;
    }

    public void remove(Ligue ligue) { ligues.remove(ligue); }

    public void sauvegarder() throws SauvegardeImpossible {
        passerelle.sauvegarderGestionPersonnel(this);
    }

    int insert(Ligue ligue) throws SauvegardeImpossible {
        return passerelle.insert(ligue);
    }
}
