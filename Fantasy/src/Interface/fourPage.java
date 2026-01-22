package Interface;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import Draft.Player;
import Repository.PlayerRepository;
import Users.Drafter;

public class fourPage extends JFrame
{
    private PlayerRepository playerRepository;
    private Random random;
    private Drafter drafter;

    private Map<String, Player> positionPlayers;
    private Map<String, RetroCardPanel> cardPlayers;

    public fourPage(Drafter drafter)
    {
        this.drafter = drafter;
        playerRepository = new PlayerRepository();
        random = new Random();
        this.positionPlayers = new HashMap<>();
        this.cardPlayers = new HashMap<>();

        setupUI();
        setupFormation();
    }

    private void setupUI()
    {
        setTitle("4-3-3 - " + drafter.getUsername());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BufferedImage background = ImageService.getBackground("field");

        JPanel mainPanel = new JPanel(new BorderLayout())
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);

                if (background != null)
                {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        mainPanel.setBackground(Color.BLACK);

        JPanel formationPanel = new JPanel();
        formationPanel.setOpaque(false);

        JPanel controlPanel  = createControlPanel();
        controlPanel.setOpaque(false);

        mainPanel.add(formationPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private void setupFormation()
    {
        JPanel formationPanel;

        formationPanel = (JPanel)((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
        formationPanel.removeAll();
        formationPanel.setOpaque(false);

        formationPanel.setLayout(new GridLayout(4,5, 20, 20));
        formationPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        formationPanel.add(createEmptyPanel());
        formationPanel.add(createEmptyCard("LW", "Left Winger"));
        formationPanel.add(createEmptyCard("ST", "Striker"));
        formationPanel.add(createEmptyCard("RW", "Right Winger"));
        formationPanel.add(createEmptyPanel());

        formationPanel.add(createEmptyPanel());
        formationPanel.add(createEmptyCard("CM", "Central Midfielder"));
        formationPanel.add(createEmptyCard("CM", "Central Midfielder"));
        formationPanel.add(createEmptyCard("CM", "Central Midfielder"));
        formationPanel.add(createEmptyPanel());

        formationPanel.add(createEmptyCard("LB", "Left Back"));
        formationPanel.add(createEmptyCard("CB", "Center Back"));
        formationPanel.add(createEmptyPanel());
        formationPanel.add(createEmptyCard("CB", "Center Back"));
        formationPanel.add(createEmptyCard("RB", "Right Back"));

        formationPanel.add(createEmptyPanel());
        formationPanel.add(createEmptyPanel());
        formationPanel.add(createEmptyCard("GK", "Goalkeeper"));
        formationPanel.add(createEmptyPanel());
        formationPanel.add(createEmptyPanel());

        formationPanel.revalidate();
        formationPanel.repaint();
    }

    private JPanel createEmptyPanel()
    {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setOpaque(false);
        return emptyPanel;
    }

    private JPanel createEmptyCard(String abrev, String name)
    {
        RetroCardPanel retroCardPanel = new RetroCardPanel();

        retroCardPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        retroCardPanel.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent evt)
            {
                cardClicked(abrev, name, retroCardPanel);
            }

            public void mouseEntered(MouseEvent evt)
            {
                retroCardPanel.setSelectedCard();
            }

            public void mouseExited(MouseEvent evt)
            {
                retroCardPanel.setNormalCard();
            }
        });

        return retroCardPanel;
    }

    private void cardClicked(String abrev, String name, JPanel placeHolder)
    {
        List<Player> randomPlayers = getRandomPlayersForPosition(name, 5);

        if (randomPlayers.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "No players");
            return;
        }

        PlayerSelectionDialog dialog = new PlayerSelectionDialog(this, name, randomPlayers);
        dialog.setVisible(true);

        Player selectedPlayer = dialog.getSelectedPlayer();

        if (selectedPlayer != null)
        {
            replaceWithPlayerCard(abrev, selectedPlayer, placeHolder);
        }
    }

    private List<Player> getRandomPlayersForPosition (String positionName, int count)
    {
        List<Player> allPlayers = getAllPlayersForPosition(positionName);

        if (allPlayers.size() <= count)
        {
            return allPlayers;
        }

        List<Player> randomPlayers = new ArrayList<>();

        List<Player> availablePlayers = new ArrayList<>(allPlayers);

        for (int i = 0; i < count; i++)
        {
            if (availablePlayers.isEmpty()) break;

            int randomIndex = random.nextInt(availablePlayers.size());
            randomPlayers.add(availablePlayers.get(randomIndex));
            availablePlayers.remove(randomIndex);
        }

        System.out.println("Random Players: " + randomPlayers);

        return randomPlayers;
    }

    private List<Player>  getAllPlayersForPosition (String positionName)
    {
        List<Player> allDefenders = playerRepository.getDefenders();
        List<Player> allMidfielders = playerRepository.getMidfielders();
        List<Player> allForwards = playerRepository.getForwards();

        switch (positionName)
        {
            case "Goalkeeper":
                return playerRepository.getGoalkeepers();

            case "Left Back":
                List<Player> leftBacks = new ArrayList<>();

                for (Player player : allDefenders)
                {
                    if (player.getPositionName().equals("Left Back"))
                    {
                        leftBacks.add(player);
                    }
                }
                return leftBacks;

            case "Center Back":
                List<Player> centerBacks = new ArrayList<>();

                for (Player player : allDefenders)
                {
                    if (player.getPositionName().equals("Center Back"))
                    {
                        centerBacks.add(player);
                    }
                }
                return centerBacks;

            case  "Right Back":
                List<Player> rightBacks = new ArrayList<>();

                for (Player player : allDefenders)
                {
                    if (player.getPositionName().equals("Right Back"))
                    {
                        rightBacks.add(player);
                    }
                }
                return rightBacks;

            case "Central Midfielder":
                return playerRepository.getMidfielders();

            case "Defensive Midfielder":
                List<Player> defMids = new ArrayList<>();

                for (Player player : allMidfielders)
                {
                    if (player.getPositionName().equals("Defensive Midfielder"))
                    {
                        defMids.add(player);
                    }
                }
                return defMids;

            case "Attacking Midfielder":
                List<Player> attackingMids = new ArrayList<>();

                for (Player player : allMidfielders)
                {
                    if (player.getPositionName().equals("Attacking Midfielder"))
                    {
                        attackingMids.add(player);
                    }
                }
                return attackingMids;

            case "Left Winger":
                List<Player> leftWings = new ArrayList<>();

                for (Player player : allForwards)
                {
                    if (player.getPositionName().equals("Left Winger"))
                    {
                        leftWings.add(player);
                    }
                }
                return leftWings;

            case "Right Winger":
                List<Player> rightWings = new ArrayList<>();

                for (Player player : allForwards)
                {
                    if (player.getPositionName().equals("Right Winger"))
                    {
                        rightWings.add(player);
                    }
                }
                return rightWings;

            case "Striker":
                List<Player> strikers = new ArrayList<>();

                for (Player player : allForwards)
                {
                    if (player.getPositionName().equals("Striker"))
                    {
                        strikers.add(player);
                    }
                }
                return strikers;

            default:
                System.out.println("Invalid Position");
                return new ArrayList<>();
        }
    }

    private void replaceWithPlayerCard(String abrev, Player player, JPanel oldPlace)
    {
        JPanel formationPanel = (JPanel) oldPlace.getParent();
        int index = -1;

        for (int i = 0; i < formationPanel.getComponentCount(); i++)
        {
            if (formationPanel.getComponent(i) == oldPlace)
            {
                index = i;
                break;
            }
        }

        if (index == -1) return;

        formationPanel.remove(index);

        RetroCardPanel playerCard = new RetroCardPanel(player);
        playerCard.setPreferredSize(new Dimension(160, 200));
        playerCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        positionPlayers.put(abrev, player);
        cardPlayers.put(abrev, playerCard);

        playerCard.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent evt)
            {
                String positionName = getPositionName(abrev);
                List<Player> randomPlayers = getRandomPlayersForPosition(positionName, 5);

                PlayerSelectionDialog dialog = new PlayerSelectionDialog(fourPage.this, positionName, randomPlayers);
                dialog.setVisible(true);

                Player newPlayer = dialog.getSelectedPlayer();
                if (newPlayer != null)
                {
                    playerCard.setPlayer(newPlayer);

                    positionPlayers.put(abrev, newPlayer);

                    updateRatingDisplay();
                }
            }
        });

        formationPanel.add(playerCard, index);

        formationPanel.revalidate();
        formationPanel.repaint();

        updateRatingDisplay();
    }

    private String getPositionName(String abrev)
    {
        switch (abrev)
        {
            case "GK": return "Goalkeeper";
            case "LB": return "Left Back";
            case "CB2": return "Center Back";
            case "RB": return "Right Back";
            case "DM": return "Defensive Midfielder";
            case "CM": return "Central Midfielder";
            case "AM": return "Attacking Midfielder";
            case "LW": return "Left Winger";
            case "RW": return "Right Winger";
            case "ST": return "Striker";
            default: return abrev;
        }
    }

    private double calculateCurrentFormationRating()
    {
        if (positionPlayers.isEmpty())
        {
            return 0.0;
        }

        List<Player> players = new ArrayList<>(positionPlayers.values());

        return drafter.calculateFormationRating(players);
    }

    private void updateRatingDisplay()
    {
        double rating = calculateCurrentFormationRating();

        Component controlPanel = ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.SOUTH);

        if (controlPanel instanceof JPanel)
        {
            for (Component comp : ((JPanel) controlPanel).getComponents())
            {
                if (comp instanceof JLabel)
                {
                    JLabel label = (JLabel) comp;
                    // Look for BOTH possible texts (with or without colon)
                    if (label.getText() != null &&
                            (label.getText().contains("Current Rating:") ||
                                    label.getText().contains("Rating")))
                    {

                        label.setText(String.format("Current Rating: %.1f/100", rating));

                        // Force UI update
                        label.revalidate();
                        label.repaint();
                        break;
                    }
                }
            }
        }
    }

    private void saveCurrentFormation()
    {
        if (positionPlayers.isEmpty())
        {
            JOptionPane.showMessageDialog(this,
                    "No players selected. Add some players to your formation first.",
                    "Empty Formation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double rating = calculateCurrentFormationRating();

        String formationName = JOptionPane.showInputDialog(this,
                "Enter a name for this formation:",
                "Save Formation", JOptionPane.QUESTION_MESSAGE);

        if (formationName == null || formationName.trim().isEmpty())
        {
            formationName = "My 4-3-3 Formation";
        }

        List<Player> players = new ArrayList<>(positionPlayers.values()); // we get all the players

        boolean saved = drafter.saveDraft(formationName, rating, players); // save the draft

        if (saved)
        {
            updateRatingDisplay();
        }
    }

    private void resetFormation()
    {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Clear all players from formation?",
                "Reset Formation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION)
        {
            positionPlayers.clear();
            cardPlayers.clear();

            setupFormation(); // put the blank formation back

            updateRatingDisplay();

            JOptionPane.showMessageDialog(this,
                    "Formation reset successfully!",
                    "Reset Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createControlPanel()
    {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(new Color(60, 60, 80, 200));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel ratingLabel = new JLabel("Current Rating: 0/100");
        ratingLabel.setForeground(Color.YELLOW);
        ratingLabel.setFont(FontLoader.getDaydreamFont(14f));
        controlPanel.add(ratingLabel);

        controlPanel.add(Box.createHorizontalStrut(40));

        JButton saveButton = new JButton("Save");
        saveButton.setFont(FontLoader.getDaydreamFont(14f));
        saveButton.setBackground(new Color(0, 150, 0));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveCurrentFormation());
        controlPanel.add(saveButton);

        JButton viewDraftsButton = new JButton("View My Drafts");
        viewDraftsButton.setFont(FontLoader.getDaydreamFont(14f));
        viewDraftsButton.setBackground(new Color(100, 100, 200));
        viewDraftsButton.setForeground(Color.WHITE);
        viewDraftsButton.addActionListener(e -> {
            drafter.viewDraftHistory();
        });
        controlPanel.add(viewDraftsButton);

        JButton resetButton = new JButton("Reset");
        resetButton.setFont(FontLoader.getDaydreamFont(14f));
        resetButton.setBackground(new Color(200, 50, 50));
        resetButton.setForeground(Color.WHITE);
        resetButton.addActionListener(e -> resetFormation());
        controlPanel.add(resetButton);

        JButton backButton = new JButton("Back to Menu");
        backButton.setFont(FontLoader.getDaydreamFont(14f));
        backButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Return to main menu? Unsaved changes will be lost.",
                    "Confirm Exit", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION)
            {
                this.dispose();

                SwingUtilities.invokeLater(() -> {
                    LoginPage loginPage = new LoginPage();
                });
            }
        });

        controlPanel.add(backButton);

        return controlPanel;
    }

}