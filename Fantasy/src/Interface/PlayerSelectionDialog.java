package Interface;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import Draft.Player;

public class PlayerSelectionDialog extends JDialog
{
    private Player selectedPlayer;

    public PlayerSelectionDialog(JFrame parent, String positionName, List<Player> players)
    {
        super(parent, "Select Player", true);
        setupUI(positionName, players);
        setLocationRelativeTo(parent);
    }

    private void setupUI(String positionName,  List<Player> players)
    {
        setSize(1200, 550);
        setLayout(new BorderLayout(10, 10));

        JLabel headerLabel = new JLabel("Select a Player", SwingConstants.CENTER);

        headerLabel.setFont(FontLoader.getDaydreamFont(20f));
        headerLabel.setForeground(Color.LIGHT_GRAY);
        headerLabel.setOpaque(true);
        headerLabel.setBackground(Color.DARK_GRAY);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(headerLabel, BorderLayout.NORTH);

        JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        cardPanel.setBackground(Color.DARK_GRAY);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        int playerCount = Math.min(players.size(), 5);

        for (int i = 0; i < playerCount; i++)
        {
            Player player = players.get(i);
            RetroCardPanel card = new RetroCardPanel(player);

            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new java.awt.event.MouseAdapter()
            {
                public void mouseClicked(java.awt.event.MouseEvent evt)
                {
                    selectedPlayer = player;
                    dispose();
                }

                public void mouseEntered(java.awt.event.MouseEvent evt)
                {
                    card.setSelectedCard();
                }

                public void mouseExited(java.awt.event.MouseEvent evt)
                {
                    card.setNormalCard();
                }
            });

            cardPanel.add(card);
        }

        if (playerCount == 0)
        {
            JLabel noPlayerLabel = new JLabel("No Players Available", SwingConstants.CENTER);
            noPlayerLabel.setForeground(Color.WHITE);
            noPlayerLabel.setFont(FontLoader.getDaydreamFont(20f));
            cardPanel.add(noPlayerLabel);
        }

        add(new JScrollPane(cardPanel), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.DARK_GRAY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(FontLoader.getDaydreamFont(10f));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> {
            selectedPlayer = null;
            dispose();
        });

        bottomPanel.add(cancelButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public Player getSelectedPlayer()
    {
        return selectedPlayer;
    }

}