package personnel;

import java.io.Serializable;
import java.time.LocalDate;

public class Employe implements Serializable, Comparable<Employe> {

    private static final long serialVersionUID = 4795721718037994734L;
    private String nom, prenom, password, mail;
    private Ligue ligue;
    private GestionPersonnel gestionPersonnel;

    private LocalDate dateArrivee;
    private LocalDate dateDepart;

    // Constructeur standard avec dates
    public Employe(GestionPersonnel gestionPersonnel, Ligue ligue, String nom, String prenom,
                   String mail, String password, LocalDate dateArrivee, LocalDate dateDepart) {
        this.gestionPersonnel = gestionPersonnel;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.mail = mail;
        this.ligue = ligue;

        if (dateArrivee == null) dateArrivee = LocalDate.now();
        if (dateDepart != null && dateDepart.isBefore(dateArrivee)) {
            throw new IllegalArgumentException("La date de départ ne peut pas être avant la date d'arrivée");
        }

        this.dateArrivee = dateArrivee;
        this.dateDepart = dateDepart;
    }

    // Constructeur simple (sans dates)
    public Employe(GestionPersonnel gestionPersonnel, Ligue ligue, String nom, String prenom,
                   String mail, String password) {
        this(gestionPersonnel, ligue, nom, prenom, mail, password, LocalDate.now(), null);
    }

    // Constructeur spécial root (ligue et dates null)
    public Employe(GestionPersonnel gestionPersonnel, String nom, String prenom,
                   String mail, String password) {
        this.gestionPersonnel = gestionPersonnel;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.password = password;
        this.ligue = null;
        this.dateArrivee = null;
        this.dateDepart = null;
    }

    // === Getters et setters ===
    public LocalDate getDateArrivee() { return dateArrivee; }
    public void setDateArrivee(LocalDate dateArrivee) {
        this.dateArrivee = dateArrivee;
        if (dateDepart != null && dateDepart.isBefore(dateArrivee)) {
            throw new IllegalArgumentException("La date de départ ne peut pas être avant la date d'arrivée");
        }
    }

    public LocalDate getDateDepart() { return dateDepart; }
    public void setDateDepart(LocalDate dateDepart) {
        if (dateDepart != null && dateDepart.isBefore(this.dateArrivee)) {
            throw new IllegalArgumentException("La date de départ ne peut pas être avant la date d'arrivée");
        }
        this.dateDepart = dateDepart;
    }

    public boolean estAdmin(Ligue ligue) { return ligue.getAdministrateur() == this; }
    public boolean estRoot() { return gestionPersonnel.getRoot() == this; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public boolean checkPassword(String password) { return this.password.equals(password); }
    public void setPassword(String password) { this.password = password; }

    public Ligue getLigue() { return ligue; }

    public void remove() {
        Employe root = gestionPersonnel.getRoot();
        if (this != root) {
            if (estAdmin(getLigue()))
                getLigue().setAdministrateur(root);
            getLigue().remove(this);
        } else
            throw new ImpossibleDeSupprimerRoot();
    }

    @Override
    public int compareTo(Employe autre) {
        int cmp = getNom().compareTo(autre.getNom());
        if (cmp != 0) return cmp;
        return getPrenom().compareTo(autre.getPrenom());
    }

    @Override
    public String toString() {
        String res = nom + " " + prenom + " " + mail + " (";
        if (estRoot()) res += "super-utilisateur";
        else if (ligue != null) res += ligue.toString();
        res += ")";
        return res;
    }
}
