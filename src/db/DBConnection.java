package db;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:postgresql//localhost:5432/electrcity_db";
    private static final String USER="mbaye";
    private static final String PASSWORD="";

    public void connect(){
        try(Connection conn= DriverManager.getConnection(URL,USER,PASSWORD)){
            if (conn!=null){
                System.out.println("Connected to DB");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

}
