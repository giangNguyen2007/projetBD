package org.example;

import org.example.exceptions.CustomInputException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Scanner;

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

        return max;
    }



    @Override
    public Boolean insertNewOffre(String email, int prixOffre, int qteAchat) throws SQLException, CustomInputException {

        if (!this.multipleOffreParPersonne && Utility.findNbOffreOfUser(idVente, email) > 0){
            throw new CustomInputException("Vente autorise une seule offre par utilisateur. Vous avez deja soumis une l'offre");
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
        try {


            assert connection != null;
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT EMAIL, SUM (QUANTITEACHAT * PRIXOFFRE) AS TOTAL, SUM (QUANTITEACHAT)  " +
                            "FROM OFFRE WHERE IDVENTE = ? " +
                            "GROUP BY EMAIL " +
                            "ORDER BY TOTAL DESC"
            );

            preparedStatement.setInt(1, idVente);

            ResultSet resultSet = preparedStatement.executeQuery();

            // if no produit trouve
            if (!resultSet.isBeforeFirst()) {
                throw new RuntimeException("Pas d'offre pour Vente");
            }
            ;

            int nbGagnant = 0;
            this.totalRevenu = 0;
            this.quteVendu = 0;

            while (resultSet.next()) {
                if (this.quteVendu + resultSet.getInt(3) <= this.qteLot) {
                    this.totalRevenu += resultSet.getInt(2);
                    this.quteVendu += resultSet.getInt(3);
                    nbGagnant++;
                    System.out.printf("Le gagnant No%d est %s avec offre total de %d pour %d produits \n", nbGagnant, resultSet.getString(1), resultSet.getInt(2), resultSet.getInt(3));
                } else {
                    break;
                }

            }

            // calculate profit / loss
            int totalProfit = this.totalRevenu - this.prixDeRevient * this.quteVendu;

            System.out.printf(" ====> BILAN : Le profit / perte actuelle du vente est %d sur %d produit \n", totalProfit, this.quteVendu);

            // DEMANDE USER ACTION
            Scanner scanner = new Scanner(System.in);

            // Prompt the user for input
            System.out.print("Valider les offres et terminer la vente (Y/N): ");
            String user_choix = scanner.nextLine();

            if (Objects.equals(user_choix, "Y")) {

                Utility.deleteVente(connection, idVente);
                Utility.updateProduitStock(connection, this.produitStock - this.quteVendu, idProduit);
            }

            Utility.commitAndCloseConnection(connection, preparedStatement, resultSet);

        } catch (SQLException e) {
            System.out.printf(e.getMessage());
        }

    }


    @Override
    public int getCurrentPrix() throws SQLException {

        return 0;
    }


    @Override
    public String printPublic() {
        return super.printPublic() + " \n Vente en cours, prix max actuel = " + this.currentPrixMax;
    }
}
