package Repository;

import Users.User;
import Users.Admin;
import Users.Drafter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository
{
    public User authenticate(String username, String password)
    {
        String sql = "SELECT UserID, FirstName, LastName, Username, Email, Password, Role " +
                "FROM Users WHERE Username = ? AND Password = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                String role = rs.getString("Role");

                if ("Admin".equalsIgnoreCase(role))
                {
                    return new Admin(
                            rs.getInt("UserID"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("Username"),
                            rs.getString("Email"),
                            rs.getString("Password")
                    );
                }
                else
                {
                    return new Drafter(
                            rs.getInt("UserID"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("Username"),
                            rs.getString("Email"),
                            rs.getString("Password")
                    );
                }
            }

        } catch (SQLException e)
        {
            System.err.println("Authentication error: " + e.getMessage());
        }

        return null; // failed
    }

    public List<User> getAllUsers()
    {
        System.out.println("=== START getAllUsers ===");
        List<User> users = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            conn = DatabaseConfig.getConnection();
            System.out.println("1. Connection created: " + conn.hashCode());

            stmt = conn.createStatement();
            System.out.println("2. Statement created");

            // REMOVED "TOP 5" - Get ALL users
            String sql = "SELECT UserID, FirstName, LastName, Username, Email, Password, Role " +
                    "FROM Users ORDER BY LastName, FirstName";
            System.out.println("3. Executing: " + sql);

            rs = stmt.executeQuery(sql);
            System.out.println("4. ResultSet created, reading rows...");

            // Test: Try to read all rows into a buffer first
            List<String[]> buffer = new ArrayList<>();
            while (true)
            {
                try
                {
                    if (!rs.next())
                    {
                        System.out.println("5. No more rows in ResultSet");
                        break;
                    }

                    String[] row = new String[7];
                    row[0] = String.valueOf(rs.getInt("UserID"));
                    row[1] = rs.getString("FirstName");
                    row[2] = rs.getString("LastName");
                    row[3] = rs.getString("Username");
                    row[4] = rs.getString("Email");
                    row[5] = rs.getString("Password");
                    row[6] = rs.getString("Role");

                    buffer.add(row);
                    System.out.println("  Row " + buffer.size() + ": " + row[1] + " " + row[2]);

                }
                catch (SQLException e)
                {
                    System.err.println("ERROR at row " + (buffer.size() + 1) + ": " + e.getMessage());
                    System.err.println("Is ResultSet closed? " + (rs == null ? "null" : rs.isClosed()));
                    System.err.println("Is Statement closed? " + (stmt == null ? "null" : stmt.isClosed()));
                    System.err.println("Is Connection closed? " + (conn == null ? "null" : conn.isClosed()));
                    break;
                }
            }

            System.out.println("6. Read " + buffer.size() + " rows into buffer");

            // Process buffer
            for (String[] row : buffer)
            {
                int userId = Integer.parseInt(row[0]);
                String firstName = row[1];
                String lastName = row[2];
                String username = row[3];
                String email = row[4];
                String password = row[5];
                String role = row[6];

                User user;
                if ("Admin".equalsIgnoreCase(role))
                {
                    user = new Admin(userId, firstName, lastName, username, email, password);
                }
                else
                {
                    user = new Drafter(userId, firstName, lastName, username, email, password);
                }

                users.add(user);
            }

            System.out.println("7. Created " + users.size() + " user objects");

        } catch (SQLException e)
        {
            System.err.println("MAIN ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            System.out.println("8. Closing resources...");
            try { if (rs != null) { rs.close(); System.out.println("  ResultSet closed"); } } catch (Exception e) {}
            try { if (stmt != null) { stmt.close(); System.out.println("  Statement closed"); } } catch (Exception e) {}
            try { if (conn != null) { conn.close(); System.out.println("  Connection closed"); } } catch (Exception e) {}
        }

        System.out.println("=== END getAllUsers (returning " + users.size() + " users) ===");
        return users;
    }
    public boolean addUser(User user) // admin
    {
        String sql = "INSERT INTO Users (FirstName, LastName, Username, Email, Password, Role) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPassword());

            String role = user.getRole();
            if (role == null)
            {
                role = "user"; // Default for new users
            }
            else
            {
                role = role.toLowerCase().trim();

                if (!role.equals("user") && !role.equals("admin"))
                {
                    role = "user"; // Default to 'user' if invalid
                }
            }

            pstmt.setString(6, role);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        }
        catch (SQLException e)
        {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(User user) //admin
    {
        String sql = "UPDATE Users SET FirstName = ?, LastName = ?, Username = ?, " +
                "Email = ?, Password = ?, Role = ? WHERE UserID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPassword());
            pstmt.setString(6, user.getRole());
            pstmt.setInt(7, user.getUserId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        }
        catch (SQLException e)
        {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(int userId) //admin
    {
        String sql = "DELETE FROM Users WHERE UserID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        }
        catch (SQLException e)
        {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public boolean usernameExists(String username)
    {
        String sql = "SELECT COUNT(*) FROM Users WHERE Username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                return rs.getInt(1) > 0;
            }

        }
        catch (SQLException e)
        {
            System.err.println("Error checking username: " + e.getMessage());
        }

        return false;
    }

    public User getUserById(int userId)
    {
        String sql = "SELECT UserID, FirstName, LastName, Username, Email, Password, Role " +
                "FROM Users WHERE UserID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                String role = rs.getString("Role");

                if ("Admin".equalsIgnoreCase(role))
                {
                    return new Admin(
                            rs.getInt("UserID"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("Username"),
                            rs.getString("Email"),
                            rs.getString("Password")
                    );
                }
                else
                {
                    return new Drafter(
                            rs.getInt("UserID"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("Username"),
                            rs.getString("Email"),
                            rs.getString("Password")
                    );
                }
            }

        }
        catch (SQLException e)
        {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }

        return null;
    }
}