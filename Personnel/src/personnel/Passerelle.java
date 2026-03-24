public interface Passerelle 
{
    public GestionPersonnel getGestionPersonnel();
    public void sauvegarderGestionPersonnel(GestionPersonnel gestionPersonnel) throws SauvegardeImpossible;
    public int insert(Ligue ligue) throws SauvegardeImpossible;
	// nouvelle méthode ajoutée pour insérer un employé dans la base de données
    public int insert(Employe employe) throws SauvegardeImpossible; 
}