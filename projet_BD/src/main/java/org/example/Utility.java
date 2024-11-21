package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
}
