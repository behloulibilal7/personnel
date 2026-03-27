package jdbc;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        catch (ClassNotFoundException e)
        {
            System.out.println("Pilote JDBC non installé.");
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
    }

    @Override
    public GestionPersonnel getGestionPersonnel() 
    {
        GestionPersonnel gestionPersonnel = new GestionPersonnel(this);

        try 
        {
            //  Charger toutes les ligues 
            String requeteLigue = "SELECT * FROM ligue";
            Statement stmtLigue = connection.createStatement();
            ResultSet rsLigue = stmtLigue.executeQuery(requeteLigue);
            while (rsLigue.next())
                gestionPersonnel.addLigue(rsLigue.getInt("id"), rsLigue.getString("nom"));

            //  Charger le root
            String requeteRoot = "SELECT * FROM employe WHERE id_ligue IS NULL LIMIT 1";
            Statement stmtRoot = connection.createStatement();
            ResultSet rsRoot = stmtRoot.executeQuery(requeteRoot);
            if (rsRoot.next())
            {
                gestionPersonnel.addRoot(
                    rsRoot.getInt("id"),
                    rsRoot.getString("nom"),
                    rsRoot.getString("prenom"),
                    rsRoot.getString("mail"),
                    rsRoot.getString("password")
                );
            }
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }

        return gestionPersonnel;
    }

    @Override
    public void sauvegarderGestionPersonnel(GestionPersonnel gestionPersonnel) throws SauvegardeImpossible 
    {
        close();
    }

    public void close() throws SauvegardeImpossible
    {
        try
        {
            if (connection != null)
                connection.close();
        }
        catch (SQLException e)
        {
            throw new SauvegardeImpossible(e);
        }
    }

    @Override
    public int insert(Ligue ligue) throws SauvegardeImpossible 
    {
        try 
        {
            PreparedStatement instruction;
            instruction = connection.prepareStatement("insert into ligue (nom) values(?)", Statement.RETURN_GENERATED_KEYS);
            instruction.setString(1, ligue.getNom());        
            instruction.executeUpdate();
            ResultSet id = instruction.getGeneratedKeys();
            id.next();
            return id.getInt(1);
        } 
        catch (SQLException exception) 
        {
            exception.printStackTrace();
            throw new SauvegardeImpossible(exception);
        }        
    }

    //  Méthode insert(Employe) 
    @Override
    public int insert(Employe employe) throws SauvegardeImpossible
    {
        try
        {
            PreparedStatement instruction = connection.prepareStatement(
                "INSERT INTO employe (nom, prenom, mail, password, id_ligue) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            instruction.setString(1, employe.getNom());
            instruction.setString(2, employe.getPrenom());
            instruction.setString(3, employe.getMail());
            instruction.setString(4, employe.getPassword());
            if (employe.getLigue() != null)
                instruction.setInt(5, employe.getLigue().getId());
            else
                instruction.setNull(5, java.sql.Types.INTEGER); // root n’a pas de ligue

            instruction.executeUpdate();
            ResultSet id = instruction.getGeneratedKeys();
            id.next();
            return id.getInt(1);
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            throw new SauvegardeImpossible(exception);
        }
    }
}
//Implementation de la methode update 
@Override
public void update(Ligue ligue) throws SauvegardeImpossible 
{
    try 
    {
        PreparedStatement instruction;
        instruction = connection.prepareStatement(
            "UPDATE ligue SET nom = ? WHERE id = ?"
        );
        instruction.setString(1, ligue.getNom());
        instruction.setInt(2, ligue.getId());
        instruction.executeUpdate();
    } 
    catch (SQLException exception) 
    {
        exception.printStackTrace();
        throw new SauvegardeImpossible(exception);
    }       
}