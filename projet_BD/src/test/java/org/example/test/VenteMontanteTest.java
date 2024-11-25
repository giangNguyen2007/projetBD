package org.example.test;

import org.example.Vente;
import org.example.exceptions.CustomInputException;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class VenteMontanteTest {

    private Vente vente;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws SQLException {

        vente = Vente.chercheVenteParId(1);

    }

    @org.junit.jupiter.api.Test
    void insertNewOffre_underPrice() throws SQLException, CustomInputException {

        vente.insertNewOffre("giang.nguyen@gmail", 2, 300);
        thrown.expect(CustomInputException.class);

    }

    @org.junit.jupiter.api.Test
    // Quantite achat total depasse lot
    void insertNewOffre_excessiveQteAchat() throws SQLException, CustomInputException {

        vente.insertNewOffre("giang.nguyen@gmail", 700, 5);
        thrown.expect(CustomInputException.class);

    }
}