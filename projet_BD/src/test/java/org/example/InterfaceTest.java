package org.example;

import org.example.exceptions.CustomInputException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class InterfaceTest {

    void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    @Test
    void testInsertNewOffre_OverPrice() {

        MyInterface myInterface = new MyInterface();

        MyInterface.email = "johndoe@gmail.com";

        int idVente = 43;
        try {
            Vente v = Vente.chercheVenteParId(idVente);
            //v.insertNewOffre(MyInterface.email, 130, 2);

            MyInterface.email = "paulmartin@hotmail.com";

            v.insertNewOffre(MyInterface.email, 130, 2);

        } catch (SQLException | CustomInputException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void testInsertNewProduit() {

        MyInterface myInterface = new MyInterface();

        MyInterface.email = "johndoe@gmail.com";

        try {

            Utility.insertNewCategorie("Fashio", "jeux, consoles, cartes...");
            Utility.insertNewProduit(MyInterface.email, "PlayStation", 300, 100, "JeuxVideo");

        } catch (SQLException | CustomInputException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void testInserNewCategorie() {


        try {

            Utility.insertNewCategorie("Fashion", "clothes, dresses and accessories...");

        } catch (SQLException | CustomInputException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void testInsertNewSalle() {

        MyInterface myInterface = new MyInterface();

        MyInterface.email = "johndoe@gmail.com";

        try {

            Utility.insertNewSalle("Fashion");

        } catch (SQLException | CustomInputException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void testAfficherSalle() {

        MyInterface myInterface = new MyInterface();

        MyInterface.email = "johndoe@gmail.com";

        try {

            Utility.afficherSalles();

        } catch (SQLException | CustomInputException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void testCalculGagnant() {

        try {
            Vente vente = Vente.chercheVenteParId(43);
            vente.calculGagnant();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void testDeleteVente() throws SQLException {

        Connection myCon = MyConnection.getConnection();

        try {
            Utility.deleteVente(myCon, 43);
            Utility.commitAndCloseConnection(myCon);

        } catch (SQLException e) {
            myCon.rollback();
            throw new RuntimeException(e);
        }

    }

    @Test
    void testCheckProduitEtSalle() throws SQLException {

        Connection myCon = MyConnection.getConnection();

        try {

            Utility.commitAndCloseConnection(myCon);

        } catch (SQLException e) {
            myCon.rollback();
            throw new RuntimeException(e);
        }

    }

    @Test
    void testInsertNewVente() throws SQLException {

        try {

            Utility.insertNewVente(200, "Y", "M", 20, 3, 22, Boolean.TRUE);

        } catch (SQLException e) {

            throw new RuntimeException(e);
        }

    }


}