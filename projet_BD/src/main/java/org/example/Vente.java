package org.example;

import java.sql.*;
import java.time.LocalDateTime;

public class Vente {

    void printResult(ResultSet r) throws SQLException {
        while (r.next()) {
            System.out.printf("idVente %s PrixDepart: %s Date = %s\n",r.getString(1), r.getString(2), r.getString(8));
        }

        Timestamp vente_date = r.getTimestamp(8);

        LocalDateTime d = vente_date.toLocalDateTime();



        r.close();
    }

    public void chercheTousVentesParProduit(int idProduit) throws SQLException {
        Connection connection = MyConnection.getConnection();

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM VENTE WHERE IDPRODUIT = " + idProduit);

        this.printResult(resultSet);

        statement.close();
        connection.close();

    }

    public void insertUtilisateur(String email, String nom, String prenom, String addresse) throws SQLException {
        Connection connection = MyConnection.getConnection();

        assert connection != null;

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ? VALUES (?, ?, ?, ?)");
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
