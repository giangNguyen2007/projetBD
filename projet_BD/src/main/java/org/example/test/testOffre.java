package org.example.test;


import org.example.Offre;

import java.sql.SQLException;

public class testOffre {

    public static void main(String[] args) {
        Boolean res;
        {
            try {
                res = Offre.insertNewOffre(1, "giang.nguyen@gmail", 200, 3);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
