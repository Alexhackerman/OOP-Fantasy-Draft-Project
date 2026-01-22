package Interface;

import java.sql.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import Repository.DatabaseConfig;
import Repository.DraftRepository;
import Repository.PlayerRepository;
import Repository.UserRepository;

import Users.User;
import Users.Admin;
import Draft.Player;

public class AdminPage extends JFrame
{
    private Admin admin;
    private UserRepository userRepository;
    private PlayerRepository playerRepository;
    private DraftRepository draftRepository;

    public AdminPage(Admin admin)
    {
        this.admin = admin;
        this.userRepository = new UserRepository();
        this.playerRepository = new PlayerRepository();
        this.draftRepository = new DraftRepository();
        initializeUI();
    }

    private void initializeUI()
    {
        setTitle("Admin Dashboard - " + admin.getFullName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(50, 50, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("ADMIN DASHBOARD");
        title.setFont(FontLoader.getDaydreamFont(14f));
        title.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("User: " + admin.getUsername());
        userLabel.setFont(FontLoader.getDaydreamFont(12f));
        userLabel.setForeground(Color.LIGHT_GRAY);

        headerPanel.add(title);
        headerPanel.add(Box.createHorizontalStrut(30));
        headerPanel.add(userLabel);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        buttonPanel.setBackground(new Color(240, 240, 240));

        buttonPanel.add(createButton("Manage Users", Color.decode("#3498db"), e -> manageUsers()));
        buttonPanel.add(createButton("Manage Players", Color.decode("#2ecc71"), e -> managePlayers()));
        buttonPanel.add(createButton("View Drafts", Color.decode("#e74c3c"), e -> viewDrafts()));
        buttonPanel.add(createButton("System Stats", Color.decode("#9b59b6"), e -> showStats()));
        buttonPanel.add(createButton("Logout", Color.decode("#e67e22"), e -> logout()));

        JPanel statsPanel = createQuickStatsPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(statsPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JPanel createQuickStatsPanel()
    {
        JPanel quickStatsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        quickStatsPanel.setBorder(BorderFactory.createTitledBorder("Quick Stats"));

        JLabel userCountLabel = new JLabel("Users: " + getCount("SELECT COUNT(*) FROM Users"));
        JLabel playerCountLabel = new JLabel("Players: " + getCount("SELECT COUNT(*) FROM Players"));
        JLabel draftCountLabel = new JLabel("Drafts: " + getCount("SELECT COUNT(DISTINCT DraftID) FROM Draft_Picks"));
        JLabel pickCountLabel = new JLabel("Picks: " + getCount("SELECT COUNT(*) FROM Draft_Picks"));
        JLabel activeUserLabel = new JLabel("Active Users: " + getCount("SELECT COUNT(DISTINCT UserID) FROM Draft_Picks"));

        int totalDrafts = getCount("SELECT COUNT(DISTINCT DraftID) FROM Draft_Picks");
        int totalPicks = getCount("SELECT COUNT(*) FROM Draft_Picks");
        String avgPicks = totalDrafts > 0 ? String.format("%.1f", (double)totalPicks/totalDrafts) : "0.0";
        JLabel avgPicksLabel = new JLabel("Avg Picks/Draft: " + avgPicks);

        quickStatsPanel.add(userCountLabel);
        quickStatsPanel.add(playerCountLabel);
        quickStatsPanel.add(draftCountLabel);
        quickStatsPanel.add(pickCountLabel);
        quickStatsPanel.add(activeUserLabel);
        quickStatsPanel.add(avgPicksLabel);

        return quickStatsPanel;
    }

    private JButton createButton(String text, Color color, java.awt.event.ActionListener action)
    {
        JButton button = new JButton(text);
        button.setFont(FontLoader.getDaydreamFont(14f));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        button.addActionListener(action);

        button.addMouseListener(new java.awt.event.MouseAdapter()
        {
            Color original = color;

            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                button.setBackground(original.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                button.setBackground(original);
            }
        });

        return button;
    }

    private void manageUsers()
    {
        String[] options = {"View All Users", "Add New User", "Edit User", "Delete User", "Back"};

        int choice = JOptionPane.showOptionDialog(this, "Select action:", "User Management",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        switch(choice)
        {
            case 0: showAllUsersFromDB(); break;
            case 1: addNewUserToDB(); break;
            case 2: editUserInDB(); break;
            case 3: deleteUserFromDB(); break;
        }
    }

    private void managePlayers()
    {
        String[] options = {"View All Players", "Add Player", "Edit Player", "Delete Player", "Back"};

        int choice = JOptionPane.showOptionDialog(this, "Select action:", "Player Management",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        switch(choice)
        {
            case 0: showAllPlayersFromDB(); break;
            case 1: addNewPlayerToDB(); break;
            case 2: editPlayerInDB(); break;
            case 3: deletePlayerFromDB(); break;
        }
    }

    private void viewDrafts()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ALL DRAFTS IN DATABASE ===\n\n");

        String sql = "SELECT d.DraftID, d.StartDate, d.Status, d.Rating, " +
                "COUNT(dp.PickID) as TotalPicks " +
                "FROM Draft d " +
                "LEFT JOIN Draft_Picks dp ON d.DraftID = dp.DraftID " +
                "GROUP BY d.DraftID, d.StartDate, d.Status, d.Rating " +
                "ORDER BY d.DraftID DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {

            int count = 0;
            while (rs.next())
            {
                count++;
                int draftId = rs.getInt("DraftID");
                Timestamp startDate = rs.getTimestamp("StartDate");
                String status = rs.getString("Status");
                int picks = rs.getInt("TotalPicks");
                double rating = rs.getDouble("Rating");
                boolean hasRating = !rs.wasNull();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateStr = startDate != null ? sdf.format(startDate) : "N/A";

                sb.append("Draft #").append(draftId)
                        .append(" - ").append(dateStr)
                        .append(" (").append(status).append(")")
                        .append(" - ").append(picks).append(" picks");

                if (hasRating)
                {
                    sb.append(" - Rating: ").append(String.format("%.1f", rating));
                }

                sb.append("\n");
            }

            if (count == 0)
            {
                sb.append("No drafts found in database.\n");
            }
            else
            {
                sb.append("\nTotal: ").append(count).append(" drafts");
            }

        }
        catch (SQLException e)
        {
            sb.append("Error loading drafts: ").append(e.getMessage());
            e.printStackTrace();
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "All Drafts",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showStats()
    {
        StringBuilder stats = new StringBuilder();
        stats.append("=== DATABASE STATISTICS ===\n\n");

        try
        {
            stats.append("USERS:\n"); //User statistics

            int totalUsers = getCount("SELECT COUNT(*) FROM Users");
            int totalAdmins = getCount("SELECT COUNT(*) FROM Users WHERE Role = 'Admin'");
            int usersWithDrafts = getCount("SELECT COUNT(DISTINCT UserID) FROM Draft_Picks");

            stats.append("  • Total Users: ").append(totalUsers).append("\n");
            stats.append("  • Admins: ").append(totalAdmins).append("\n");
            stats.append("  • Regular Users: ").append(totalUsers - totalAdmins).append("\n");
            stats.append("  • Users Who Drafted: ").append(usersWithDrafts).append("\n\n");

            stats.append("PLAYERS:\n"); //Players statistics

            int totalPlayers = getCount("SELECT COUNT(*) FROM Players");
            int maxOverall = getCount("SELECT MAX(Overall) FROM Players");
            int minOverall = getCount("SELECT MIN(Overall) FROM Players");
            double avgOverall = getAverage("SELECT AVG(Overall) FROM Players");

            stats.append("  • Total Players: ").append(totalPlayers).append("\n");
            stats.append("  • Highest Rating: ").append(maxOverall).append("\n");
            stats.append("  • Lowest Rating: ").append(minOverall).append("\n");
            stats.append("  • Average Rating: ").append(String.format("%.1f", avgOverall)).append("\n\n");

            stats.append("DRAFTS:\n"); //Drfats statistics

            int totalDrafts = getCount("SELECT COUNT(DISTINCT DraftID) FROM Draft_Picks");
            int totalPicks = getCount("SELECT COUNT(*) FROM Draft_Picks");

            stats.append("  • Total Drafts: ").append(totalDrafts).append("\n");
            stats.append("  • Total Picks Made: ").append(totalPicks).append("\n");

            if (totalDrafts > 0)
            {
                double avgPicksPerDraft = (double) totalPicks / totalDrafts;

                stats.append("  • Average Picks/Draft: ").append(String.format("%.1f", avgPicksPerDraft)).append("\n");

                int maxPicks = getCount("SELECT MAX(PickCount) FROM (" +
                        "SELECT COUNT(*) as PickCount FROM Draft_Picks GROUP BY DraftID) as counts");
                stats.append("  • Most Picks in a Draft: ").append(maxPicks).append("\n");
            }

            stats.append("\nEXISTING DRAFT IDs:\n");
            String draftIds = getDraftIds();
            if (!draftIds.isEmpty())
            {
                stats.append("  ").append(draftIds).append("\n");
            }
            else
            {
                stats.append("  No drafts found in database\n");
            }

        }
        catch (Exception e)
        {
            stats.append("\nError loading statistics: ").append(e.getMessage());
            e.printStackTrace();
        }

        JTextArea textArea = new JTextArea(stats.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Database Statistics",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private int getCount(String sql)
    {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {

            if (rs.next())
            {
                return rs.getInt(1);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error in getCount: " + e.getMessage());
        }

        return 0;
    }

    private double getAverage(String sql)
    {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {

            if (rs.next())
            {
                return rs.getDouble(1);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error in getAverage: " + e.getMessage());
        }
        return 0.0;
    }

    private String getDraftIds()
    {
        StringBuilder ids = new StringBuilder();

        String sql = "SELECT DISTINCT DraftID FROM Draft_Picks ORDER BY DraftID";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {

            int count = 0;
            while (rs.next())
            {
                if (count > 0) ids.append(", ");
                ids.append(rs.getInt(1));
                count++;
            }

            if (count == 0)
            {
                return "";
            }

        }
        catch (SQLException e)
        {
            System.err.println("Error getting draft IDs: " + e.getMessage());
            return "";
        }

        return ids.toString();
    }

    private void logout()
    {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Logout and return to login screen?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION)
        {
            this.dispose();

            JOptionPane.showMessageDialog(null, "Logged out successfully!", "Logout",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showAllUsersFromDB()
    {
        List<User> users = userRepository.getAllUsers();

        StringBuilder sb = new StringBuilder();
        sb.append("=== ALL USERS ===\n\n");

        for (User user : users)
        {
            sb.append("ID: ").append(user.getUserId())
                    .append(" | Name: ").append(user.getFullName())
                    .append(" | Username: ").append(user.getUsername())
                    .append(" | Role: ").append(user.getRole())
                    .append(" | Email: ").append(user.getEmail())
                    .append("\n");
        }

        sb.append("\nTotal: ").append(users.size()).append(" users");

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "All Users", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addNewUserToDB()
    {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

        panel.add(new JLabel("First Name:"));
        JTextField firstName = new JTextField();
        panel.add(firstName);

        panel.add(new JLabel("Last Name:"));
        JTextField lastName = new JTextField();
        panel.add(lastName);

        panel.add(new JLabel("Username:"));
        JTextField username = new JTextField();
        panel.add(username);

        panel.add(new JLabel("Email:"));
        JTextField email = new JTextField();
        panel.add(email);

        panel.add(new JLabel("Password:"));
        JPasswordField password = new JPasswordField();
        panel.add(password);

        panel.add(new JLabel("Role:"));
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"user", "admin"});
        panel.add(roleCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION)
        {
            try
            {
                // Check if username exists
                if (userRepository.usernameExists(username.getText()))
                {
                    JOptionPane.showMessageDialog(this, "Username already exists!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create user based on role
                Users.User newUser;
                if ("admin".equals(roleCombo.getSelectedItem()))
                {
                    newUser = new Users.Admin(0, firstName.getText(), lastName.getText(),
                            username.getText(), email.getText(), new String(password.getPassword()));
                }
                else
                {
                    newUser = new Users.Drafter(0, firstName.getText(), lastName.getText(),
                            username.getText(), email.getText(), new String(password.getPassword()));
                }

                // Add to database
                boolean success = userRepository.addUser(newUser);

                if (success)
                {
                    JOptionPane.showMessageDialog(this, "User added successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Failed to add user!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editUserInDB()
    {
        String userIdStr = JOptionPane.showInputDialog(this, "Enter User ID to edit:", "Edit User",
                JOptionPane.QUESTION_MESSAGE);

        if (userIdStr != null && !userIdStr.trim().isEmpty())
        {
            try
            {
                int userId = Integer.parseInt(userIdStr);
                Users.User user = userRepository.getUserById(userId);

                if (user == null)
                {
                    JOptionPane.showMessageDialog(this, "User not found!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

                panel.add(new JLabel("First Name:"));
                JTextField firstName = new JTextField(user.getFirstName());
                panel.add(firstName);

                panel.add(new JLabel("Last Name:"));
                JTextField lastName = new JTextField(user.getLastName());
                panel.add(lastName);

                panel.add(new JLabel("Email:"));
                JTextField email = new JTextField(user.getEmail());
                panel.add(email);

                panel.add(new JLabel("New Password (leave empty to keep current):"));
                JPasswordField password = new JPasswordField();
                panel.add(password);

                panel.add(new JLabel("Role:"));
                JComboBox<String> roleCombo = new JComboBox<>(new String[]{"user", "admin"});
                roleCombo.setSelectedItem(user.getRole());
                panel.add(roleCombo);

                int result = JOptionPane.showConfirmDialog(this, panel, "Edit User ID: " + userId,
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION)
                {
                    user.setFirstName(firstName.getText());
                    user.setLastName(lastName.getText());
                    user.setEmail(email.getText());
                    user.setRole((String) roleCombo.getSelectedItem());

                    // Only update password if provided
                    String newPassword = new String(password.getPassword());
                    if (!newPassword.isEmpty())
                    {
                        user.setPassword(newPassword);
                    }

                    boolean success = userRepository.updateUser(user);

                    if (success)
                    {
                        JOptionPane.showMessageDialog(this, "User updated successfully!", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this, "Failed to update user!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
            catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(this, "Invalid User ID!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteUserFromDB()
    {
        String userIdStr = JOptionPane.showInputDialog(this, "Enter User ID to delete:", "Delete User",
                JOptionPane.WARNING_MESSAGE);

        if (userIdStr != null && !userIdStr.trim().isEmpty())
        {
            try
            {
                int userId = Integer.parseInt(userIdStr);

                // Don't allow deleting yourself
                if (userId == admin.getUserId())
                {
                    JOptionPane.showMessageDialog(this, "Cannot delete your own account!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete user ID " + userId + "?\nThis action cannot be undone!",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION)
                {
                    boolean success = userRepository.deleteUser(userId);

                    if (success)
                    {
                        JOptionPane.showMessageDialog(this, "User deleted successfully!", "Deleted",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this, "Failed to delete user!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
            catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(this, "Invalid User ID!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAllPlayersFromDB()
    {
        List<Draft.Player> players = playerRepository.getAllPlayers();

        String[] columns = {"ID", "Name", "Position", "Rating", "Team", "Country"};
        Object[][] data = new Object[players.size()][6];

        for (int i = 0; i < players.size(); i++)
        {
            Draft.Player p = players.get(i);
            data[i][0] = p.getPlayerId();
            data[i][1] = p.getFullName();
            data[i][2] = p.getPositionName();
            data[i][3] = p.getOverall();
            data[i][4] = p.getTeamName();
            data[i][5] = p.getCountry();
        }

        JTable table = new JTable(data, columns);
        table.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "All Players (" + players.size() + ")",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void addNewPlayerToDB()
    {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));

        panel.add(new JLabel("First Name:"));
        JTextField FirstName = new JTextField();
        panel.add(FirstName);

        panel.add(new JLabel("Last Name:"));
        JTextField LastName = new JTextField();
        panel.add(LastName);

        panel.add(new JLabel("Age:"));
        JTextField Age = new JTextField();
        panel.add(Age);

        panel.add(new JLabel("Country:"));
        JTextField Country = new JTextField();
        panel.add(Country);

        panel.add(new JLabel("Overall:"));
        JTextField Overall = new JPasswordField();
        panel.add(Overall);

        panel.add(new JLabel("Team ID:"));
        JTextField TeamID = new JTextField();
        panel.add(TeamID);

        panel.add(new JLabel("Position ID:"));
        JTextField PositionID = new JTextField();
        panel.add(PositionID);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Player",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION)
        {
            int age;
            int overall;
            int teamId;
            int positionId;

            try
            {
                age = Integer.parseInt(Age.getText().trim());
                overall = Integer.parseInt(Overall.getText().trim());
                teamId = Integer.parseInt(TeamID.getText().trim());
                positionId = Integer.parseInt(PositionID.getText().trim());

                if (age < 16 || age > 50)
                {
                    JOptionPane.showMessageDialog(this, "Age must be between 16 and 50!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (overall < 0 || overall > 100)
                {
                    JOptionPane.showMessageDialog(this, "Overall must be between 0 and 100!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

            } catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(this,
                        "Age, Overall, Team ID and Position ID must be valid numbers!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Player newPlayer = new Player(FirstName.getText().trim(), LastName.getText().trim(), age,
                    Country.getText().trim(), overall, teamId, positionId);

            boolean success = playerRepository.addPlayer(newPlayer);

            if (success)
            {
                JOptionPane.showMessageDialog(this, "Player added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                // Optionally clear fields or refresh player list if you have one
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Failed to add player!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    private void editPlayerInDB()
    {
        String playerIdStr = JOptionPane.showInputDialog(this, "Enter Player ID to edit rating:",
                "Edit Player", JOptionPane.QUESTION_MESSAGE);

        if (playerIdStr != null && !playerIdStr.trim().isEmpty())
        {
            try
            {
                int playerId = Integer.parseInt(playerIdStr);

                // Get current player
                Draft.Player player = playerRepository.getPlayerById(playerId);
                if (player == null)
                {
                    JOptionPane.showMessageDialog(this, "Player not found!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
                panel.add(new JLabel("Player:"));
                panel.add(new JLabel(player.getFullName()));

                panel.add(new JLabel("Current Rating:"));
                panel.add(new JLabel(String.valueOf(player.getOverall())));

                panel.add(new JLabel("New Rating (0-99):"));
                JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(player.getOverall(), 0, 99, 1));
                panel.add(ratingSpinner);

                int result = JOptionPane.showConfirmDialog(this, panel, "Edit Player Rating",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION)
                {
                    int newRating = (Integer) ratingSpinner.getValue();
                    player.setOverall(newRating);

                    boolean success = playerRepository.updatePlayer(player);

                    if (success)
                    {
                        JOptionPane.showMessageDialog(this,
                                "Updated " + player.getFullName() + " to rating " + newRating,
                                "Updated", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this, "Update failed!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
            catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(this, "Invalid Player ID!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deletePlayerFromDB()
    {
        String playerIdStr = JOptionPane.showInputDialog(this, "Enter Player ID to delete:",
                "Delete Player", JOptionPane.WARNING_MESSAGE);

        if (playerIdStr != null && !playerIdStr.trim().isEmpty())
        {
            try
            {
                int playerId = Integer.parseInt(playerIdStr);

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Delete player ID " + playerId + "?\nWarning: This will affect all drafts containing this player!",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION)
                {
                    boolean success = playerRepository.deletePlayer(playerId);

                    if (success)
                    {
                        JOptionPane.showMessageDialog(this, "Player deleted!", "Deleted",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this, "Delete failed!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
            catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(this, "Invalid Player ID!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}