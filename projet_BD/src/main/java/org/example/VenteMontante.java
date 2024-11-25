package org.example;

import org.example.exceptions.CustomInputException;

import java.sql.*;
import java.time.LocalDateTime;

public class VenteMontante extends Vente{

    public int currentPrixMax;
    public VenteMontante(int idVente, int prixDepart, Boolean revocabilite, String nbOffreParPersonne, int qteLot, int idSalleDeVente, int idProduit, LocalDateTime dateDepot) {
        super(idVente, prixDepart, revocabilite, nbOffreParPersonne, qteLot, idSalleDeVente, idProduit, dateDepot);

        this.categorie = VenteCategorie.MONTANTE;

        try {
            this.currentPrixMax = this.getCurrentPrixMax();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int getCurrentPrixMax() throws SQLException {
        Connection connection = MyConnection.getConnection();

        assert connection != null;


        PreparedStatement statement = connection.prepareStatement(
                "SELECT MAX(PRIXOFFRE) FROM OFFRE WHERE IDVENTE = " + this.idVente
        );

        ResultSet res1 = statement.executeQuery();

        res1.next();

        int max = res1.getInt(1);

        if (true) {
            System.out.printf("Currnet Max Price = : %d \n", max);
        }

        return max;
    }



    @Override
    public Boolean insertNewOffre(String email, int prixOffre, int qteAchat) throws SQLException, CustomInputException {

        if (!this.multipleOffreParPersonne && Utility.findNbOffreOfUser(idVente, email) > 0){
            throw new CustomInputException("Vente autorise une seule offre par utilisateur. Vous avez deja soumis une l'offre");
        }

        // check prix
        if (prixOffre <= this.currentPrixMax){
            throw new CustomInputException("Nouvelle offre doit avoit proposer un prix superieur a ceux des anciens offres");
        }

        int totalCurrentQtyAchat = 0;

        if (this.multipleOffreParPersonne){
            totalCurrentQtyAchat = Utility.findTotalQteAchatPerUser(idVente, email);
        }

        if (qteAchat + totalCurrentQtyAchat > this.qteLot){
            throw new CustomInputException("La quantite d'achat total doit etre inferieur a la quantite du lot. Vous avez deja faite l'offre pour " + totalCurrentQtyAchat);
        }


        // check unicite du depot
        // if single offre, one person can only proposer une seule offre

//        if (this.)

        return Offre.insertNewOffre(idVente, email, prixOffre, qteAchat);
    }

    public void calculGagnant() throws SQLException {
        Connection connection = MyConnection.getConnection();

        assert connection != null;
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT EMAIL, SUM (QUANTITEACHAT * PRIXOFFRE) AS TOTAL, SUM (QUANTITEACHAT)  " +
                "FROM OFFRE WHERE IDVENTE = ? " +
                "GROUP BY EMAIL " +
                "ORDER BY TOTAL DESC"
        );

        preparedStatement.setInt(1, idProduit);

        ResultSet resultSet = preparedStatement.executeQuery();

        // if no produit trouve
        if (!resultSet.isBeforeFirst()){
            throw new RuntimeException("Pas d'offre pour Vente");
        };

        int nbGagnant = 0;
        int totalVente = 0;
        int totalQte = 0;

        while(resultSet.next()){
            if (totalQte + resultSet.getInt(3) <= this.qteLot){
                totalVente += resultSet.getInt(2);
                totalQte+= resultSet.getInt(3);
                nbGagnant++;
                System.out.printf("Le gagnant No%d est %s avec offre total de %d pour %d produits \n", nbGagnant, resultSet.getString(1), resultSet.getInt(2), resultSet.getInt(3));
            } else {
                break;
            }

        }

        // calculate profit / loss
        int totalProfit = totalVente - this.prixDeRevient * totalQte;

        System.out.printf(" ====> BILAN : Le profit / perte du vente est %d sur %d produit \n", totalProfit, totalQte);

        Utility.closeConnection(connection, preparedStatement, resultSet);
    }

    @Override
    public int getCurrentPrix() throws SQLException {

        return 0;
    }


    @Override
    public String toString() {
        return "VenteMontante{" +
                "currentPrixMax=" + currentPrixMax +
                ", idVente=" + idVente +
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
