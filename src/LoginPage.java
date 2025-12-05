import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JPanel implements Page {
    private Main main;
    private Image backgroundImage;
    private JTextField userField;
    private JPasswordField passField;
    private int loginRegister = -1;
    private JButton loginRegisterButton;
    private Clip bgmClip;

    public LoginPage(Main main) {
        this.main = main;
        loadBGM("/assets/audio/menu.wav");

        try {
            backgroundImage = new ImageIcon(getClass().getResource("/assets/scene/page.png")).getImage();
        } catch (Exception e) {
            System.out.println("no background found in LoginPage");
        }

        // Usename
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(Theme.FONT.deriveFont(Font.BOLD));
        userLabel.setForeground(Theme.COLOR);
        
        userField = new JTextField(20);
        userField.setFont(Theme.FONT);
        userField.setForeground(Theme.COLOR);
        userField.setBorder(new LineBorder(Theme.COLOR));

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(Theme.FONT.deriveFont(Font.BOLD));
        passLabel.setForeground(Theme.COLOR);

        passField = new JPasswordField(20);
        passField.setFont(Theme.FONT);
        passField.setForeground(Theme.COLOR);
        passField.setBorder(new LineBorder(Theme.COLOR));

        // Button Register dan Login
        JButton registerButton = new JButton("Register");
        registerButton.setFont(Theme.FONT);
        registerButton.setBackground(Theme.COLOR);
        registerButton.setForeground(Theme.WHITE_TEXT);
        registerButton.setBorderPainted(false);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener((e) -> { loginRegister = 0; });

        JButton loginButton = new JButton("Login");
        loginButton.setFont(Theme.FONT);
        loginButton.setBackground(Theme.COLOR);
        loginButton.setForeground(Theme.WHITE_TEXT);
        loginButton.setBorderPainted(false);
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

        // Button Process
        loginRegisterButton = new JButton("->");
        loginRegisterButton.setOpaque(false);
        loginRegisterButton.setFont(Theme.FONT);
        loginRegisterButton.setBackground(Theme.COLOR);
        loginRegisterButton.setForeground(Theme.WHITE_TEXT);
        loginRegisterButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginRegisterButton.setFocusPainted(false);
        loginRegisterButton.addActionListener((e) -> { processRegisterOrLogin(); });

        setLayout(new GridBagLayout()); // supaya container bisa ditaruh di tengah
        setBackground(Theme.BACK_COLOR);

        // PANEL PEMBATAS (form tidak melebar ke seluruh layar)
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        container.setPreferredSize(new Dimension(420, 260));
        container.setMaximumSize(new Dimension(420, 260));

        // Panel atas
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        topPanel.setOpaque(false);
        topPanel.add(registerButton);
        topPanel.add(loginButton);

        // GridBagConstraints untuk container
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        // Baris 0: login and register button
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(topPanel, gbc);

        // reset gridwidth
        gbc.gridwidth = 1;

        // Baris 1 Kolom 0: label username
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 1;
        container.add(userLabel, gbc);

        // Baris 1 Kolom 1: field username
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        container.add(userField, gbc);

        // Baris 2 Kolom 0: label password
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 2;
        container.add(passLabel, gbc);

        // Baris 2 Kolom 1: field password
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        container.add(passField, gbc);

        // Baris 3 Kolom 1: tombol panah (->)
        gbc.gridy = 3;
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        container.add(loginRegisterButton, gbc);

        // Masukkan container KE PANEL TENGAH
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.anchor = GridBagConstraints.CENTER;

        add(container, mainGbc);
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

    public void loadBGM(String path) {
    try {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource(path));

        bgmClip = AudioSystem.getClip();
        bgmClip.open(audioStream);
        bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
        bgmClip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopBGM() {
        bgmClip.stop();
    }

    @Override
    public void setVisible(boolean flag) {
        super.setVisible(flag);
        if (!flag) {
            stopBGM();
        }
    }
}