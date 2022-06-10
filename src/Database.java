

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;
import java.time.*;
import java.util.Scanner;

public class Database {


    public static void DBstart() {
        //Code von: https://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/
        Connection conn = null;
        Statement stmt = null;
        Statement stmt2 = null;
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
            stmt2 = conn.createStatement();
            String sql2 = "CREATE TABLE IF NOT EXISTS USERHANDLING " +
                    "(USERNAME TEXT PRIMARY KEY     NOT NULL," +
                    " PASSWORD        TEXT  NOT NULL)";
            stmt2.executeUpdate(sql2);
            stmt2.close();
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

    public void insertIntoOldMessages(String Message, int ChatroomAdresse) {
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

    public ArrayList LoadingOldMessages(int Addresse) {
        Connection conn = null;
        Statement stmt = null;
        ArrayList<String> loadingMessages = new ArrayList<String>();

        try {
            String s = String.valueOf(Path.of("").toAbsolutePath());
            String url = "jdbc:sqlite:" + s + "/src/DatabaseForChatApplication.db";
            url = url.replaceAll("\\\\", "/");

            // create a connection to the database
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("Select MESSAGE FROM MESSAGES WHERE ADDRESS = %o", Addresse));

            while (rs.next()) {
                String returnMessage = rs.getString("Message");
                loadingMessages.add(returnMessage);
            }
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loadingMessages;
    }

        public void insertIntoUserHandling(String Username, String Password) {
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
                String sql = "INSERT INTO USERHANDLING (USERNAME, PASSWORD)" +
                        String.format(" VALUES (%s", "'" + Username+ "', '" + Password+ "' " + ")");
                stmt.executeUpdate(sql);
                stmt.close();
                conn.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
            public boolean UsernameCheck(String Username) {
                boolean check;
                Connection conn = null;
                Statement stmt = null;
                ArrayList<String> Usernames = new ArrayList<String>();
                try {
                    String s = String.valueOf(Path.of("").toAbsolutePath());
                    String url = "jdbc:sqlite:" + s + "/src/DatabaseForChatApplication.db";
                    url = url.replaceAll("\\\\", "/");

                    // create a connection to the database
                    conn = DriverManager.getConnection(url);
                    stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("Select USERNAME FROM USERHANDLING");
                    while (rs.next()){
                        String UsernameToAdd = rs.getString("Username");
                        Usernames.add(UsernameToAdd);
                    }
                    if(Usernames.contains(Username)){
                        check = false;
                    } else {
                        check = true;
                    }
                    stmt.close();
                    conn.close();
                return check;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
    public static boolean PasswordCheck(String Username, String Password) {
        boolean check;
        Connection conn = null;
        Statement stmt = null;
        ArrayList<String> PASSWORDS = new ArrayList<String>();
        try {
            String s = String.valueOf(Path.of("").toAbsolutePath());
            String url = "jdbc:sqlite:" + s + "/src/DatabaseForChatApplication.db";
            url = url.replaceAll("\\\\", "/");

            // create a connection to the database
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            System.out.println(String.format("Select PASSWORD FROM USERHANDLING where USERNAME = '%s", Username + "'"));
            ResultSet rs = stmt.executeQuery(String.format("Select PASSWORD FROM USERHANDLING where USERNAME = '%s", Username + "'"));
            while (rs.next()){
                String UsernameToAdd = rs.getString("Username");
                PASSWORDS.add(UsernameToAdd);
            }
            if(PASSWORDS.contains(Password)){
                check = false;
            } else {
                check = true;
            }
            stmt.close();
            conn.close();
            return check;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public static void main(String[] args) {
        DBstart();
        System.out.println(PasswordCheck("Simon", "210501"));
    }
}




