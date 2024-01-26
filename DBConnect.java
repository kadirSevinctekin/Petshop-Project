import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    public Connection connectToDb(String dbname, String user, String password) {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + dbname, user, password);
            if (conn != null) {
                System.out.println("Got it !!! ");
            } else {
                System.out.println("Failed !!! ");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return conn;
    }
}
