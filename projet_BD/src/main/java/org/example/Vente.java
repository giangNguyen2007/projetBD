package org.example;

import org.example.exceptions.CustomInputException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public abstract class Vente {

    public int idVente;
    public int prixDepart;

    public Boolean multipleOffreParPersonne; // Valeur M ou U

    public Boolean Revocabilite;
    public int qteLot;
    public int idSalleDeVente;

    public int idProduit;

    public LocalDateTime dateDepot;

    public VenteCategorie categorie;

    public int prixDeRevient;

    public ArrayList<Offre> list_offres;

    public Vente(int idVente, int prixDepart, Boolean revocabilite, String nbOffreParPersonne, int qteLot, int idSalleDeVente, int idProduit, LocalDateTime dateDepot) {
        this.idVente = idVente;
        this.prixDepart = prixDepart;
        this.Revocabilite = revocabilite;
        this.multipleOffreParPersonne = (Objects.equals(nbOffreParPersonne, "M"));
        this.qteLot = qteLot;
        this.idSalleDeVente = idSalleDeVente;
        this.idProduit = idProduit;
        this.dateDepot = dateDepot;
        this.list_offres = new ArrayList<>();

        try {
            this.getInfoProduit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void printResult(ResultSet r) throws SQLException {
        while (r.next()) {
            System.out.printf("idVente %s PrixDepart: %s Date = %s\n", r.getString(1), r.getString(2), r.getString(8));

            Timestamp vente_date = r.getTimestamp(8);
            LocalDateTime d = vente_date.toLocalDateTime();

            System.out.printf(" Date = %s" , d);
        }

        r.close();
    }

    public static void chercheTousVentesParProduit(int idProduit) throws SQLException {
        Connection connection = MyConnection.getConnection();

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM VENTE WHERE IDPRODUIT = " + idProduit);

        assert(resultSet != null);
        Vente.printResult(resultSet);

        statement.close();
        connection.close();

    }

    public static Vente chercheVenteParId(int idVente) throws SQLException {
        Connection connection = MyConnection.getConnection();

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM VENTE WHERE IDVENTE = " + idVente);

        assert(resultSet != null);

        VenteCategorie venteCategorie;
        Vente vente;

        if (!resultSet.isBeforeFirst()){
            System.out.printf("No vente trouve avec id correspondant");
            Utility.closeConnection(connection, statement, resultSet);
            return null;
        }

        resultSet.next();

        if ( Utility.estVenteMontante(idVente))
        {
            vente = new VenteMontante(
                    resultSet.getInt(1),   // idVente
                    resultSet.getInt(2),  // prix Depart
                    resultSet.getBoolean(3),
                    resultSet.getString(4),
                    resultSet.getInt(5), // Qte lot
                    resultSet.getInt(6), // id salle de vente
                    resultSet.getInt(7),
                    resultSet.getTimestamp(8).toLocalDateTime()// idProduit
            );
        } else {
            vente = new VenteDescendante(
                    resultSet.getInt(1),   // idVente
                    resultSet.getInt(2),  // prix Depart
                    resultSet.getBoolean(3),
                    resultSet.getString(4), // multiple ou sing offre par personne
                    resultSet.getInt(5), // Qte lot
                    resultSet.getInt(6), // id salle de vente
                    resultSet.getInt(7),
                    resultSet.getTimestamp(8).toLocalDateTime()// idProduit
            );
        }

        System.out.printf(" Vente found = %s \n", vente.toString());

        Utility.closeConnection(connection, statement, resultSet);
        return vente;
    }

    public abstract void calculGagnant() throws SQLException;


    public abstract int getCurrentPrix() throws SQLException;



    public void chercheTousOffresParVente() throws SQLException {
        Connection connection = MyConnection.getConnection();

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM OFFRE WHERE IDVENTE = " + this.idVente);

        assert(resultSet != null);

        if (resultSet.isBeforeFirst()){
            this.loadOffre(resultSet);

        } else {
            System.out.printf("No Offre pour ce Vente actuellement \n");
        }

        statement.close();
        connection.close();

    }



    public void getInfoProduit() throws SQLException {
        Connection connection = MyConnection.getConnection();

        assert connection != null;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM PRODUIT WHERE IDPRODUIT = (?)");

        preparedStatement.setInt(1, idProduit);

        ResultSet resultSet = preparedStatement.executeQuery();

        // if no produit trouve
        if (!resultSet.isBeforeFirst()){
            throw new RuntimeException("Aucun Produit trouve pour cette Vente");
        };

        resultSet.next();

        this.prixDeRevient = resultSet.getInt(3);

        System.out.printf("Produit du vente: %s at prix de revient = %d , stock = %d \n", resultSet.getString(2), resultSet.getInt(3), resultSet.getInt(4));

        Utility.closeConnection(connection, preparedStatement, resultSet);

    }

    void loadOffre(ResultSet r) throws SQLException {
        while (r.next()) {
            Timestamp date_offre = r.getTimestamp(2);
            LocalDateTime d = date_offre.toLocalDateTime();

            Offre newOffre = new Offre(
                    r.getInt(1),   // idVente
                    d,    // Date offre
                    r.getString(3),   // email
                    r.getInt(4),
                    r.getInt(5)
            );

            this.list_offres.add(newOffre);

            System.out.printf(newOffre.toString());
            System.out.printf(" \n");
        }
        r.close();
    }

    public abstract Boolean insertNewOffre(String email, int prixOffre, int qteAchat) throws SQLException, CustomInputException;

    @Override
    public String toString() {
        return "Vente{" +
                "idVente=" + idVente +
                ", prixDepart=" + prixDepart +
                ", Revocabilite=" + Revocabilite +
                ", qteLot=" + qteLot +
                ", idSalleDeVente=" + idSalleDeVente +
                ", idProduit=" + idProduit +
                ", dateDepot=" + dateDepot +
                ", categorie=" + categorie +
                '}';
    }
}
