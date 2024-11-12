package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        try {
            DriverManager.registerDriver(
                    new oracle.jdbc.driver.OracleDriver()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String url = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
            String username = "ngutruon";
            String password = "ngutruon";

            Connection connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.printf("Connection fail!");
            e.printStackTrace();
        }

        System.out.printf("Connection sucessful!");
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
    }
}