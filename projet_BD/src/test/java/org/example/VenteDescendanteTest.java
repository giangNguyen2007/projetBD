package org.example;

import org.example.exceptions.CustomInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class VenteDescendanteTest {

    private Vente venteDescendante;

    @BeforeEach
    void setUp() {

        try {
            venteDescendante = Vente.chercheVenteParId(2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    // throw error because single offre per user only
    void insertNewOffre_doubleUser() {

        try {
            venteDescendante.insertNewOffre("janedoe3@yahoo.com", 1000, 45);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (CustomInputException e) {
            throw new RuntimeException(e);
        }

    }
}