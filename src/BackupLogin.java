import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;;

public class BackupLogin extends JPanel{
    private Main main;
    private Image backgroundImage;
    private JTextField userField;
    private JPasswordField passField;
    private int loginRegister = -1;
    private JButton loginRegisterButton;
    private Dimension size = new Dimension(500, Integer.MAX_VALUE);

    public BackupLogin(Main main) {
        this.main = main;

        try {
            backgroundImage = new ImageIcon(getClass().getResource("/assets/page/login-page.png")).getImage();
        } catch (Exception e) {
            System.out.println("no background found in LoginPage");
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Theme.BACK_COLOR);
        
        // Usename
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(Theme.FONT);
        userLabel.setForeground(Theme.COLOR);
        
        userField = new JTextField(20);
        userField.setFont(Theme.FONT);
        userField.setForeground(Theme.COLOR);
        userField.setBorder(new LineBorder(Theme.COLOR));

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(Theme.FONT);
        passLabel.setForeground(Theme.COLOR);

        passField = new JPasswordField(20);
        passField.setFont(Theme.FONT);
        passField.setForeground(Theme.COLOR);
        passField.setBorder(new LineBorder(Theme.COLOR));

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setMaximumSize(size);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // margin antar komponen
        gbc.anchor = GridBagConstraints.WEST; // rata kiri

        gbc.gridx = 0; 
        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(passField, gbc);

        // == BUTTON ==
        JButton registerButton = new JButton("Register");
        registerButton.setFont(Theme.FONT);
        registerButton.setBackground(Theme.COLOR);
        registerButton.setForeground(Theme.WHITE_TEXT);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener((e) -> { loginRegister = 0; });

        JButton loginButton = new JButton("Login");
        loginButton.setFont(Theme.FONT);
        loginButton.setBackground(Theme.COLOR);
        loginButton.setForeground(Theme.WHITE_TEXT);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener((e) -> { loginRegister = 1; });

        registerButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                registerButton.setBackground(Theme.COLOR.darker());
                loginButton.setBackground(Theme.COLOR);
            }
        });

        loginButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                registerButton.setBackground(Theme.COLOR);
                loginButton.setBackground(Theme.COLOR.darker());
            }
        });

        // Register Or Login Component
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        buttonPanel.setOpaque(false);

        buttonPanel.setMaximumSize(size);
        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);

        // Button Process
        loginRegisterButton = new JButton("->");
        loginRegisterButton.setOpaque(false);
        loginRegisterButton.setFont(Theme.FONT);
        loginRegisterButton.setBackground(Theme.COLOR);
        loginRegisterButton.setForeground(Theme.WHITE_TEXT);
        loginRegisterButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginRegisterButton.setFocusPainted(false);
        loginRegisterButton.addActionListener((e) -> { processRegisterOrLogin(); });

        // Atur Button Process
        JPanel logRegPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logRegPanel.setOpaque(false);
        logRegPanel.setMaximumSize(size);
        
        logRegPanel.add(loginRegisterButton);

        add(Box.createVerticalStrut(200));
        add(buttonPanel);
        add(formPanel);
        add(logRegPanel);
        add(Box.createVerticalStrut(500));
    }

    private void processRegisterOrLogin() {
        if (loginRegister == 0) {
            processRegister();
        } else if (loginRegister == 1) {
            processLogin();
        }
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

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}