import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HomePage extends JPanel implements Page {

    private Image backgroundImage;
    private Clip bgmClip;
    private Main main;

    public HomePage(Main main) {
        this.main = main;

        loadBGM("/assets/audio/slow-travel.wav"); 

        try {
            backgroundImage = new ImageIcon(getClass().getResource("/assets/scene/page.png")).getImage();
        } catch (Exception e) {
            backgroundImage = null;
        }

        setLayout(new BorderLayout());

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        RoundedPanel centerPanel = new RoundedPanel(
            40,                           
            new Color(255, 255, 255, 128) 
        );
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40)); 

        // ========= 1. BUTTON NEW GAME =========
        RoundedButton btnNewGame = new RoundedButton("NEW GAME", 35);
        setupButtonStyle(btnNewGame, new Color(143, 201, 127), Theme.FONT.deriveFont(Font.BOLD));
        btnNewGame.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, 
                "Start New Game? Data lama akan tertimpa.", 
                "Warning", JOptionPane.YES_NO_OPTION);
                
            if (choice == JOptionPane.YES_OPTION) {
                main.startNewGame(); 
            }
        });

        // ========= 2. BUTTON CONTINUE =========
        RoundedButton btnContinue = new RoundedButton("CONTINUE", 35);
        setupButtonStyle(btnContinue, Theme.COLOR, Theme.FONT.deriveFont(Font.BOLD));
        btnContinue.addActionListener(e -> {
            main.continueGame(); 
        });

        // ========= 3. BUTTON EXIT =========
        RoundedButton btnExit = new RoundedButton("EXIT", 35);
        setupButtonStyle(btnExit, new Color(219, 109, 94), Theme.FONT.deriveFont(Font.BOLD));
        btnExit.addActionListener(e -> System.exit(0));

        centerPanel.add(btnNewGame);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(btnContinue);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(btnExit);

        wrapper.add(centerPanel);
        add(wrapper, BorderLayout.CENTER);
    }
    
    private void setupButtonStyle(RoundedButton btn, Color color, Font font) {
        btn.setFont(font);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(color.darker()); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(color); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // Helper Class UI (RoundedButton & RoundedPanel) tetap sama
    static class RoundedButton extends JButton {
        private final int arc;
        public RoundedButton(String text, int arc) {
            super(text);
            this.arc = arc;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            Dimension size = new Dimension(250, 60);
            setPreferredSize(size);
            setMaximumSize(size);
            setMinimumSize(size);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(getText())) / 2;
            int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.setColor(getForeground());
            g2.drawString(getText(), textX, textY);
            g2.dispose();
        }
    }

    static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color bg;
        public RoundedPanel(int arc, Color bg) {
            this.arc = arc;
            this.bg = bg;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // --- IMPLEMENTASI MUSIK ---
    @Override
    public void loadBGM(String path) {
        try {
            if (bgmClip != null) {
                bgmClip.stop();
                bgmClip.close();
            }
            // Load audio
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource(path));
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioStream);
        } catch (Exception e) {
            System.out.println("Gagal load BGM HomePage: " + path);
            e.printStackTrace();
        }
    }

    @Override
    public void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
        }
    }

    // Saat halaman ditampilkan/disembunyikan oleh CardLayout
    @Override
    public void setVisible(boolean flag) {
        super.setVisible(flag);
        if (flag) {
            if (bgmClip != null) {
                bgmClip.setFramePosition(0); // Reset ke awal
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
                bgmClip.start();
            }
        } else {
            stopBGM();
        }
    }
}