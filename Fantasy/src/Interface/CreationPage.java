package Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import Repository.UserRepository;
import Users.Drafter;

public class CreationPage implements ActionListener
{

    JFrame frame = new JFrame("Creation Page");

    private JButton finishButton = new JButton("Finish");
    private JButton backButton = new JButton("Back to login");

    private JTextField firstNameField = new JTextField();
    private JTextField lastNameField = new JTextField();
    private JTextField userNameField = new JTextField();
    private JTextField emailField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();

    private JLabel firstNameLabel = new JLabel("First Name ");
    private JLabel lastNameLabel = new JLabel("Last Name ");
    private JLabel userNameLabel = new JLabel("User Name ");
    private JLabel emailLabel = new JLabel("Email ");
    private JLabel passwordLabel = new JLabel("Password ");
    private JLabel messageLabel = new JLabel("");

    private UserRepository userRepository;

    public CreationPage()
    {
        userRepository = new UserRepository(); // Initialize repository
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
                //Convert BufferedImage to ImageIcon
                Image scaledImage = backgroundImage.getScaledInstance(1200, 800, Image.SCALE_SMOOTH);
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
        firstNameField.setBounds(475, 250, 200, 30);
        lastNameField.setBounds(475, 290, 200, 30);
        userNameField.setBounds(475, 330, 200, 30);
        emailField.setBounds(475, 370, 200, 30);
        passwordField.setBounds(475, 410, 200, 30);

        firstNameLabel.setBounds(375, 250, 200, 30);
        firstNameLabel.setForeground(Color.WHITE);
        firstNameLabel.setFont(FontLoader.getDaydreamFont(10f));

        lastNameLabel.setBounds(375, 290, 200, 30);
        lastNameLabel.setForeground(Color.WHITE);
        lastNameLabel.setFont(FontLoader.getDaydreamFont(10f));

        userNameLabel.setBounds(375, 330, 200, 30);
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setFont(FontLoader.getDaydreamFont(10f));

        emailLabel.setBounds(375, 370, 200, 30);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(FontLoader.getDaydreamFont(10f));

        passwordLabel.setBounds(375, 410, 200, 30);
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(FontLoader.getDaydreamFont(10f));

        finishButton.setBounds(500, 450, 150, 30);
        finishButton.setFont(FontLoader.getDaydreamFont(14f));
        finishButton.setForeground(Color.BLACK);
        finishButton.addActionListener(this);

        backButton.setBounds(500, 500, 150, 30); // Moved up a bit
        backButton.setFont(FontLoader.getDaydreamFont(8f));
        backButton.setForeground(Color.BLACK);
        backButton.addActionListener(this);

        messageLabel.setBounds(400, 550, 300, 30);
        messageLabel.setForeground(Color.RED);
        messageLabel.setFont(FontLoader.getDaydreamFont(12f));

        frame.add(firstNameField);
        frame.add(lastNameField);
        frame.add(userNameField);
        frame.add(emailField);
        frame.add(passwordField);
        frame.add(firstNameLabel);
        frame.add(lastNameLabel);
        frame.add(userNameLabel);
        frame.add(emailLabel);
        frame.add(passwordLabel);
        frame.add(finishButton);
        frame.add(backButton);
        frame.add(messageLabel);

        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    private void createNewAccount()
    {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = userNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() ||
                email.isEmpty() || password.isEmpty())
        {
            messageLabel.setText("Please fill in all fields!");
            return;
        }

        if (password.length() < 6)
        {
            messageLabel.setText("Password must be at least 6 characters!");
            return;
        }

        if (!email.contains("@") || !email.contains("."))
        {
            messageLabel.setText("Please enter a valid email address!");
            return;
        }

        try
        {
            System.out.println("DEBUG: Attempting to create user:");
            System.out.println("  First Name: " + firstName);
            System.out.println("  Last Name: " + lastName);
            System.out.println("  Username: " + username);
            System.out.println("  Email: " + email);
            System.out.println("  Password: " + password.substring(0, Math.min(3, password.length())) + "...");

            boolean usernameExists = userRepository.usernameExists(username);
            System.out.println("DEBUG: Username exists check: " + usernameExists);

            if (usernameExists)
            {
                messageLabel.setText("Username already exists! Choose another.");
                return;
            }

            Drafter newUser = new Drafter(
                    0,
                    firstName,
                    lastName,
                    username,
                    email,
                    password
            );

            System.out.println("DEBUG: User object created, calling addUser()...");

            boolean success = userRepository.addUser(newUser);

            System.out.println("DEBUG: addUser() returned: " + success);

            if (success)
            {
                System.out.println("DEBUG: Account created successfully!");
                JOptionPane.showMessageDialog(frame,
                        "Account created successfully!\nYou can now login with your credentials.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                firstNameField.setText("");
                lastNameField.setText("");
                userNameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                messageLabel.setText("");

                // Go back to login page
                frame.dispose();

                // Open login page
                SwingUtilities.invokeLater(() -> {
                    new LoginPage();
                });

            }
            else
            {
                String errorMsg = "Error creating account. Possible reasons:\n" +
                        "1. Database connection issue\n" +
                        "2. Username/email already exists\n" +
                        "3. Database table doesn't exist";
                System.out.println("DEBUG: " + errorMsg);
                messageLabel.setText("Error creating account. Please try again.");
            }

        }
        catch (Exception ex)
        {
            System.err.println("EXCEPTION in createNewAccount: " + ex.getMessage());
            ex.printStackTrace();
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == finishButton)
        {
            createNewAccount();
        }

        if (e.getSource() == backButton)
        {
            frame.dispose();

            // Open login page when going back
            SwingUtilities.invokeLater(() -> {
                LoginPage loginPage = new LoginPage();
            });
        }
    }
}