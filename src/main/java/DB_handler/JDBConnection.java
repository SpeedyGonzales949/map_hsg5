package DB_handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;

/**
 * this class implements the connection to a MYSQL database
 */
public class JDBConnection {
    private static final String URL="jdbc:mysql://localhost:3306/map_hsg5";
    private static final  String USER="root";
    private static final String PASS="20edf292-cc4a-4f78-a783-3b878a6fb92c";

    public static Connection getJDBConnection() {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager
                    .getConnection(URL, USER, PASS);
        }catch (Exception exception){
            System.out.println(Arrays.toString(exception.getStackTrace()));
            return null;
        }
    }
}
