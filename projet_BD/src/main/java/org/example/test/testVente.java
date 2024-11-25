package org.example.test;

import org.example.Vente;

import java.sql.SQLException;

public class testVente {
    public static void main(String[] args) throws SQLException {
        //Vente v = new Vente();
//        Vente.chercheTousVentesParProduit(1);

        Vente v = Vente.chercheVenteParId(2);
        v.calculGagnant();
        v.getCurrentPrix();
        //v.chercheTousOffresParVente();
    }
}
