package personnel;

import java.io.Serializable;

/**
 * Employé d'une ligue hébergée par la M2L.
 * Le root est l’employé qui n’est rattaché à aucune ligue.
 */
public class Employe implements Serializable, Comparable<Employe>
{
    private static final long serialVersionUID = 4795721718037994734L;

    private int id = -1; // nouvel id
    private String nom, prenom, password, mail;
    private Ligue ligue;
    private GestionPersonnel gestionPersonnel;

    // Constructeur pour création normale
    Employe(GestionPersonnel gestionPersonnel, Ligue ligue, String nom, String prenom, String mail, String password)
    {
        this.gestionPersonnel = gestionPersonnel;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.mail = mail;
        this.ligue = ligue;
    }

    // Constructeur pour chargement depuis la base
    Employe(GestionPersonnel gestionPersonnel, Ligue ligue, int id, String nom, String prenom, String mail, String password)
    {
        this.gestionPersonnel = gestionPersonnel;
        this.ligue = ligue;
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.password = password;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public boolean checkPassword(String password) { return this.password.equals(password); }
    public void setPassword(String password) { this.password = password; }

    public Ligue getLigue() { return ligue; }

    public boolean estAdmin(Ligue ligue) { return ligue.getAdministrateur() == this; }
    public boolean estRoot() { return gestionPersonnel.getRoot() == this; }

    public void remove()
    {
        Employe root = gestionPersonnel.getRoot();
        if (this != root)
        {
            if (estAdmin(getLigue()))
                getLigue().setAdministrateur(root);
            getLigue().remove(this);
        }
        else
            throw new ImpossibleDeSupprimerRoot();
    }

    @Override
    public int compareTo(Employe autre)
    {
        int cmp = getNom().compareTo(autre.getNom());
        if (cmp != 0) return cmp;
        return getPrenom().compareTo(autre.getPrenom());
    }

    @Override
    public String toString()
    {
        String res = nom + " " + prenom + " " + mail + " (";
        if (estRoot()) res += "super-utilisateur";
        else res += ligue.toString();
        return res + ")";
    }
}