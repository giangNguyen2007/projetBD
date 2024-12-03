package org.example;

import java.sql.*;

public class Utilisateur {

    private String utilisateur = "UTILISATEUR";

    void printResult(ResultSet r) throws SQLException {
        while (r.next()) {
            System.out.println(
                    "email: " + r.getString(1) +
                    " Nom: " + r.getString(2) +
                    " adresse: " + r.getString(3));
        }
        r.close();
    }

    public void findAllUtilisateurs() throws SQLException {
        Connection connection = MyConnection.getConnection();

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM UTILISATEUR");

        this.printResult(resultSet);

        statement.close();
        connection.close();

    }



    public void insertUtilisateur(String email, String nom, String prenom, String addresse) throws SQLException {
        Connection connection = MyConnection.getConnection();

        assert connection != null;

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO UTILISATEUR VALUES (?, ?, ?, ?)");
        //preparedStatement.setString(1, this.utilisateur);
        preparedStatement.setString(1, email);
        preparedStatement.setString(2, nom);
        preparedStatement.setString(3, prenom);
        preparedStatement.setString(4, addresse);

        int res = preparedStatement.executeUpdate();

        if (res > 0) {
            System.out.printf("Insertion success");
        }

        preparedStatement.close();
        connection.close();
    }
}
