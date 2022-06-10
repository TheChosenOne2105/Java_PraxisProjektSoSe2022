

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.time.*;
import java.util.Scanner;

public class Database {


    public void connect() {
        //Code von: https://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/
        Connection conn = null;
        Statement stmt = null;
        try {
            // db parameters
            String s = String.valueOf(Path.of("").toAbsolutePath());
            String url = "jdbc:sqlite:" + s + "/src/DatabaseForChatApplication.db";
            url = url.replaceAll("\\\\", "/");

            // create a connection to the database
            conn = DriverManager.getConnection(url);


            stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS MESSAGES " +
                    "(TIME TEXT PRIMARY KEY     NOT NULL," +
                    " MESSAGE        TEXT    NOT NULL, " +
                    " ADDRESS        INT  NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void insertIntoDatabase(String Message, int ChatroomAdresse) {
        Connection conn = null;
        Statement stmt = null;
        String Messagekey = LocalDateTime.now().toString();


        try {
            String s = String.valueOf(Path.of("").toAbsolutePath());
            String url = "jdbc:sqlite:" + s + "/src/DatabaseForChatApplication.db";
            url = url.replaceAll("\\\\", "/");

            // create a connection to the database
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            String sql = "INSERT INTO MESSAGES (TIME, MESSAGE, ADDRESS)" +
                    String.format(" VALUES (%s", "'" + Messagekey + "', '" + Message + "', " + ChatroomAdresse + ")");
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void LoadingOldMessages(int Addresse){
        Connection conn = null;
        Statement stmt = null;
        String Messagekey = LocalDateTime.now().toString();


        try {
            String s = String.valueOf(Path.of("").toAbsolutePath());
            String url = "jdbc:sqlite:" + s + "/src/DatabaseForChatApplication.db";
            url = url.replaceAll("\\\\", "/");

            // create a connection to the database
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("Select MESSAGE FROM MESSAGES WHERE ADDRESS = %o", Addresse));

            while (rs.next()){
                String Message = rs.getString("MESSAGE");
                System.out.println(Message);
            }
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public static void main(String[] args) {
        LoadingOldMessages(3);
    }
}


