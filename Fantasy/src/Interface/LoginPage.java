package Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import Repository.UserRepository;
import Users.Drafter;
import Users.User;

public class LoginPage implements ActionListener
{
    JFrame frame = new JFrame("Fantasy Draft");

    JButton loginButton = new JButton("Login");
    JButton createButton = new JButton("Create Account");

    JTextField username = new JTextField();
    JPasswordField password = new JPasswordField();

    JLabel usernameLabel = new JLabel("Username ");
    JLabel passwordLabel = new JLabel("Password ");
    JLabel messageLabel = new JLabel();
    JLabel titleLable = new JLabel("Fantasy Draft");

    private UserRepository userRepository;

    public LoginPage()
    {
        userRepository = new UserRepository();
        setBackground();
        setupUI();
    }

    private void setBackground()
    {
        try
        {
            String backgroundName = "backLog";
            BufferedImage backgroundImage = ImageService.getBackground(backgroundName);

            if (backgroundImage != null)
            {
                // Convert BufferedImage to ImageIcon
                Image scaledImage = backgroundImage.getScaledInstance(1920, 1080, Image.SCALE_SMOOTH);
                ImageIcon backgroundIcon = new ImageIcon(scaledImage);

                JLabel backgroundLabel = new JLabel(backgroundIcon);
                backgroundLabel.setBounds(0, 0, 1200, 800);

                frame.setContentPane(backgroundLabel);
                frame.setLayout(new BorderLayout());

                System.out.println("Background loaded successfully via ImageService");
            }
            else
            {
                throw new Exception("ImageService returned null");
            }

        }
        catch (Exception e)
        {
            System.out.println("Error loading background via ImageService: " + e.getMessage());
        }
    }

    private void setupUI()
    {
        usernameLabel.setBounds(740, 250, 200, 30);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(FontLoader.getDaydreamFont(10f));
        passwordLabel.setBounds(740, 300, 200, 30);
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(FontLoader.getDaydreamFont(10f));

        messageLabel.setBounds(850, 400, 250, 35);
        messageLabel.setFont(FontLoader.getDaydreamFont(20f));

        titleLable.setBounds(510, 0, 1500, 200);
        titleLable.setForeground(Color.WHITE);
        titleLable.setFont(FontLoader.getDaydreamFont(70f));

        username.setBounds(830, 250, 260, 30);
        password.setBounds(830, 300, 260, 30);

        loginButton.setBounds(830, 350, 120, 30);
        loginButton.setFont(FontLoader.getDaydreamFont(14f));
        loginButton.setFocusable(false);
        loginButton.addActionListener(this);

        createButton.setBounds(970, 350, 120, 30);
        createButton.setFont(FontLoader.getDaydreamFont(10f));
        createButton.setFocusable(false);
        createButton.addActionListener(this);

        frame.add(usernameLabel);
        frame.add(passwordLabel);
        frame.add(messageLabel);
        frame.add(titleLable);
        frame.add(username);
        frame.add(password);
        frame.add(loginButton);
        frame.add(createButton);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == createButton)
        {
            CreationPage Creationpage = new CreationPage();
        }

        if (e.getSource() == loginButton)
        {
            String userID = username.getText();
            String pass = String.valueOf(password.getPassword());

            if (userID.isEmpty() || pass.isEmpty())
            {
                messageLabel.setForeground(Color.red);
                messageLabel.setText("Please enter your username and password");
                return;
            }

            User user = userRepository.authenticate(userID, pass);

            if (user != null)
            {
                messageLabel.setForeground(Color.green);
                messageLabel.setFont(FontLoader.getDaydreamFont(20f));
                messageLabel.setText("Logged successfully");

                Timer timer = new Timer(500, ev -> {
                    frame.dispose();

                    if (user.isDrafter())
                    {
                        Drafter drafter = (Drafter) user;
                        FormationPage  Formationpage = new FormationPage(drafter);
                    }
                    else if (user.isAdmin())
                    {
                        user.openDashboard();
                    }
                });

                timer.setRepeats(false);
                timer.start();
            }
            else
            {
                messageLabel.setForeground(Color.red);
                messageLabel.setFont(FontLoader.getDaydreamFont(20f));
                messageLabel.setText("Invalid username or password");
            }
        }
    }
}
