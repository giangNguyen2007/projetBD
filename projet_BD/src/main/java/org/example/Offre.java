package org.example;

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

        LocalDateTime now = LocalDateTime.now();

        PreparedStatement updateDateStatement = connection.prepareStatement(
                "INSERT INTO DATEHEURE VALUES (?)"
        );

        updateDateStatement.setTimestamp(1, Timestamp.valueOf(now));
        int res1 = updateDateStatement.executeUpdate();

        if (res1 > 0) {
            System.out.printf("OFFRE INSERTION : Update Dateheure table with current date heure success \n");
        }

//        Calendar cal = new GregorianCalendar(2020,5,12);
//        java.sql.Date d = new Date(cal.getTime().getTime());

//        Timestamp myTimeStamp = Timestamp.valueOf("2018-11-12 01:02:03");

        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO OFFRE VALUES (?, ?, ?, ?, ?)"
        );
        preparedStatement.setInt(1, idVente);
        preparedStatement.setTimestamp(2, Timestamp.valueOf(now));
        preparedStatement.setString(3, email);
        preparedStatement.setInt(4, prixOffre);
        preparedStatement.setInt(5, qteAchat);

//        PreparedStatement preparedStatement = connection.prepareStatement(
//                "INSERT INTO DATEHEURE VALUES (TO_TIMESTAMP('2019-11-19 15:30:45', 'YYYY-MM-DD HH24:MI:SS'))"
//        );

//        PreparedStatement preparedStatement = connection.prepareStatement(
//                "INSERT INTO DATEHEURE VALUES (?)"
//        );

//        preparedStatement.setDate(1, d);
//        preparedStatement.setTimestamp(1, Timestamp.valueOf(now));
//        preparedStatement.setDate(1, d);


        int res = preparedStatement.executeUpdate();

        if (res > 0) {
            System.out.printf("OFFRE INSERTION : Insertion new offer success");
        }


        if (res1 > 0 && res > 0){
            insertionSuccess = Boolean.TRUE;
        }

        preparedStatement.close();
        updateDateStatement.close();
        connection.close();

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
