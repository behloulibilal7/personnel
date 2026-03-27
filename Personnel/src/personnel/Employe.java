package personnel;

import java.io.Serializable;

public class Employe implements Serializable, Comparable<Employe>
{
    private static final long serialVersionUID = 4795721718037994734L;
    
    private int id = -1; // id pour la BDD
    private String nom, prenom, password, mail;
    private Ligue ligue;
    private GestionPersonnel gestionPersonnel;

    // Constructeur normal pour créer un nouvel employé
    Employe(GestionPersonnel gestionPersonnel, Ligue ligue, String nom, String prenom, String mail, String password)
    {
        this.gestionPersonnel = gestionPersonnel;
        this.ligue = ligue;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.password = password;
    }

    //  Constructeur pour charger depuis BDD 
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

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Ligue getLigue() { return ligue; }

    public boolean estRoot() { return gestionPersonnel.getRoot() == this; }

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
        if (estRoot())
            res += "super-utilisateur";
        else
            res += ligue.toString();
        return res + ")";
    }
}