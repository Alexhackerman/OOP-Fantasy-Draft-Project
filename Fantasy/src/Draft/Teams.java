package Draft;

import Repository.DatabaseConfig;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Teams
{
    private static Map<Integer, String> idToName = null;
    private static Map<String, Integer> nameToId = null;

    static
    {
        loadTeamsFromDatabase();
    }

    private static void loadTeamsFromDatabase()
    {
        idToName = new HashMap<>();
        nameToId = new HashMap<>();

        String sql = "SELECT TeamID, TeamName FROM Teams ORDER BY TeamName";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {

            while (rs.next())
            {
                int teamId = rs.getInt("TeamID");
                String teamName = rs.getString("TeamName");

                idToName.put(teamId, teamName);
                nameToId.put(teamName, teamId);
            }

            System.out.println("Teams class: Loaded " + idToName.size() + " teams from database");

        }
        catch (SQLException e)
        {
            System.err.println("Error loading teams from database: " + e.getMessage());

            idToName = new HashMap<>();
            nameToId = new HashMap<>();
        }
    }


    public static String getNameById(int teamId)
    {
        if (idToName == null)
        {
            loadTeamsFromDatabase();
        }

        String teamName = idToName.get(teamId);

        if (teamName != null)
        {
            return teamName;
        }

        return getNameByIdFromDatabase(teamId);
    }

    public static int getIdByName(String teamName)
    {
        if (nameToId == null)
        {
            loadTeamsFromDatabase();
        }

        Integer teamId = nameToId.get(teamName);

        if (teamId != null)
        {
            return teamId;
        }

        return getIdByNameFromDatabase(teamName);
    }

    public static Map<Integer, String> getAllTeams()
    {
        if (idToName == null)
        {
            loadTeamsFromDatabase();
        }

        return new HashMap<>(idToName);
    }

    public static java.util.List<String> getAllTeamNames()
    {
        if (idToName == null)
        {
            loadTeamsFromDatabase();
        }

        return new java.util.ArrayList<>(idToName.values());
    }

    // Private helper methods

    private static String getNameByIdFromDatabase(int teamId)
    {
        String sql = "SELECT TeamName FROM Teams WHERE TeamID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {

            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                String teamName = rs.getString("TeamName");

                // Add to cache for next time
                idToName.put(teamId, teamName);
                nameToId.put(teamName, teamId);
                return teamName;
            }

        }
        catch (SQLException e)
        {
            System.err.println("Error getting team name for ID " + teamId + ": " + e.getMessage());
        }

        return "Team #" + teamId;
    }

    private static int getIdByNameFromDatabase(String teamName)
    {
        String sql = "SELECT TeamID FROM Teams WHERE TeamName = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {

            pstmt.setString(1, teamName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                int teamId = rs.getInt("TeamID");

                // Add to cache for next time
                idToName.put(teamId, teamName);
                nameToId.put(teamName, teamId);
                return teamId;
            }

        }
        catch (SQLException e)
        {
            System.err.println("Error getting team ID for name " + teamName + ": " + e.getMessage());
        }

        return -1; // Not found
    }
}