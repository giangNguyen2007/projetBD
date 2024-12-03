package org.example;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    @Test
    void findTotalQteAchatPerUser_Case1() {

        int res = 0;
        try {
            res = Utility.findTotalQteAchatPerUser(1, "giang.nguyen@gmail");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        assertEquals(6, res);

    }

    @Test
    void findTotalQteAchatPerUser_Case2_NoUser() {

        int res = 0;
        try {
            res = Utility.findTotalQteAchatPerUser(1, "giang2.nguyen@gmail");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

//    @Test
//    void testInsertNewVente() {
//
//        try {
//            Utility.insertNewVente(100, "Y", "M", 100, 1, 1);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//    }


    @Test
    void testTotalProduitEnVente() {

        try {
            int total = Utility.findTotalQuteEnVenteProduit(1);
            System.out.printf("Total du produit 1 = %d \n", total);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}