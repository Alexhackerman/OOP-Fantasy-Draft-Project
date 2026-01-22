package Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import Draft.Player;

public class RetroCardPanel extends JPanel
{
    private Player player;

    // Images
    private BufferedImage cardImage;
    private BufferedImage faceImage;
    private BufferedImage teamImage;
    private BufferedImage flagImage;

    // Constants
    private final int CARD_WIDTH = 180;
    private final int CARD_HEIGHT = 210;

    // 1. SIMPLIFIED CONSTRUCTORS
    public RetroCardPanel()
    {
        this(null); // Chain to the main constructor
    }

    public RetroCardPanel(Player player)
    {
        this.player = player;

        this.setOpaque(false);
        this.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        //this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 0, true));
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        updateImages();
    }

    private void updateImages()
    {
        cardImage = ImageService.getCardImage();

        if (player != null)
        {
            faceImage = ImageService.getFaceImage(player);
            teamImage = ImageService.getTeamImage(player);
            flagImage = ImageService.getFlagImage(player);
        }
        else
        {
            faceImage = null;
            teamImage = null;
            flagImage = null;
        }
    }

    public void setPlayer(Player player)
    {
        this.player = player;
        updateImages();
        repaint();
    }

    public Player getPlayer()
    {
        return player;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Optimize rendering quality
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int xOffset = (panelWidth - CARD_WIDTH) / 2;
        int yOffset = (panelHeight - CARD_HEIGHT) / 2;

        g2.translate(xOffset, yOffset);

        // A. Draw Background
        if (cardImage != null)
        {
            g2.drawImage(cardImage, 0, 0, CARD_WIDTH, CARD_HEIGHT, null);
        }
        else
        {
            g2.setColor(new Color(40, 40, 60));
            g2.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT);
        }

        if (player == null)
        {
            drawPlusSign(g2);
        }
        else
        {
            // Draw Images
            if (faceImage != null) g2.drawImage(faceImage, 80, 30, 80, 120, null); // Adjusted Y
            if (teamImage != null) g2.drawImage(teamImage, 30, 115, 30, 30, null);
            if (flagImage != null) g2.drawImage(flagImage, 27, 90, 35, 20, null);

            // Draw Text
            drawPlayerInfo(g2);
        }
    }

    private void drawPlusSign(Graphics2D g2)
    {
        int cx = CARD_WIDTH / 2;
        int cy = CARD_HEIGHT / 2;
        int size = 12;

        g2.setColor(Color.YELLOW);
        g2.setStroke(new BasicStroke(4));
        g2.drawLine(cx - size, cy, cx + size, cy);
        g2.drawLine(cx, cy - size, cx, cy + size);
    }

    private void drawPlayerInfo(Graphics2D g2)
    {
        // Position (Top Left-ish)
        g2.setFont(FontLoader.getDaydreamFont(14f));
        String pos = player.getPositionAbrev();
        drawTextWithShadow(g2, pos, 27, 80, Color.CYAN);

        // Overall (Large, Left)
        g2.setFont(FontLoader.getDaydreamFont(28f));
        String ovr = String.valueOf(player.getOverall());
        drawTextWithShadow(g2, ovr, 20, 50, Color.WHITE);

        // Name (Centered Bottom)
        g2.setFont(FontLoader.getDaydreamFont(18f));
        String name = player.getLastName(); // Last name usually fits better

        FontMetrics fm = g2.getFontMetrics();
        int nameX = (CARD_WIDTH - fm.stringWidth(name)) / 2;
        drawTextWithShadow(g2, name, nameX, 175, Color.WHITE);
    }

    public void setSelectedCard()
    {
        this.cardImage = ImageService.getSelectedCard();
        repaint();
    }

    public void setNormalCard()
    {
        this.cardImage = ImageService.getCardImage();
        repaint();
    }

    private void drawTextWithShadow(Graphics2D g2, String text, int x, int y, Color color)
    {
        g2.setColor(Color.BLACK);
        g2.drawString(text, x + 2, y + 2); // Slightly deeper shadow
        g2.setColor(color);
        g2.drawString(text, x, y);
    }
}