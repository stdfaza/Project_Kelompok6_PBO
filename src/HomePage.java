import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HomePage extends JPanel {

    private Image backgroundImage;
    private Main main;

    public HomePage(Main main) {
        this.main = main;

        // Load background dari resource
        try {
            backgroundImage = new ImageIcon(getClass().getResource("/assets/page/landing-page.png")).getImage();
        } catch (Exception e) {
            System.out.println("GAMBAR TIDAK KETEMU! Menggunakan warna hitam.");
            backgroundImage = null;
        }

        setLayout(new BorderLayout());

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        Font roboto = new Font("Roboto", Font.BOLD, 28);

        // ========= BUTTON PLAY =========
        RoundedButton btnPlay = new RoundedButton("PLAY", 35);
        btnPlay.setFont(roboto);
        btnPlay.setBackground(new Color(46, 204, 113));
        btnPlay.setForeground(Color.WHITE);
        btnPlay.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ⬅ Aksi tombol PLAY ke GamePage
        btnPlay.addActionListener(e -> main.showPage("GamePage"));

        btnPlay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnPlay.setBackground(new Color(39, 174, 96));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnPlay.setBackground(new Color(46, 204, 113));
            }
        });

        // ========= BUTTON EXIT =========
        RoundedButton btnExit = new RoundedButton("EXIT", 35);
        btnExit.setFont(roboto);
        btnExit.setBackground(new Color(231, 76, 60));
        btnExit.setForeground(Color.WHITE);
        btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnExit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnExit.setBackground(new Color(192, 57, 43));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnExit.setBackground(new Color(231, 76, 60));
            }
        });

        centerPanel.add(btnPlay);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(btnExit);

        wrapper.add(centerPanel);
        add(wrapper, BorderLayout.CENTER);
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