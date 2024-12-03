package org.example;

import org.example.exceptions.CustomInputException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Utility {

    public static void commitAndCloseConnection(Connection c, Statement s, ResultSet r) throws SQLException {

        s.close();
        r.close();

        c.commit();
        c.close();
    }

    public static void commitAndCloseConnection(Connection c, Statement s) throws SQLException {
        c.commit();
        s.close();
        c.close();

    }

    public static void commitAndCloseConnection(Connection c) throws SQLException {
        c.commit();
        c.close();

    }

    public static Boolean estVenteMontante(int idVente) throws SQLException {

        Connection connection = MyConnection.getConnection();

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM VENTEMONTANTE WHERE IDVENTE = " + idVente);

        // connection.commit() is called in commitAndCloseConnection function

        if(resultSet.isBeforeFirst()){

            Utility.commitAndCloseConnection(connection, statement, resultSet);
            return Boolean.TRUE;
        } else {

            Utility.commitAndCloseConnection(connection, statement, resultSet);
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

        Utility.commitAndCloseConnection(connection, preparedStatement, resultSet);

        return res;
    }

    public static int getProduitStock(int idProduit) throws SQLException {

        Connection connection = MyConnection.getConnection();

        assert connection != null;

        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT STOCK FROM PRODUIT WHERE IDPRODUIT = ?"
        );

        preparedStatement.setInt(1, idProduit);

        ResultSet resultSet = preparedStatement.executeQuery();

        resultSet.next();

        int res = resultSet.getInt(1);

        Utility.commitAndCloseConnection(connection, preparedStatement, resultSet);

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
            Utility.commitAndCloseConnection(connection, preparedStatement, resultSet);
            return 0;
        }

        resultSet.next();

        int res = resultSet.getInt(1);

        Utility.commitAndCloseConnection(connection, preparedStatement, resultSet);

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
            Utility.commitAndCloseConnection(connection, preparedStatement, resultSet);
            return 0;
        }

        resultSet.next();
        int res = resultSet.getInt(1);
        Utility.commitAndCloseConnection(connection, preparedStatement, resultSet);
        return res;
    }

    public static void afficheToutesVentes() throws SQLException {

        ArrayList<Integer> allVenteId = Utility.getToutesVenteId();

        Vente myVente = null;

        System.out.printf("Afficher toutes ventes : \n");

        for (Integer venteId : allVenteId) {
            myVente = Vente.chercheVenteParId(venteId);
            System.out.println(myVente.printPublic());
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
            Utility.commitAndCloseConnection(connection, preparedStatement, resultSet);
            System.out.printf("Aucun vente en cours en ce moment \n");
        }

        ArrayList<Integer> res = new ArrayList<>();

        while(resultSet.next()){
            //System.out.printf("Vente id = %d \n", resultSet.getInt(1));
            res.add(resultSet.getInt(1));
        }

        Utility.commitAndCloseConnection(connection, preparedStatement, resultSet);

        return res;

    }

    public static Boolean insertNewVente(int prixDepart, String revocabilitty, String nbOffre, int quteLot, int idSalle, int idProduit, Boolean venteMontante) throws SQLException {

        String venteType;
        if (venteMontante) {
            venteType = "VENTEMONTANTE";
        } else {
            venteType = "VENTEDESCENDANTE";
        }

        Connection connection = MyConnection.getConnection();

        Boolean insertionSuccess = Boolean.FALSE;

        assert connection != null;

        try {

            // CHECK PRODUIT CATEGORIE MATCH SALLE CATEGORIE

            Utility.checkProduitEtSalleCategorie(connection, idProduit, idSalle);


            // INSERER NOUVELLE VENTE

            LocalDateTime now = LocalDateTime.now();

            PreparedStatement updateDateStatement = null;

            updateDateStatement = connection.prepareStatement(
                    "INSERT INTO DATEHEURE VALUES (?)"
            );


            updateDateStatement.setTimestamp(1, Timestamp.valueOf(now));
            int res1 = updateDateStatement.executeUpdate();

            if (res1 > 0) {
                System.out.printf("OFFRE INSERTION : Update Dateheure table with current date heure success \n");
            }


            // get current max index

            PreparedStatement max_idVente_stm = connection.prepareStatement(
                    "SELECT MAX(IDVENTE) FROM VENTE"

            );

            ResultSet max_idVente_res = max_idVente_stm.executeQuery();

            max_idVente_res.next();
            int max_idVente = max_idVente_res.getInt(1);

            System.out.printf("current max id est : %d \n", max_idVente);

            // insert into table vente

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO VENTE (IDVENTE, PRIXDEPART, REVOCABILITE, NBREOFFRE, QUANTITELOT, IDSALLEDEVENTE, IDPRODUIT, DATEHEURE)" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );
            preparedStatement.setFloat(1, max_idVente + 1);
            preparedStatement.setFloat(2, prixDepart);
            preparedStatement.setString(3, revocabilitty);
            preparedStatement.setString(4, nbOffre);
            preparedStatement.setInt(5, quteLot);
            preparedStatement.setInt(6, idSalle);
            preparedStatement.setInt(7, idProduit);
            preparedStatement.setTimestamp(8, Timestamp.valueOf(now));


            int res = preparedStatement.executeUpdate();

            if (res > 0) {
                System.out.printf(" 1 - Reussir a inserer nouvelle vente dans table VENTE \n");
                insertionSuccess = Boolean.TRUE;
            }



            // ------------- Get newly created idvente to inserint into Ventemontante / descendante table -------------

//            PreparedStatement search_idVente_stm = connection.prepareStatement(
//                    "SELECT IDVENTE FROM VENTE WHERE " +
//                            "IDPRODUIT = ?" +
//                            "AND QUANTITELOT = ?" +
//                            "AND PRIXDEPART = ?"
//            );
//
//            search_idVente_stm.setInt(1, idProduit);
//            search_idVente_stm.setInt(2, quteLot);
//            search_idVente_stm.setInt(3, prixDepart);
//
//            ResultSet idVente_res = search_idVente_stm.executeQuery();
//
//            idVente_res.next();
//            int idVente = idVente_res.getInt(1);
//
//
//            System.out.printf(" 2 - id du nouvelle vente est %d \n", idVente);


            // --------------- insert new id into Ventemontante/ descendante table -----------------

            PreparedStatement insert_venteId_montante = connection.prepareStatement(
                    "INSERT INTO VENTEMONTANTE VALUES (?)"
            );

            PreparedStatement insert_venteId_descendante = connection.prepareStatement(
                    "INSERT INTO VENTEDESCENDANTE VALUES (?)"
            );

            int insert_res;

            insert_venteId_montante.setInt(1, max_idVente + 1);
            insert_venteId_descendante.setInt(1, max_idVente + 1);

            if (venteMontante) {
                insert_res = insert_venteId_montante.executeUpdate();
            } else {
                insert_res = insert_venteId_descendante.executeUpdate();
            }


            if (res1 > 0 && res > 0 && insert_res > 0) {
                System.out.printf(" Reussir a inserer nouvelle vente avec Id %d dans la table VENTE \n", max_idVente + 1);
                insertionSuccess = Boolean.TRUE;
            }

            commitAndCloseConnection(connection, preparedStatement);

        } catch (SQLException | CustomInputException e) {
            connection.rollback();
            System.out.printf(e.getMessage());
        }

        return insertionSuccess;
    }

    public static void deleteVente(Connection connection, int idVente) throws SQLException {

            assert connection != null;

            // DELTE ALL OFFRES FROM TABLE OFFRE

            PreparedStatement delete_offres_statement = connection.prepareStatement(
                    "DELETE FROM OFFRE WHERE IDVENTE = ?"
            );
            delete_offres_statement.setInt(1, idVente);

            int res = delete_offres_statement.executeUpdate();

            if (res > 0) {
                System.out.printf("Suppression reussi pour toutes offres sur vente id = %d", idVente);
            }

            // DELETE VENTE FROM TABLE VENTE AND VENTEMONTANTE / DESCENDANTE

            PreparedStatement preparedStatement1 = connection.prepareStatement(
                    "DELETE FROM VENTEMONTANTE WHERE IDVENTE = ?"
            );
            preparedStatement1.setInt(1, idVente);

            int res1 = preparedStatement1.executeUpdate();

//            PreparedStatement preparedStatement2 = connection.prepareStatement(
//                    "DELETE FROM VENTEDESCENDANTE WHERE IDVENTE = ?"
//            );
//            preparedStatement1.setInt(1, idVente);

           // int res2 = preparedStatement2.executeUpdate();

            PreparedStatement preparedStatement3 = connection.prepareStatement(
                    "DELETE FROM VENTE WHERE IDVENTE = ?"
            );
            preparedStatement3.setInt(1, idVente);

            int res3 = preparedStatement3.executeUpdate();

            if (res3 > 0) {
                System.out.printf("Delete operation reussie");
            }

            preparedStatement1.close();
            //preparedStatement2.close();
            preparedStatement3.close();

    }

    public static void deleteOffresParVente(int idVente) throws SQLException {
        Connection connection = MyConnection.getConnection();

        try {

            Boolean insertionSuccess = Boolean.FALSE;

            assert connection != null;

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM OFFRE WHERE IDVENTE = ?"
            );
            preparedStatement.setInt(1, idVente);

            int res = preparedStatement.executeUpdate();

            if (res > 0) {
                System.out.printf("Suppression reussi pour toutes offres sur vente id = %d", idVente);
            }

            preparedStatement.close();
            Utility.commitAndCloseConnection(connection);

        } catch (SQLException e){
            connection.rollback();
        }

    }

    public static void updateProduitStock(Connection connection,  int quantite, int produitId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE PRODUIT SET STOCK = ?  WHERE IDPRODUIT = ?"
        );
        preparedStatement.setInt(1, quantite);
        preparedStatement.setInt(2, produitId);

        int res = preparedStatement.executeUpdate();

        if (res > 0) {
            System.out.printf("Update produit stock reussie");
        }
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
            Utility.commitAndCloseConnection(connection, preparedStatement, resultSet);
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

        Utility.commitAndCloseConnection(connection, preparedStatement, resultSet);

    }

    public static void checkAuteur(String email, int idProduit) throws CustomInputException, SQLException {

        Connection connection = MyConnection.getConnection();

        assert connection != null;

        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT EMAIL FROM PRODUIT WHERE IDPRODUIT = ?"
        );

        preparedStatement.setInt(1, idProduit);

        ResultSet resultSet = preparedStatement.executeQuery();

        resultSet.next();

        String auteurProduit = resultSet.getString(1);

        if (!Objects.equals(email, auteurProduit)){
            throw new CustomInputException("Vous n'est pas le proprietaire du produit");
        }

        Utility.commitAndCloseConnection(connection, preparedStatement, resultSet);
    }

    public static void insertNewProduit(String email, String nomProduit, int prixRevient, int stock, String categorie) throws CustomInputException, SQLException {

        Connection connection = MyConnection.getConnection();



        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO PRODUIT (NOMPRODUIT, PRIXREVIENT, STOCK, EMAIL, NOMCATEGORIE)" +
                        "VALUES (?, ?, ?, ?, ?)"
        );
        preparedStatement.setString(1, nomProduit);
        preparedStatement.setInt(2, prixRevient);
        preparedStatement.setInt(3, stock);
        preparedStatement.setString(4, email);
        preparedStatement.setString(5, categorie);


        int res = preparedStatement.executeUpdate();

        if (res > 0){
            System.out.printf("Insertion du nouveau produit reuissi \n");
        }

        preparedStatement.close();

        Utility.commitAndCloseConnection(connection, preparedStatement);
    }

    public static void insertNewCategorie(String categorie, String description) throws CustomInputException, SQLException {

        Connection connection = MyConnection.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO CATEGORIE (NOMCATEGORIE, DESCRIPTION) VALUES (?, ?)"
        );
        preparedStatement.setString(1, categorie);
        preparedStatement.setString(2, description);

        int res = preparedStatement.executeUpdate();

        if (res > 0){
            System.out.printf("Insertion du nouveau categorie reussi \n");
        }

        preparedStatement.close();

        Utility.commitAndCloseConnection(connection, preparedStatement);
    }

    public static void insertNewSalle(String categorie) throws CustomInputException, SQLException {

        try {
            Connection connection = MyConnection.getConnection();

            PreparedStatement getMaxIdStatement = connection.prepareStatement(
                    "SELECT MAX(IDSALLEDEVENTE) FROM SALLEDEVENTE"
            );
            ResultSet resultSet = getMaxIdStatement.executeQuery();
            resultSet.next();

            int idMax = resultSet.getInt(1);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO SALLEDEVENTE (IDSALLEDEVENTE, NOMCATEGORIE) VALUES (?, ?)"
            );
            preparedStatement.setInt(1, idMax + 1);
            preparedStatement.setString(2, categorie);

            int res = preparedStatement.executeUpdate();

            if (res > 0){
                System.out.printf("Insertion de la nouvelle salle de vente reussi \n");
            }

            preparedStatement.close();

            Utility.commitAndCloseConnection(connection, preparedStatement);

        } catch (SQLException e){
            System.out.printf(e.getMessage());
        }

    }

    public static void afficherSalles() throws CustomInputException, SQLException {

        Connection connection = MyConnection.getConnection();

        try {

            assert connection != null;

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM PRODUIT SALLEDEVENTE"
            );


            ResultSet resultSet = preparedStatement.executeQuery();

            // if user has no offre => resulset = NUll
            if (!resultSet.isBeforeFirst()){
                Utility.commitAndCloseConnection(connection, preparedStatement, resultSet);
                System.out.printf("Aucun salle de vente \n");
            }

            System.out.printf("Liste de toutes salles de vente: \n");
            while(resultSet.next()){
                //System.out.printf("Vente id = %d \n", resultSet.getInt(1));
                System.out.printf("Salle id %d : categorie = %s\n",
                        resultSet.getInt(1),
                        resultSet.getString(2)
                );

            }

            Utility.commitAndCloseConnection(connection, preparedStatement, resultSet);

        } catch (SQLException e){
            System.out.printf(e.getMessage());
        }

    }

    public static void checkProduitEtSalleCategorie(Connection connection, int idProduit, int idSalle) throws CustomInputException, SQLException {

            assert connection != null;

            PreparedStatement preparedStatement1 = connection.prepareStatement(
                    "SELECT NOMCATEGORIE FROM SALLEDEVENTE WHERE IDSALLEDEVENTE = " + idSalle
            );

            ResultSet resultSet = preparedStatement1.executeQuery();

            resultSet.next();

            String categorie_salle = resultSet.getString(1);

            PreparedStatement preparedStatement2 = connection.prepareStatement(
                    "SELECT NOMCATEGORIE FROM PRODUIT WHERE IDPRODUIT = " + idProduit
            );

            ResultSet resultSet2 = preparedStatement2.executeQuery();

            resultSet2.next();

            String categorie_produit = resultSet2.getString(1);

            if(! categorie_salle.equals(categorie_produit)){
                throw new CustomInputException("Categorie du produit doit correspond au categorie de la salle de vente");
            }


            preparedStatement1.close();
            preparedStatement2.close();

    }





}
