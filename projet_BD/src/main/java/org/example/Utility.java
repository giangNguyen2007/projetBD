package org.example;

import java.sql.*;

public class Utility {

    public static void closeConnection(Connection c, Statement s, ResultSet r) throws SQLException {
        c.close();
        s.close();
        r.close();
    }

    public static void closeConnection(Connection c, Statement s) throws SQLException {
        c.close();
        s.close();
    }

    public static Boolean estVenteMontante(int idVente) throws SQLException {

        Connection connection = MyConnection.getConnection();

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM VENTEMONTANTE WHERE IDVENTE = " + idVente);

        if(resultSet.isBeforeFirst()){

            Utility.closeConnection(connection, statement, resultSet);
            return Boolean.TRUE;
        } else {

            Utility.closeConnection(connection, statement, resultSet);
            return Boolean.FALSE;
        }

    }

    public static int findNbOffreOfUser(int idVente, String email) throws SQLException {

        Connection connection = MyConnection.getConnection();

        assert connection != null;

        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT COUNT (*) FROM OFFRE WHERE IDVENTE = ? AND EMAIL = ?"
        );

        preparedStatement.setInt(1, idVente);
        preparedStatement.setString(2, email);

        ResultSet resultSet = preparedStatement.executeQuery();

        resultSet.next();

        int res = resultSet.getInt(1);

        Utility.closeConnection(connection, preparedStatement, resultSet);

        return res;
    }

    public static int findTotalQteAchatPerUser(int idVente, String email) throws SQLException {

        Connection connection = MyConnection.getConnection();

        assert connection != null;

        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT SUM (QUANTITEACHAT) FROM OFFRE WHERE IDVENTE = ? AND EMAIL = ?"
        );

        preparedStatement.setInt(1, idVente);
        preparedStatement.setString(2, email);

        ResultSet resultSet = preparedStatement.executeQuery();

        // if user has no offre => resulset = NUll
        if (!resultSet.isBeforeFirst()){
            Utility.closeConnection(connection, preparedStatement, resultSet);
            return 0;
        }

        resultSet.next();

        int res = resultSet.getInt(1);

        Utility.closeConnection(connection, preparedStatement, resultSet);

        return res;
    }
}
