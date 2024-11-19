package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MyConnection {
//    public static final String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
//    public static final String USER = "azizih";     // A remplacer pour votre compte, sinon genere une exception
//    public static final String PASSWD = "azizih";

    public static final String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
    public static final String USER = "azizih";     // A remplacer pour votre compte, sinon genere une exception
    public static final String PASSWD = "azizih";


    public void loadDriver(){
        try {
            DriverManager.registerDriver(
                    new oracle.jdbc.driver.OracleDriver()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        try {

            Connection connection = DriverManager.getConnection(CONN_URL, USER, PASSWD);

            System.out.printf("Connection sucessful!");

            return connection;

        } catch (SQLException e) {
            System.out.printf("Error!");
            e.printStackTrace();

            return null;
        }
    }
}
