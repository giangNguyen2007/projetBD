package org.example.test;

import org.example.Utility;

import java.sql.SQLException;

public class testUtility {
    public static void main(String[] args) {

        try {
            int i = Utility.findNbOffreOfUser(1, "gianggmail");
            System.out.printf("nb user = %d ", i);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
