package Repository;

import Draft.Player;
import Users.Drafter;
import java.sql.*;
import java.util.*;

public class DraftRepository
{
    public int createDraft(int drafterId, Double rating)
    {
        String sql = "INSERT INTO Draft (StartDate, Status, Rating) VALUES (GETDATE(), 'Active', ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {

            if (rating != null)
            {
                pstmt.setDouble(1, rating);
            }
            else
            {
                pstmt.setNull(1, Types.DOUBLE);
            }

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys(); // get the draft id

            if (rs.next())
            {
                int draftId = rs.getInt(1);

                System.out.println("Created new draft ID: " + draftId + " for drafter ID: " + drafterId);

                return draftId;
            }

        }
        catch (SQLException e)
        {
            System.err.println("Error creating draft: " + e.getMessage());
        }

        return -1;
    }

    public boolean saveDraftPick(int draftId, int drafterId, int playerId, int pickNumber)
    {
        String sql = "INSERT INTO Draft_Picks (DraftID, UserID, PlayerID, PickNumber, PickTime) " +
                "VALUES (?, ?, ?, ?, GETDATE())";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {

            pstmt.setInt(1, draftId);
            pstmt.setInt(2, drafterId);
            pstmt.setInt(3, playerId);
            pstmt.setInt(4, pickNumber);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        }
        catch (SQLException e)
        {
            System.err.println("Error saving draft pick: " + e.getMessage());
            return false;
        }
    }

    public boolean completeDraft(int draftId, Double rating)
    {
        String sql = "UPDATE Draft SET Status = 'Completed', EndDate = GETDATE(), Rating = ? WHERE DraftID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {

            pstmt.setDouble(1, rating);
            pstmt.setInt(1, draftId);
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error completing draft: " + e.getMessage());

            return false;
        }
    }

    public List<Map<String, Object>> getDraftsByDrafter(int drafterId)
    {
        System.out.println("=== START getDraftsByDrafter for ID: " + drafterId + " ===");
        List<Map<String, Object>> drafts = new ArrayList<>();

        // This shows ONLY drafts where this user participated
        String sql = "SELECT DISTINCT d.DraftID, d.StartDate, d.EndDate, d.Status, d.Rating " +
                "FROM Draft d " +
                "INNER JOIN Draft_Picks dp ON d.DraftID = dp.DraftID " +
                "WHERE dp.UserID = ? " +
                "ORDER BY d.StartDate DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {

            pstmt.setInt(1, drafterId);
            ResultSet rs = pstmt.executeQuery();

            int draftCount = 0;
            while (rs.next())
            {
                draftCount++;
                Map<String, Object> draft = new HashMap<>();
                int draftId = rs.getInt("DraftID");

                draft.put("draftId", draftId);
                draft.put("startDate", rs.getTimestamp("StartDate"));
                draft.put("endDate", rs.getTimestamp("EndDate"));
                draft.put("status", rs.getString("Status"));

                // Get pick count for this user in this draft
                int userPickCount = getPickCountForUserInDraft(draftId, drafterId);
                draft.put("totalPicks", userPickCount); // Actually user's picks in this draft

                // Get total picks in draft
                int totalPicksInDraft = getTotalPicksForDraft(draftId);
                draft.put("totalPicksInDraft", totalPicksInDraft);

                // Handle NULL rating
                double rating = rs.getDouble("Rating");
                if (rs.wasNull())
                {
                    draft.put("rating", null);
                }
                else
                {
                    draft.put("rating", rating);
                }

                drafts.add(draft);

                System.out.println("  User " + drafterId + " in Draft #" + draftCount +
                        ": ID=" + draftId + ", user picks=" + userPickCount +
                        ", total in draft=" + totalPicksInDraft);
            }

            System.out.println("Loaded " + draftCount + " drafts for drafter ID: " + drafterId);

        }
        catch (SQLException e)
        {
            System.err.println("ERROR in getDraftsByDrafter: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== END getDraftsByDrafter (returning " + drafts.size() + " drafts) ===");
        return drafts;
    }

    private int getPickCountForUserInDraft(int draftId, int userId)
    {
        String sql = "SELECT COUNT(*) as pickCount FROM Draft_Picks WHERE DraftID = ? AND UserID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {

            pstmt.setInt(1, draftId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                return rs.getInt("pickCount");
            }

        }
        catch (SQLException e)
        {
            System.err.println("Error counting picks for user " + userId + " in draft " + draftId + ": " + e.getMessage());
        }

        return 0;
    }

    private int getTotalPicksForDraftByUser(int draftId, int userId)
    {
        String sql = "SELECT COUNT(*) as pickCount FROM Draft_Picks WHERE DraftID = ? AND UserID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {

            pstmt.setInt(1, draftId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                return rs.getInt("pickCount");
            }

        }
        catch (SQLException e)
        {
            System.err.println("Error counting picks for draft " + draftId + " by user " + userId + ": " + e.getMessage());
        }

        return 0;
    }

    private int getTotalPicksForDraft(int draftId)
    {
        String sql = "SELECT COUNT(*) as pickCount FROM Draft_Picks WHERE DraftID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {

            pstmt.setInt(1, draftId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                return rs.getInt("pickCount");
            }

        }
        catch (SQLException e)
        {
            System.err.println("Error counting picks for draft " + draftId + ": " + e.getMessage());
        }

        return 0;
    }

    public Map<String, Object> getDrafterStats(int drafterId)
    {
        Map<String, Object> stats = new HashMap<>();

        String sql = "SELECT " +
                "COUNT(DISTINCT d.DraftID) as totalDrafts, " +
                "AVG(d.Rating) as avgRating, " +
                "MAX(d.StartDate) as lastDraftDate " +
                "FROM Draft d " +
                "JOIN Draft_Picks dp ON d.DraftID = dp.DraftID " +
                "JOIN Players p ON dp.PlayerID = p.PlayerID " +
                "WHERE dp.UserID = ? AND d.Status = 'Completed'";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {

            pstmt.setInt(1, drafterId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                stats.put("totalDrafts", rs.getInt("totalDrafts"));

                if (rs.wasNull())
                {
                    stats.put("avgRating", 0.0);
                }
                else
                {
                    stats.put("avgRating", rs.getDouble("avgRating"));
                }

                stats.put("lastDraftDate", rs.getTimestamp("lastDraftDate"));
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error getting drafter stats: " + e.getMessage());
        }

        return stats;
    }
}