
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

                System.out.println("✓ Database connection established!");
                System.out.println("  Using SQL Authentication");

            }
            catch (ClassNotFoundException e)
            {
                throw new SQLException("JDBC Driver not found. Add mssql-jdbc.jar to classpath.", e);
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

    public static void testConnection()
    {
        System.out.println("=== Testing Database Connection ===");
        System.out.println("Server: " + SERVER);
        System.out.println("Database: " + DATABASE);
        System.out.println("Username: " + USERNAME);
        System.out.println("Connection string: " +
                CONNECTION_STRING.replace(PASSWORD, "******"));
        System.out.println();

        try (Connection conn = getConnection())
        {
            // Test with version query
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery("SELECT @@VERSION as version");

            if (rs.next())
            {
                String version = rs.getString("version");
                System.out.println("✓ Connection successful!");
                System.out.println("  SQL Server: " + version.split("\n")[0]);
            }

            // Show current user
            rs = stmt.executeQuery("SELECT USER_NAME() as user_name");
            if (rs.next())
            {
                System.out.println("  Database User: " + rs.getString("user_name"));
            }

        }
        catch (SQLException e)
        {
            System.err.println("✗ Connection failed: " + e.getMessage());
        }
    }

    public static void checkTables()
    {
        System.out.println("\n=== Checking Database Tables ===");

        try (Connection conn = getConnection();
             var stmt = conn.createStatement())
        {

            // List all tables
            var rs = stmt.executeQuery(
                    "SELECT TABLE_NAME " +
                            "FROM INFORMATION_SCHEMA.TABLES " +
                            "WHERE TABLE_TYPE = 'BASE TABLE' " +
                            "ORDER BY TABLE_NAME");

            int count = 0;
            while (rs.next())
            {
                System.out.println("  - " + rs.getString("TABLE_NAME"));
                count++;
            }

            System.out.println("Total tables: " + count);

            // Count records in Users table (if exists)
            try
            {
                rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Users");
                if (rs.next())
                {
                    System.out.println("  Users table has: " + rs.getInt("count") + " records");
                }
            }
            catch (Exception e)
            {
                System.out.println("  Users table not found or empty");
            }

        }
        catch (SQLException e)
        {
            System.err.println("Error checking tables: " + e.getMessage());
        }
    }
}