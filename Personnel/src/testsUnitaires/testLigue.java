package testsUnitaires;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import personnel.*;

import java.time.LocalDate;

class testEmployeDates {

    GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();

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
}
