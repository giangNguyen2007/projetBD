package org.example;

import org.example.exceptions.CustomInputException;

import javax.swing.text.StyledEditorKit;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Offre {
    public int idVente;
    public LocalDateTime dateOffre;
    public String email;
    public int prixOffre;
    public int qteAchat;

    public Offre(int idVente, LocalDateTime dateOffre, String email, int prixOffre, int qteAchat) {
        this.idVente = idVente;
        this.dateOffre = dateOffre;
        this.email = email;
        this.prixOffre = prixOffre;
        this.qteAchat = qteAchat;
    }



    public static Boolean insertNewOffre(int idVente, String email, int prixOffre, int qteAchat) throws SQLException {
        Connection connection = MyConnection.getConnection();

        Boolean insertionSuccess = Boolean.FALSE;

        assert connection != null;

        // check prix

        try {

            // CHECK PRIX OFFRE > CURRENT PRIX MAX

            PreparedStatement statement_prix_max = connection.prepareStatement(
                    "SELECT MAX(PRIXOFFRE) FROM OFFRE WHERE IDVENTE = " + idVente
            );

            ResultSet prix_max_resultSet = statement_prix_max.executeQuery();

            prix_max_resultSet.next();

            int currentPrixMax = prix_max_resultSet.getInt(1);

            if (prixOffre <= currentPrixMax) {
                throw new CustomInputException("Nouvelle offre doit avoit proposer un prix superieur a ceux des anciens offres");
            }

            // INSERT CURRENT DATE TO DATEHEURE TABLE

            LocalDateTime now = LocalDateTime.now();

            PreparedStatement updateDateStatement = connection.prepareStatement(
                    "INSERT INTO DATEHEURE VALUES (?)"
            );

            updateDateStatement.setTimestamp(1, Timestamp.valueOf(now));
            int res1 = updateDateStatement.executeUpdate();

            if (res1 > 0) {
                System.out.printf("OFFRE INSERTION : Update Dateheure table with current date heure success \n");
            }

            // INSERT NEW OFFRE TO OFFRE TABLE

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO OFFRE VALUES (?, ?, ?, ?, ?)"
            );
            preparedStatement.setInt(1, idVente);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(now));
            preparedStatement.setString(3, email);
            preparedStatement.setInt(4, prixOffre);
            preparedStatement.setInt(5, qteAchat);



            int res = preparedStatement.executeUpdate();

            if (res > 0) {
                System.out.printf("OFFRE INSERTION : Insertion new offer success");
            }


            if (res1 > 0 && res > 0) {
                insertionSuccess = Boolean.TRUE;
            }

            statement_prix_max.close();
            preparedStatement.close();
            updateDateStatement.close();

            Utility.commitAndCloseConnection(connection);

        } catch (CustomInputException | SQLException e){
            connection.rollback();
            System.out.println(e.getMessage());
        }

        return insertionSuccess;
    }

    @Override
    public String toString() {
        return "Offre{" +
                "idVente=" + idVente +
                ", dateOffre=" + dateOffre +
                ", email='" + email + '\'' +
                ", prixOffre=" + prixOffre +
                ", qteAchat=" + qteAchat +
                '}';
    }
}
