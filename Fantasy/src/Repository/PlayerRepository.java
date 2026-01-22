package Repository;

import Draft.Player;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerRepository
{
    public List<Player> getAllPlayers()
    {
        List<Player> players = new ArrayList<>();

        // JOIN query with your exact column names
        String sql = "SELECT p.PlayerID, p.FirstName, p.LastName, p.Overall, p.Age, " +
                "p.Country, p.PositionID, p.TeamID, " +
                "pos.PositionName, t.TeamName " +
                "FROM Players p " +
                "LEFT JOIN Positions pos ON p.PositionID = pos.PositionID " +
                "LEFT JOIN Teams t ON p.TeamID = t.TeamID " +
                "ORDER BY p.Overall DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                Player player = mapResultSetToPlayer(rs);
                players.add(player);
            }

            System.out.println("Loaded " + players.size() + " players");
        }
        catch (SQLException e)
        {
            System.err.println("Error loading players: " + e.getMessage());
            e.printStackTrace();
        }

        return players;
    }

    public List<Player> getPlayersByPosition(String positionName)
    {
        List<Player> players = new ArrayList<>();

        String sql = "SELECT p.PlayerID, p.FirstName, p.LastName, p.Overall, p.Age, " +
                "p.Country, p.PositionID, p.TeamID, " +
                "pos.PositionName, t.TeamName " +
                "FROM Players p " +
                "JOIN Positions pos ON p.PositionID = pos.PositionID " +
                "LEFT JOIN Teams t ON p.TeamID = t.TeamID " +
                "WHERE pos.PositionName = ? " +
                "ORDER BY p.Overall DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, positionName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next())
            {
                Player player = mapResultSetToPlayer(rs);
                players.add(player);
            }

            rs.close();
            System.out.println("Loaded " + players.size() + " players for position: " + positionName);
        }
        catch (SQLException e)
        {
            System.err.println("Error loading players by position: " + e.getMessage());
        }

        return players;
    }

    public Player getPlayerById(int playerId)
    {
        String sql = "SELECT p.PlayerID, p.FirstName, p.LastName, p.Overall, p.Age, " +
                "p.Country, p.PositionID, p.TeamID, " +
                "pos.PositionName, t.TeamName " +
                "FROM Players p " +
                "LEFT JOIN Positions pos ON p.PositionID = pos.PositionID " +
                "LEFT JOIN Teams t ON p.TeamID = t.TeamID " +
                "WHERE p.PlayerID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                return mapResultSetToPlayer(rs);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error loading player by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean addPlayer(Player player)
    {
        String sql = "INSERT INTO Players (FirstName, LastName, Overall, Age, " +
                "Country, PositionID, TeamID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, player.getFirstName());
            pstmt.setString(2, player.getLastName());
            pstmt.setInt(3, player.getOverall());
            pstmt.setInt(4, player.getAge());
            pstmt.setString(5, player.getCountry());
            pstmt.setInt(6, player.getPositionId());
            pstmt.setInt(7, player.getTeamId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error adding player: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePlayer(Player player)
    {
        String sql = "UPDATE Players SET FirstName = ?, LastName = ?, Overall = ?, " +
                "Age = ?, Country = ?, PositionID = ?, TeamID = ? " +
                "WHERE PlayerID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, player.getFirstName());
            pstmt.setString(2, player.getLastName());
            pstmt.setInt(3, player.getOverall());
            pstmt.setInt(4, player.getAge());
            pstmt.setString(5, player.getCountry());
            pstmt.setInt(6, player.getPositionId());
            pstmt.setInt(7, player.getTeamId());
            pstmt.setInt(8, player.getPlayerId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error updating player: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePlayer(int playerId)
    {
        String sql = "DELETE FROM Players WHERE PlayerID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, playerId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error deleting player: " + e.getMessage());
            return false;
        }
    }

    private Player mapResultSetToPlayer(ResultSet rs) throws SQLException
    {
        Player player = new Player(
                rs.getInt("PlayerID"),
                rs.getString("FirstName"),
                rs.getString("LastName"),
                rs.getInt("Overall"),
                rs.getInt("Age"),
                rs.getInt("TeamID"),
                rs.getInt("PositionID"),
                rs.getString("Country"),
                rs.getString("PositionName"),
                rs.getString("TeamName")
        );

        return player;
    }

    public List<Player> getGoalkeepers()
    {
        return getPlayersByPosition("Goalkeeper");
    }


    public List<Player> getDefenders()
    {
        List<Player> defenders = new ArrayList<>();

        String sql = "SELECT p.PlayerID, p.FirstName, p.LastName, p.Overall, p.Age, " +
                "p.Country, p.PositionID, p.TeamID, " +
                "pos.PositionName, t.TeamName " +
                "FROM Players p " +
                "JOIN Positions pos ON p.PositionID = pos.PositionID " +
                "LEFT JOIN Teams t ON p.TeamID = t.TeamID " +
                "WHERE pos.PositionName IN ('Center Back', 'Right Back', 'Left Back') " +
                "ORDER BY p.Overall DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                Player player = mapResultSetToPlayer(rs);
                defenders.add(player);
            }

            System.out.println("Loaded " + defenders.size() + " defenders");
        }
        catch (SQLException e)
        {
            System.err.println("Error loading defenders: " + e.getMessage());
        }

        return defenders;
    }

    public List<Player> getMidfielders()
    {
        List<Player> midfielders = new ArrayList<>();

        String sql = "SELECT p.PlayerID, p.FirstName, p.LastName, p.Overall, p.Age, " +
                "p.Country, p.PositionID, p.TeamID, " +
                "pos.PositionName, t.TeamName " +
                "FROM Players p " +
                "JOIN Positions pos ON p.PositionID = pos.PositionID " +
                "LEFT JOIN Teams t ON p.TeamID = t.TeamID " +
                "WHERE pos.PositionName IN ('Defensive Midfielder', 'Central Midfielder', 'Attacking Midfielder') " +
                "ORDER BY p.Overall DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                Player player = mapResultSetToPlayer(rs);
                midfielders.add(player);
            }

            System.out.println("Loaded " + midfielders.size() + " midfielders");
        }
        catch (SQLException e)
        {
            System.err.println("Error loading midfielders: " + e.getMessage());
        }

        return midfielders;
    }

    public List<Player> getForwards()
    {
        List<Player> forwards = new ArrayList<>();

        String sql = "SELECT p.PlayerID, p.FirstName, p.LastName, p.Overall, p.Age, " +
                "p.Country, p.PositionID, p.TeamID, " +
                "pos.PositionName, t.TeamName " +
                "FROM Players p " +
                "JOIN Positions pos ON p.PositionID = pos.PositionID " +
                "LEFT JOIN Teams t ON p.TeamID = t.TeamID " +
                "WHERE pos.PositionName IN ('Right Winger', 'Left Winger', 'Striker') " +
                "ORDER BY p.Overall DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                Player player = mapResultSetToPlayer(rs);
                forwards.add(player);
            }

            System.out.println("Loaded " + forwards.size() + " forwards");
        }
        catch (SQLException e)
        {
            System.err.println("Error loading forwards: " + e.getMessage());
        }

        return forwards;
    }




}