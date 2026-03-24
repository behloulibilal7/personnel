package jdbc;

import java.sql.*;
import personnel.*;

public class JDBC implements Passerelle 
{
    Connection connection;

    public JDBC()
    {
        try
        {
            Class.forName(Credentials.getDriverClassName());
            connection = DriverManager.getConnection(Credentials.getUrl(), Credentials.getUser(), Credentials.getPassword());
        }
        catch (ClassNotFoundException e) { System.out.println("Pilote JDBC non installé."); }
        catch (SQLException e) { System.out.println(e); }
    }

    @Override
    public GestionPersonnel getGestionPersonnel()
    {
        GestionPersonnel gestionPersonnel = new GestionPersonnel();
        try 
        {
            // Récupérer ligues
            String requeteLigue = "SELECT * FROM ligue";
            Statement stmtLigue = connection.createStatement();
            ResultSet rsLigue = stmtLigue.executeQuery(requeteLigue);
            while (rsLigue.next())
                gestionPersonnel.addLigue(rsLigue.getInt("id"), rsLigue.getString("nom"));

            // Récupérer root
            String requeteRoot = "SELECT * FROM employe WHERE id_ligue IS NULL LIMIT 1";
            Statement stmtRoot = connection.createStatement();
            ResultSet rsRoot = stmtRoot.executeQuery(requeteRoot);
            if (rsRoot.next())
            {
                gestionPersonnel.addRoot(
                    rsRoot.getString("nom"),
                    rsRoot.getString("prenom"),
                    rsRoot.getString("mail"),
                    rsRoot.getString("password")
                );
            }
        }
        catch (SQLException | SauvegardeImpossible e) { System.out.println(e); }

        return gestionPersonnel;
    }

    @Override
    public void sauvegarderGestionPersonnel(GestionPersonnel gestionPersonnel) throws SauvegardeImpossible
    {
        close();
    }

    public void close() throws SauvegardeImpossible
    {
        try { if (connection != null) connection.close(); }
        catch (SQLException e) { throw new SauvegardeImpossible(e); }
    }

    @Override
    public int insert(Ligue ligue) throws SauvegardeImpossible
    {
        try
        {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO ligue (nom) VALUES (?)", Statement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, ligue.getNom());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        }
        catch (SQLException e) { throw new SauvegardeImpossible(e); }
    }

    // ---------------- Nouvelle méthode ----------------
    @Override
    public int insert(Employe employe) throws SauvegardeImpossible
    {
        try
        {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO employe (nom, prenom, mail, password, id_ligue) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, employe.getNom());
            stmt.setString(2, employe.getPrenom());
            stmt.setString(3, employe.getMail());
            stmt.setString(4, employe.getPassword());
            stmt.setInt(5, employe.getLigue() != null ? employe.getLigue().getId() : 0); // root n'a pas de ligue
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        }
        catch (SQLException e) { throw new SauvegardeImpossible(e); }
    }
}