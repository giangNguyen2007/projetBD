package org.example.test;

import org.example.Utilisateur;

import java.sql.SQLException;

public class testUtilisateur {
    public static void main(String[] args) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.findAllUtilisateurs();

        utilisateur.insertUtilisateur("giang.nguyen@gmail", "giang", "nguyen", "Rue Lilas, Grenoble");
    }
}
