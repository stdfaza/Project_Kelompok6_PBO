import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HomePage extends JPanel {

    private Image backgroundImage;
    private Main main;

    public HomePage(Main main) {
        this.main = main;

        try {
            backgroundImage = new ImageIcon(getClass().getResource("/assets/page/landing-page.png")).getImage();
        } catch (Exception e) {
            backgroundImage = null;
        }

        setLayout(new BorderLayout());

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        Font roboto = new Font("Roboto", Font.BOLD, 24);

        // ========= 1. BUTTON NEW GAME =========
        RoundedButton btnNewGame = new RoundedButton("NEW GAME", 35);
        setupButtonStyle(btnNewGame, new Color(46, 204, 113), roboto);
        btnNewGame.addActionListener(e -> {
            // Konfirmasi jika mau New Game (takut kepencet)
            int choice = JOptionPane.showConfirmDialog(this, 
                "Start New Game? Data lama akan tertimpa.", 
                "Warning", JOptionPane.YES_NO_OPTION);
                
            if (choice == JOptionPane.YES_OPTION) {
                main.startNewGame(); // Panggil method di Main
            }
        });

        // ========= 2. BUTTON CONTINUE =========
        RoundedButton btnContinue = new RoundedButton("CONTINUE", 35);
        setupButtonStyle(btnContinue, new Color(52, 152, 219), roboto);
        btnContinue.addActionListener(e -> {
            main.continueGame(); // Panggil method di Main
        });

        // ========= 3. BUTTON EXIT =========
        RoundedButton btnExit = new RoundedButton("EXIT", 35);
        setupButtonStyle(btnExit, new Color(231, 76, 60), roboto);
        btnExit.addActionListener(e -> System.exit(0));

        centerPanel.add(btnNewGame);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(btnContinue); // Tombol Continue Ditambahkan
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(btnExit);

        wrapper.add(centerPanel);
        add(wrapper, BorderLayout.CENTER);
    }
    
    // Helper untuk mempersingkat styling tombol
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

    // Helper Class untuk Tombol Bulat
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
}