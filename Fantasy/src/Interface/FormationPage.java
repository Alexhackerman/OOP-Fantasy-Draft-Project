package Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import Users.Drafter;

public class FormationPage extends JFrame implements ActionListener
{

    private HashMap<String, ImageIcon> imageCache = new HashMap<>();
    private JLabel formationImageLabel;
    private Drafter drafter;

    private JButton btn433;
    private JButton btn352;
    private JButton btn523;

    public FormationPage(Drafter drafter)
    {
        this.drafter = drafter;
        setTitle("Formation Selection");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(4, 1));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 5, Color.BLACK));
        leftPanel.setBackground(Color.WHITE);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBackground(Color.DARK_GRAY);

        formationImageLabel = new JLabel("Hover over a formaton to preview");
        formationImageLabel.setFont(FontLoader.getDaydreamFont(20f));
        rightPanel.add(formationImageLabel);

        JLabel headerLabel = new JLabel("Choose your formation", SwingConstants.CENTER);
        headerLabel.setFont(FontLoader.getDaydreamFont(25f));
        leftPanel.add(headerLabel);

        btn433 = createFormationButton("4-3-3");
        btn352 = createFormationButton("3-5-2");
        btn523 = createFormationButton("5-2-3");

        btn433.addActionListener(this);
        btn352.addActionListener(this);
        btn523.addActionListener(this);

        leftPanel.add(btn433);
        leftPanel.add(btn352);
        leftPanel.add(btn523);

        add(leftPanel);
        add(rightPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JButton createFormationButton(String text)
    {
        JButton btn = new JButton(text);
        btn.setFont(FontLoader.getDaydreamFont(20f));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);

        btn.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, Color.BLACK));

        btn.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                updateFormationPreview(text);
                btn.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });

        return btn;
    }

    private void updateFormationPreview(String formationName)
    {

        formationImageLabel.setText("Showing formation: " + formationName);

        BufferedImage backgroundImage = ImageService.getBackground(formationName);

        Image scaledImage = backgroundImage.getScaledInstance(1000, 600, Image.SCALE_SMOOTH);
        ImageIcon backgroundIcon = new ImageIcon(scaledImage);

        if (backgroundIcon != null)
        {
            formationImageLabel.setText(formationName);
            formationImageLabel.setIcon(backgroundIcon);
        }
        else
        {
            formationImageLabel.setText("Image not found");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String selectedFormation = "";

        if (e.getSource() == btn433)
        {
            selectedFormation = "4-3-3";
        }
        else if (e.getSource() == btn352)
        {
            selectedFormation = "3-5-2";
        }
        else if (e.getSource() == btn523)
        {
            selectedFormation = "5-2-3";
        }

        openDraftingPage(selectedFormation);
    }

    private void openDraftingPage(String formationName)
    {
        this.dispose();

        if (drafter == null)
        {
            JOptionPane.showMessageDialog(this, "Error: Drafter is null", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if  (formationName.equals("4-3-3"))
        {
            fourPage fourPage = new fourPage(drafter);
        }
        else if (formationName.equals("3-5-2"))
        {
            threePage threePage = new threePage(drafter);
        }
        else if (formationName.equals("5-2-3"))
        {
            fivePage fivePage = new fivePage(drafter);
        }
    }
}
