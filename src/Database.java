import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {



        public static void connect() {
            //Code von: https://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/
            Connection conn = null;
            try {
                // db parameters
                String s = String.valueOf(Path.of("").toAbsolutePath());
                String url = "jdbc:sqlite:" + s + "/src/DatabaseForChatApplication.db";
                url = url.replaceAll("\\\\", "/");

                // create a connection to the database
                conn = DriverManager.getConnection(url);

                System.out.println("Connection to SQLite has been established.");

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


        public static void main(String[] args) {
            connect();
        }

}