Ligue.java:package personnel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class Ligue implements Serializable, Comparable<Ligue> {

    private static final long serialVersionUID = 1L;
    private int id = -1;
    private String nom;
    private SortedSet<Employe> employes;
    private Employe administrateur;
    private GestionPersonnel gestionPersonnel;

    Ligue(GestionPersonnel gestionPersonnel, String nom) throws SauvegardeImpossible {
        this(gestionPersonnel, -1, nom);
        this.id = gestionPersonnel.insert(this); 
    }

    Ligue(GestionPersonnel gestionPersonnel, int id, String nom) {
        this.nom = nom;
        this.gestionPersonnel = gestionPersonnel;
        this.employes = new TreeSet<>();
        this.administrateur = gestionPersonnel.getRoot();
        this.id = id;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Employe getAdministrateur() { return administrateur; }
    public void setAdministrateur(Employe administrateur) {
        Employe root = gestionPersonnel.getRoot();
        if (administrateur != root && administrateur.getLigue() != this)
            throw new DroitsInsuffisants();
        this.administrateur = administrateur;
    }

    public SortedSet<Employe> getEmployes() {
        return Collections.unmodifiableSortedSet(employes);
    }

    // Ajouter un employé simple
    public Employe addEmploye(String nom, String prenom, String mail, String password) {
        Employe emp = new Employe(gestionPersonnel, this, nom, prenom, mail, password);
        employes.add(emp);
        if (administrateur == null) administrateur = emp;
        return emp;
    }

    // Ajouter un employé avec dates
    public Employe addEmploye(String nom, String prenom, String mail, String password,
                              LocalDate dateArrivee, LocalDate dateDepart) {
        if (dateArrivee == null)
            throw new IllegalArgumentException("La date d'arrivée ne peut pas être nulle");
        if (dateDepart != null && dateDepart.isBefore(dateArrivee))
            throw new IllegalArgumentException("La date de départ ne peut pas être avant la date d'arrivée");

        Employe emp = new Employe(gestionPersonnel, this, nom, prenom, mail, password, dateArrivee, dateDepart);
        employes.add(emp);
        if (administrateur == null) administrateur = emp;
        return emp;
    }

    void remove(Employe employe) { employes.remove(employe); }
    public void remove() { gestionPersonnel.remove(this); }

    @Override
    public int compareTo(Ligue autre) { return getNom().compareTo(autre.getNom()); }
    @Override
    public String toString() { return nom; }
}
