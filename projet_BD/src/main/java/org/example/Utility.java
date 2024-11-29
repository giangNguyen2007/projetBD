package org.example;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

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

    public static int findTotalQuteEnVenteProduit(int idProduit) throws SQLException {

        Connection connection = MyConnection.getConnection();

        assert connection != null;

        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT SUM (QUANTITELOT) FROM VENTE WHERE IDPRODUIT = ?"
        );
        preparedStatement.setInt(1, idProduit);
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

    public static void afficheToutesVentes() throws SQLException {

        ArrayList<Integer> allVenteId = Utility.getToutesVenteId();

        Vente myVente = null;

        System.out.printf("Afficher toutes ventes : \n");

        for (Integer venteId : allVenteId) {
            myVente = Vente.chercheVenteParId(venteId);
            System.out.printf("\n");
            System.out.printf(myVente.toString());
        }

    }

    private static ArrayList<Integer> getToutesVenteId() throws SQLException {

        Connection connection = MyConnection.getConnection();

        assert connection != null;

        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT IDVENTE FROM VENTE"
        );


        ResultSet resultSet = preparedStatement.executeQuery();

        // if user has no offre => resulset = NUll
        if (!resultSet.isBeforeFirst()){
            Utility.closeConnection(connection, preparedStatement, resultSet);
            System.out.printf("Aucun vente en cours en ce moment \n");
        }

        ArrayList<Integer> res = new ArrayList<>();

        while(resultSet.next()){
            //System.out.printf("Vente id = %d \n", resultSet.getInt(1));
            res.add(resultSet.getInt(1));
        }

        Utility.closeConnection(connection, preparedStatement, resultSet);

        return res;

    }

    public static Boolean insertNewVente(int prixDepart, String revocabilitty, String nbOffre, int quteLot, int idSalle, int idProduit) throws SQLException {
        Connection connection = MyConnection.getConnection();

        Boolean insertionSuccess = Boolean.FALSE;

        assert connection != null;

        LocalDateTime now = LocalDateTime.now();

        PreparedStatement updateDateStatement = connection.prepareStatement(
                "INSERT INTO DATEHEURE VALUES (?)"
        );

        updateDateStatement.setTimestamp(1, Timestamp.valueOf(now));
        int res1 = updateDateStatement.executeUpdate();

        if (res1 > 0) {
            System.out.printf("OFFRE INSERTION : Update Dateheure table with current date heure success \n");
        }


        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO VENTE (PRIXDEPART, REVOCABILITE, NBREOFFRE, QUANTITELOT, IDSALLEDEVENTE, IDPRODUIT, DATEHEURE)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)"
        );
        preparedStatement.setFloat(1, prixDepart);
        preparedStatement.setString(2, revocabilitty);
        preparedStatement.setString(3, nbOffre);
        preparedStatement.setInt(4, quteLot);
        preparedStatement.setInt(5, idSalle);
        preparedStatement.setInt(6, idProduit);
        preparedStatement.setTimestamp(7, Timestamp.valueOf(now));


        int res = preparedStatement.executeUpdate();

        if (res > 0) {
            System.out.printf("VENTE INSERTION : Insertion new offer success");
        }


        if (res1 > 0 && res > 0){
            insertionSuccess = Boolean.TRUE;
        }

        preparedStatement.close();
        updateDateStatement.close();
        connection.close();

        return insertionSuccess;
    }

    public static void deleteVente(int idVente) throws SQLException {
        Connection connection = MyConnection.getConnection();

        Boolean insertionSuccess = Boolean.FALSE;

        assert connection != null;

        PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM VENTE WHERE IDVENTE = ?"
        );
        preparedStatement.setInt(1, idVente);

        int res = preparedStatement.executeUpdate();

        if (res > 0) {
            System.out.printf("Delete operation reussie");
        }

        preparedStatement.close();
        connection.close();

    }

    public static void updateProduitStock(int quantite, int produitId) throws SQLException {
        Connection connection = MyConnection.getConnection();

        Boolean insertionSuccess = Boolean.FALSE;

        assert connection != null;

        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE PRODUIT SET STOCK = ?  WHERE IDPRODUIT = ?"
        );
        preparedStatement.setInt(1, quantite);
        preparedStatement.setInt(2, produitId);

        int res = preparedStatement.executeUpdate();

        if (res > 0) {
            System.out.printf("Update produit stock reussie");
        }

        preparedStatement.close();
        connection.close();

    }

    public static void getProduitsParUser(String email) throws SQLException {

        Connection connection = MyConnection.getConnection();

        assert connection != null;

        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM PRODUIT WHERE EMAIL = ?"
        );

        preparedStatement.setString(1, email);


        ResultSet resultSet = preparedStatement.executeQuery();

        // if user has no offre => resulset = NUll
        if (!resultSet.isBeforeFirst()){
            Utility.closeConnection(connection, preparedStatement, resultSet);
            System.out.printf("Aucun produit dans votre catalogue \n");
        }

        System.out.printf("Afficher tous produits de %s \n", email);
        while(resultSet.next()){
            //System.out.printf("Vente id = %d \n", resultSet.getInt(1));
            System.out.printf("Produit id %d : %s , prix de revient = %d, stock = %d \n",
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getInt(3),

                    resultSet.getInt(4)
            );

        }

        Utility.closeConnection(connection, preparedStatement, resultSet);

    }

}
