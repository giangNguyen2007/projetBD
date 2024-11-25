package org.example;

import org.example.exceptions.CustomInputException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class VenteDescendante extends Vente{

    private int reductionPerMinute;

    private int currentPrix;

    private int quteRestant;

    public VenteDescendante(int idVente, int prixDepart, Boolean revocabilite, String nbOffreParPersonne, int qteLot, int idSalleDeVente, int idProduit, LocalDateTime dateDepot) {
        super(idVente, prixDepart, revocabilite, nbOffreParPersonne,qteLot, idSalleDeVente, idProduit, dateDepot);
        this.categorie = VenteCategorie.DESCENDANTE;
        this.currentPrix = prixDepart;
        this.quteRestant = qteLot;

        try {
            this.reductionPerMinute = this.getReductionPerMinute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void calculGagnant() throws SQLException {
    }


    @Override
    public Boolean insertNewOffre(String email, int prixOffre, int qteAchat) throws SQLException, CustomInputException {

//        if (prixOffre <= this.currentPrixMax){
//            throw new CustomInputException("Nouvelle offre doit avoit proposer un prix superieur a ceux des anciens offres");
//        }

        if (qteAchat > this.quteRestant){
            throw new CustomInputException("La quantite d'achat doit etre inferieur a la quantite restante du lot");
        }

        if (!this.multipleOffreParPersonne && Utility.findNbOffreOfUser(idVente, email) > 0){
            throw new CustomInputException("Vente autorise une seule offre par utilisateur. Vous avez deja soumis une l'offre");
        }



        // check unicite du depot
        // if single offre, one person can only proposer une seule offre

//        if (this.)

        return Offre.insertNewOffre(idVente, email, prixOffre, qteAchat);

    }

    public int getReductionPerMinute() throws SQLException {
        Connection connection = MyConnection.getConnection();

        assert connection != null;
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT PRIXREDUCTION FROM VENTEDESCENDANTE WHERE IDVENTE = ?"
        );

        preparedStatement.setInt(1, idVente);

        ResultSet resultSet = preparedStatement.executeQuery();

        // if no produit trouve
        if (!resultSet.isBeforeFirst()){
            throw new RuntimeException("Id vente n'est pas trouve dans table VENTEDESCENDANTE");
        };

        resultSet.next();
        int res = resultSet.getInt(1);
        if (res <= 0){
            throw new RuntimeException("Montant de la reduction par minute doit etre positive");
        }

        Utility.closeConnection(connection, preparedStatement, resultSet);
        return res;
    }

    public int getCurrentPrix(){

        LocalDateTime now = LocalDateTime.now();

        long interval = this.dateDepot.until(now, ChronoUnit.MINUTES);
        // print results
        System.out.printf("Number of minutes from depot : %d \n", interval);
        System.out.println("Date de depot = : " + this.dateDepot);



        int currentPrice = this.prixDepart - (int)interval * reductionPerMinute;
        if (currentPrice < 0) {
            System.out.println("Le temps de l'enchere est passe, produit n'est plus en vente  " + this.currentPrix);
            return 0;}

        System.out.println("Le prix actuel par produit est  " + this.currentPrix);

        return currentPrice;
    }




}
