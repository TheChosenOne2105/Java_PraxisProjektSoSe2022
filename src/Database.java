

import java.nio.file.Path;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.*;

public class Database {


    public void DBstart() {
        //Code basierend auf den Tutorials auf dieser Seite: https://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/
        Connection conn = null;
        Statement stmt;
        Statement stmt2;
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
        Connection conn;
        Statement stmt;
        DateTimeFormatter DateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SS");
        String Messagekey = LocalDateTime.now().format(DateTimeFormat);


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

    public ArrayList<String> LoadingOldMessages(int Addresse) {
        Connection conn;
        Statement stmt;
        ArrayList<String> loadingMessages = new ArrayList<>();


        try {
            String s = String.valueOf(Path.of("").toAbsolutePath());
            String url = "jdbc:sqlite:" + s + "/src/DatabaseForChatApplication.db";
            url = url.replaceAll("\\\\", "/");

            // create a connection to the database
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("Select MESSAGE, TIME FROM MESSAGES WHERE ADDRESS = %o", Addresse));

            while (rs.next()) {
                String returnMessage = "["+ rs.getString("TIME")+ "] " + rs.getString("Message");
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
            Connection conn;
            Statement stmt;



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
                Connection conn;
                Statement stmt;
                ArrayList<String> Usernames = new ArrayList<>();
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
                    check = !Usernames.contains(Username);
                    stmt.close();
                    conn.close();
                return check;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
    public boolean PasswordCheck(String Username, String Password) {
        boolean check;
        Connection conn;
        Statement stmt;
        ArrayList<String> PASSWORDS = new ArrayList<>();
        try {
            String s = String.valueOf(Path.of("").toAbsolutePath());
            String url = "jdbc:sqlite:" + s + "/src/DatabaseForChatApplication.db";
            url = url.replaceAll("\\\\", "/");

            // create a connection to the database
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("Select PASSWORD FROM USERHANDLING where USERNAME = '%s", Username + "'"));
            while (rs.next()){
                String UsernameToAdd = rs.getString("PASSWORD");
                PASSWORDS.add(UsernameToAdd);
            }
            check = PASSWORDS.contains(Password);
            stmt.close();
            conn.close();
            return check;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }



}




