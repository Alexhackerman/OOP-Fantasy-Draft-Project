package Users;

import Interface.FormationPage;
import javax.swing.*;
import java.util.Date;
import Draft.Player;
import Repository.DraftRepository;
import java.util.List;
import java.util.Map;

public class Drafter extends User
{
    private int totalDraftsCreated;
    private double averageFormationRating;
    private Date lastDraftDate;
    private DraftRepository draftRepo;
    private int currentDraftID = -1;

    public Drafter(int userId, String firstName, String lastName, String username, String email, String password)
    {
        super(userId, firstName, lastName, username, email, password);
        this.setRole("user");
        this.draftRepo = new DraftRepository();

        loadStatsFromData();
    }

    private void loadStatsFromData()
    {
        Map<String, Object> stats = draftRepo.getDrafterStats(this.getUserId());
        this.totalDraftsCreated = (int) stats.getOrDefault("totalDrafts", 0);
        this.averageFormationRating = (double) stats.getOrDefault("avgRating", 0.0);
        this.lastDraftDate = (Date) stats.getOrDefault("lastDraftDate", null);
    }

    @Override
    public String getDashboardTitle()
    {
        return "Draft Game - Welcome " + getFirstName();
    }

    @Override
    public void openDashboard()
    {
        SwingUtilities.invokeLater(() ->
        {
            FormationPage formationPage = new FormationPage(this);
            formationPage.setVisible(true);
            System.out.println("Drafter " + getFullName() + " started playing the draft game.");
        });
    }

    @Override
    public String getPermissionsDescription()
    {
        return "Can play draft game, create formations, save drafts, view own history";
    }

    public double calculateFormationRating(int[] playerOveralls)
    {
        if (playerOveralls == null || playerOveralls.length == 0)
        {
            return 0.0;
        }

        int sum = 0;
        int count = 0;

        for (int rating : playerOveralls)
        {
            if (rating > 0)
            {
                sum += rating;
                count++;
            }
        }

        if (count == 0) return 0.0;

        double average = (double) sum / count;

        if (count == 11)
        {
            average *= 1.05; // 5% bonus for complete formation
        }

        return Math.round(average * 10.0) / 10.0; // round
    }

    public double calculateFormationRating(List<Draft.Player> players)
    {
        if (players == null || players.isEmpty())
        {
            return 0.0;
        }

        int[] overalls = new int[players.size()];

        for (int i = 0; i < players.size(); i++)
        {
            overalls[i] = players.get(i).getOverall();
        }

        return calculateFormationRating(overalls);
    }

    public boolean saveDraft(String formationName, double formationRating, List<Draft.Player> players)
    {
        int choice = JOptionPane.showConfirmDialog(null,
                String.format("Save draft '%s' with rating %.1f?", formationName, formationRating),
                "Save Draft", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION)
        {
            currentDraftID = draftRepo.createDraft(this.getUserId(), formationRating);

            if (currentDraftID == -1)
            {
                JOptionPane.showMessageDialog(null, "Failed to create a draft!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (players != null)
            {
                for (int i = 0; i < players.size(); i++)
                {
                    Player player = players.get(i);
                    draftRepo.saveDraftPick(currentDraftID, this.getUserId(), player.getPlayerId(), i + 1);
                }
            }

            draftRepo.completeDraft(currentDraftID, formationRating);

            this.totalDraftsCreated++;
            this.lastDraftDate = new Date();
            updateAverageRating(formationRating);

            JOptionPane.showMessageDialog(null, "Draft saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            System.out.println("Drafter " + getUsername() + "saved the draft game.");
            return true;
        }

        return false;
    }

    private void updateAverageRating(double newRating)
    {
        if (totalDraftsCreated == 1)
        {
            averageFormationRating = newRating;
        }
        else
        {
            averageFormationRating = ((averageFormationRating * (totalDraftsCreated - 1)) + newRating) / totalDraftsCreated;
        }

        averageFormationRating = Math.round(averageFormationRating * 10.0) / 10.0;
    }

    public void viewDraftHistory()
    {
        List<Map<String, Object>> drafts = draftRepo.getDraftsByDrafter(this.getUserId());

        if (drafts.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "No drafts found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder history = new StringBuilder();
        history.append("=== Draft History for ").append(getFullName()).append(" ===\n\n");

        for (Map<String, Object> draft : drafts)
        {
            history.append("Draft ID: ").append(draft.get("draftId")).append("\n");
            history.append("Date: ").append(draft.get("startDate")).append("\n");
            history.append("Rating: ").append(draft.get("rating")).append("\n");
            history.append("Players: ").append(draft.get("totalPicks")).append("\n");
            history.append("------------------------\n");
        }

        history.append("\nTotal Drafts: ").append(drafts.size());

        JOptionPane.showMessageDialog(null, history.toString(), "Draft History", JOptionPane.INFORMATION_MESSAGE);
    }

    public int getTotalDraftsCreated()
    {
        return totalDraftsCreated;
    }

    public double getAverageFormationRating()
    {
        return averageFormationRating;
    }

    public Date getLastDraftDate()
    {
        return lastDraftDate;
    }

    @Override
    public String toString()
    {
        return super.toString() + " [Drafts: " + totalDraftsCreated +
                ", Avg Rating: " + averageFormationRating + "]";
    }
}