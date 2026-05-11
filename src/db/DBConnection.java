package db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        try {
            Properties props = new Properties();
            InputStream input = getClass()
                    .getClassLoader()
                    .getResourceAsStream("db.properties");
            if (input == null) {
                throw new RuntimeException("db.properties not found in classpath");
            }
            props.load(input);

            String url = "jdbc:postgresql://"
                    + props.getProperty("db.host") + ":"
                    + props.getProperty("db.port") + "/"
                    + props.getProperty("db.name");

            connection = DriverManager.getConnection(
                    url,
                    props.getProperty("db.user"),
                    props.getProperty("db.password")
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) instance = new DBConnection();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
