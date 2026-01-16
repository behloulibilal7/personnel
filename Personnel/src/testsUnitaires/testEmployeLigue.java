package testsUnitaires;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import personnel.*;
import java.time.LocalDate;

class testEmployeLigueComplet {

    GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();

    // ===== Tests dates d'arrivée et de départ =====
    @Test
    void testAjoutEmployeAvecDatesValides() throws SauvegardeImpossible {
        Ligue ligue = gestionPersonnel.addLigue("Basketball");
        LocalDate arrivee = LocalDate.of(2026, 1, 15);
        LocalDate depart = LocalDate.of(2026, 6, 30);

        Employe emp = ligue.addEmploye("Martin", "Alice", "a.martin@gmail.com", "password123", arrivee, depart);

        assertEquals(arrivee, emp.getDateArrivee());
        assertEquals(depart, emp.getDateDepart());
        assertEquals(emp, ligue.getEmployes().first());
    }

    @Test
    void testAjoutEmployeSansDateDepart() throws SauvegardeImpossible {
        Ligue ligue = gestionPersonnel.addLigue("Football");
        LocalDate arrivee = LocalDate.of(2026, 2, 1);

        Employe emp = ligue.addEmploye("Durand", "Paul", "p.durand@gmail.com", "secret", arrivee, null);

        assertEquals(arrivee, emp.getDateArrivee());
        assertNull(emp.getDateDepart());
    }

    @Test
    void testExceptionSiDepartAvantArrivee() throws SauvegardeImpossible {
        Ligue ligue = gestionPersonnel.addLigue("Volley");
        LocalDate arrivee = LocalDate.of(2026, 3, 1);
        LocalDate depart = LocalDate.of(2026, 2, 15);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ligue.addEmploye("Petit", "Luc", "l.petit@gmail.com", "mdp456", arrivee, depart);
        });
        assertTrue(exception.getMessage().contains("La date de départ ne peut pas être avant la date d'arrivée"));
    }

    @Test
    void testExceptionSiArriveeNulle() throws SauvegardeImpossible {
        Ligue ligue = gestionPersonnel.addLigue("Tennis");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ligue.addEmploye("Lemoine", "Claire", "c.lemoine@gmail.com", "pass789", null, null);
        });
        assertTrue(exception.getMessage().contains("La date d'arrivée"));
    }

    // ===== Tests des setters =====
    @Test
    void testSettersEmploye() throws SauvegardeImpossible {
        Ligue ligue = gestionPersonnel.addLigue("Handball");
        Employe emp = ligue.addEmploye("Dupont", "Jean", "j.dupont@gmail.com", "mdp123",
                                      LocalDate.of(2026, 1, 10), null);

        emp.setNom("Durand");
        emp.setPrenom("Paul");
        emp.setMail("p.durand@gmail.com");
        emp.setPassword("secret");
        emp.setDateArrivee(LocalDate.of(2026, 1, 15));
        emp.setDateDepart(LocalDate.of(2026, 6, 1));

        assertEquals("Durand", emp.getNom());
        assertEquals("Paul", emp.getPrenom());
        assertEquals("p.durand@gmail.com", emp.getMail());
        assertTrue(emp.checkPassword("secret"));
        assertEquals(LocalDate.of(2026, 1, 15), emp.getDateArrivee());
        assertEquals(LocalDate.of(2026, 6, 1), emp.getDateDepart());
    }

    // ===== Test suppression d'un employé =====
    @Test
    void testRemoveEmploye() throws SauvegardeImpossible {
        Ligue ligue = gestionPersonnel.addLigue("Rugby");
        Employe emp = ligue.addEmploye("Legrand", "Marie", "m.legrand@gmail.com", "pass123",
                                      LocalDate.of(2026, 2, 1), null);

        // Avant suppression
        assertTrue(ligue.getEmployes().contains(emp));

        emp.remove();

        // Après suppression
        assertFalse(ligue.getEmployes().contains(emp));
    }

    @Test
    void testRemoveAdminRevertToRoot() throws SauvegardeImpossible {
        Ligue ligue = gestionPersonnel.addLigue("Natation");
        Employe emp = ligue.addEmploye("Petit", "Luc", "l.petit@gmail.com", "mdp456",
                                      LocalDate.of(2026, 1, 1), null);

        // Rendre emp administrateur
        ligue.setAdministrateur(emp);
        assertEquals(emp, ligue.getAdministrateur());

        // Supprimer emp => root devient admin
        emp.remove();
        assertEquals(gestionPersonnel.getRoot(), ligue.getAdministrateur());
    }

    // ===== Test modification administrateur =====
    @Test
    void testSetAdministrateur() throws SauvegardeImpossible {
        Ligue ligue = gestionPersonnel.addLigue("Escrime");
        Employe emp = ligue.addEmploye("Bernard", "Claire", "c.bernard@gmail.com", "pass123",
                                      LocalDate.of(2026, 3, 1), null);

        // Modifier administrateur vers un employé valide
        ligue.setAdministrateur(emp);
        assertEquals(emp, ligue.getAdministrateur());

        // Essayer de mettre un employé d'une autre ligue => exception
        Ligue autre = gestionPersonnel.addLigue("Boxe");
        Employe emp2 = autre.addEmploye("Durand", "Paul", "p.durand@gmail.com", "secret",
                                       LocalDate.of(2026, 4, 1), null);

        Exception exception = assertThrows(DroitsInsuffisants.class, () -> {
            ligue.setAdministrateur(emp2);
        });
        assertNotEquals(emp2, ligue.getAdministrateur());
    }
}
