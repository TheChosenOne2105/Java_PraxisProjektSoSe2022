

import java.nio.file.Path;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.*;

public class Database {


    public void DbStart() {
        //Code basierend auf den Tutorials auf dieser Seite: https://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/
        Connection Conn = null;
        Statement Stmt;
        Statement Stmt2;
        Statement Stmt3;
        try {
            // db parameters
            String AbsolutePath = String.valueOf(Path.of("").toAbsolutePath());
            String URL = "jdbc:sqlite:" + AbsolutePath + "/src/DatabaseForChatApplication.db";
            URL = URL.replaceAll("\\\\", "/");

            // create a connection to the database
            Conn = DriverManager.getConnection(URL);


            Stmt = Conn.createStatement();
            String SQL = "CREATE TABLE IF NOT EXISTS MESSAGES " +
                    "(TIME TEXT PRIMARY KEY     NOT NULL," +
                    " MESSAGE        TEXT    NOT NULL, " +
                    " ADDRESS        INT  NOT NULL)";
            Stmt.executeUpdate(SQL);
            Stmt.close();
            Stmt2 = Conn.createStatement();
            String SQL2 = "CREATE TABLE IF NOT EXISTS USERHANDLING " +
                    "(USERNAME TEXT PRIMARY KEY     NOT NULL," +
                    " PASSWORD        TEXT  NOT NULL)";
            Stmt2.executeUpdate(SQL2);
            Stmt2.close();
            Stmt3 = Conn.createStatement();
            String SQL3 = "CREATE TABLE IF NOT EXISTS BANNLIST " +
                    "(USERNAME TEXT PRIMARY KEY NOT NULL," +
                    "REASON TEXT NOT NULL)";
            Stmt3.executeUpdate(SQL3);
            Stmt.close();
            Conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (Conn != null) {
                    Conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void insertIntoOldMessages(String Message, int ChatroomAdresse) {
        Connection Conn;
        Statement Stmt;
        DateTimeFormatter DateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SS");
        String Messagekey = LocalDateTime.now().format(DateTimeFormat);
        try {
            String s = String.valueOf(Path.of("").toAbsolutePath());
            String url = "jdbc:sqlite:" + s + "/src/DatabaseForChatApplication.db";
            url = url.replaceAll("\\\\", "/");

            // create a connection to the database
            Conn = DriverManager.getConnection(url);
            Stmt = Conn.createStatement();
            String SQL = "INSERT INTO MESSAGES (TIME, MESSAGE, ADDRESS)" +
                    String.format(" VALUES (%s", "'" + Messagekey + "', '" + Message + "', " + ChatroomAdresse + ")");
            Stmt.executeUpdate(SQL);
            Stmt.close();
            Conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> LoadingOldMessages(int Addresse) {
        Connection Conn;
        Statement Stmt;
        ArrayList<String> OldMessages = new ArrayList<>();


        try {
            String AbsolutePath = String.valueOf(Path.of("").toAbsolutePath());
            String URL = "jdbc:sqlite:" + AbsolutePath + "/src/DatabaseForChatApplication.db";
            URL = URL.replaceAll("\\\\", "/");

            // create a connection to the database
            Conn = DriverManager.getConnection(URL);
            Stmt = Conn.createStatement();
            ResultSet RS = Stmt.executeQuery(String.format("Select MESSAGE, TIME FROM MESSAGES WHERE ADDRESS = %o", Addresse) + " ORDER BY TIME DESC LIMIT 10;");

            while (RS.next()) {
                String ReturnMessage = "["+ RS.getString("TIME")+ "] " + RS.getString("Message");
                OldMessages.add(ReturnMessage);
            }
            Stmt.close();
            Conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return OldMessages;
    }

        public void insertIntoUserHandling(String Username, String Password) {
            Connection Conn;
            Statement Stmt;



            try {
                String AbsolutePath = String.valueOf(Path.of("").toAbsolutePath());
                String URL = "jdbc:sqlite:" + AbsolutePath + "/src/DatabaseForChatApplication.db";
                URL = URL.replaceAll("\\\\", "/");

                // create a connection to the database
                Conn = DriverManager.getConnection(URL);
                Stmt = Conn.createStatement();
                String SQL = "INSERT INTO USERHANDLING (USERNAME, PASSWORD)" +
                        String.format(" VALUES (%s", "'" + Username+ "', '" + Password+ "' " + ")");
                Stmt.executeUpdate(SQL);
                Stmt.close();
                Conn.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
            public boolean UsernameCheck(String Username) {
                boolean Check;
                Connection Conn;
                Statement Stmt;
                ArrayList<String> Usernames = new ArrayList<>();
                try {
                    String AbsolutePath = String.valueOf(Path.of("").toAbsolutePath());
                    String URL = "jdbc:sqlite:" + AbsolutePath + "/src/DatabaseForChatApplication.db";
                    URL = URL.replaceAll("\\\\", "/");

                    // create a connection to the database
                    Conn = DriverManager.getConnection(URL);
                    Stmt = Conn.createStatement();
                    ResultSet RS = Stmt.executeQuery("Select USERNAME FROM USERHANDLING");
                    while (RS.next()){
                        String UsernameToAdd = RS.getString("Username");
                        Usernames.add(UsernameToAdd);
                    }
                    Check = !Usernames.contains(Username);
                    Stmt.close();
                    Conn.close();
                return Check;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
    public boolean PasswordCheck(String Username, String Password) {
        boolean Check;
        Connection Conn;
        Statement Stmt;
        ArrayList<String> Passwords = new ArrayList<>();
        try {
            String s = String.valueOf(Path.of("").toAbsolutePath());
            String url = "jdbc:sqlite:" + s + "/src/DatabaseForChatApplication.db";
            url = url.replaceAll("\\\\", "/");

            // create a connection to the database
            Conn = DriverManager.getConnection(url);
            Stmt = Conn.createStatement();
            ResultSet RS = Stmt.executeQuery(String.format("Select PASSWORD FROM USERHANDLING where USERNAME = '%s", Username + "'"));
            while (RS.next()){
                String UsernameToAdd = RS.getString("PASSWORD");
                Passwords.add(UsernameToAdd);
            }
            Check = Passwords.contains(Password);
            Stmt.close();
            Conn.close();
            return Check;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }



}




