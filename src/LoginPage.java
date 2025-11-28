import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;;

public class LoginPage extends JPanel{
    private Main main;
    private JTextField userField;
    private JPasswordField passField;

    public LoginPage(Main main) {
        this.main = main;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Theme.BACK_COLOR);

        // == TITLE ==
        JLabel titleLabel = new JLabel("Astronout : Survive in The Outer Planet");
        titleLabel.setFont(Theme.FONT.deriveFont(Font.BOLD, 40));
        titleLabel.setForeground(Theme.COLOR);
        titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        // == FORM ==
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(Theme.FONT);
        userLabel.setForeground(Theme.COLOR);
        
        userField = new JTextField(20);
        userField.setFont(Theme.FONT);
        userField.setForeground(Theme.COLOR);
        userField.setBorder(new LineBorder(Theme.COLOR));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(Theme.FONT);
        passLabel.setForeground(Theme.COLOR);

        passField = new JPasswordField(20);
        passField.setFont(Theme.FONT);
        passField.setForeground(Theme.COLOR);
        passField.setBorder(new LineBorder(Theme.COLOR));

        // Atur Component
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Theme.BACK_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passField, gbc);

        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // == BUTTON ==
        JButton loginButton = new JButton("Login");
        loginButton.setFont(Theme.FONT);
        loginButton.setBackground(Theme.COLOR);
        loginButton.setForeground(Theme.WHITE_TEXT);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(formPanel.getPreferredSize().width, 45));
        loginButton.addActionListener((e) -> { processLogin(); });

        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(Theme.COLOR.darker());
            }

            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(Theme.COLOR);
            }
        });

        JButton registerButton = new JButton("Register");
        registerButton.setFont(Theme.FONT);
        registerButton.setBackground(Theme.COLOR);
        registerButton.setForeground(Theme.WHITE_TEXT);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(formPanel.getPreferredSize().width, 45));
        registerButton.addActionListener((e) -> { processRegister(); });

        registerButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(Theme.COLOR.darker());
            }

            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(Theme.COLOR);
            }
        });

        // Atur Component
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Theme.BACK_COLOR);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(registerButton);

        // == LOGINPAGE ==
        add(Box.createVerticalStrut(150));
        add(titleLabel);
        add(Box.createVerticalStrut(40));
        add(formPanel);
        add(Box.createVerticalStrut(40));
        add(buttonPanel);
        add(Box.createVerticalStrut(500));
        add(Box.createVerticalGlue());
    }

    private void processLogin() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if(username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username dan password tidak boleh kosong.", 
                "Login Gagal",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        DatabaseConnection db = new DatabaseConnection();
        int resultId = db.loginUser(username, password);

        if (resultId <= 0) {
            JOptionPane.showMessageDialog(this,
            "Username atau password salah.", 
            "Login Gagal",
            JOptionPane.INFORMATION_MESSAGE);

            main.showPage("Login");
        } else {
            JOptionPane.showMessageDialog(this,
            "Login berhasil!", 
            "Message",
            JOptionPane.INFORMATION_MESSAGE);

            main.onLoginSuccess(resultId, username);
            main.showPage("MainPage");
        }
    }

    private void processRegister() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if(username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username dan password tidak boleh kosong.", 
                "Registrasi Gagal",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        DatabaseConnection db = new DatabaseConnection();
        boolean resultId = db.registerUser(username, password);

        if (resultId == false) {
            JOptionPane.showMessageDialog(this,
            "Registrasi gagal. Username mungkin sudah digunakan.", 
            "Registrasi Gagal",
            JOptionPane.INFORMATION_MESSAGE);

            main.showPage("LoginPage");
            resetField();
        } else {
            JOptionPane.showMessageDialog(this,
            "Registrasi berhasil! Silahkan login", 
            "Message",
            JOptionPane.INFORMATION_MESSAGE);

            main.showPage("LoginPage");
            resetField();
        }
    }

    public void resetField() {
        userField.setText("");
        passField.setText("");
    }
}