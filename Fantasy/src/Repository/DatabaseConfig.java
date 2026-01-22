
package Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig
{
    private static Connection connection = null;

    // SQL Authentication
    private static final String SERVER = "ALEX";
    private static final String DATABASE = "Draft System";
    private static final String USERNAME = "fantasy_user";
    private static final String PASSWORD = "fantasy123";
    private static final int PORT = 1433;

    // SQL Authentication connection string
    private static final String CONNECTION_STRING = String.format(
            "jdbc:sqlserver://%s:%d;" +
                    "databaseName=%s;" +
                    "user=%s;" +
                    "password=%s;" +
                    "encrypt=false;" +
                    "trustServerCertificate=true;" +
                    "loginTimeout=5;",
            SERVER, PORT, DATABASE, USERNAME, PASSWORD
    );

    private DatabaseConfig()
    {

    }

    public static Connection getConnection() throws SQLException
    {
        if (connection == null || connection.isClosed())
        {
            try
            {
                // Load driver
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

                // Create connection
                connection = DriverManager.getConnection(CONNECTION_STRING);

                System.out.println("Database connection established!");
                System.out.println("Using SQL Authentication");

            }
            catch (ClassNotFoundException e)
            {
                throw new SQLException("Driver not found", e);
            }
        }

        return connection;
    }

    public static void closeConnection()
    {
        if (connection != null)
        {
            try
            {
                connection.close();
                System.out.println("Database connection closed.");
                connection = null;
            }
            catch (SQLException e)
            {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}