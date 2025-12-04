import javax.swing.*;
import java.awt.*;

public class EndingPage extends JPanel {
    private Main main;
    private Image endingImage;
    private JLabel titleLabel;
    private JTextArea descriptionArea;

    public EndingPage(Main main) {
        this.main = main;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // --- Panel Overlay (Teks & Tombol) ---
        // Kita gunakan GridBagLayout agar posisi teks bisa di tengah-bawah atau tengah
        JPanel overlayPanel = new JPanel(new GridBagLayout());
        overlayPanel.setOpaque(false); // Transparan agar gambar background terlihat
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        // 1. Title (Contoh: "MISSION SUCCESS" atau "GAME OVER")
        titleLabel = new JLabel("ENDING TITLE");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        // Beri efek shadow sedikit (opsional) atau outline, tapi warna putih sudah cukup kontras di background gelap
        
        // 2. Description (Penjelasan Ending)
        descriptionArea = new JTextArea("Deskripsi ending akan muncul di sini...");
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 18));
        descriptionArea.setForeground(Color.LIGHT_GRAY);
        descriptionArea.setOpaque(false); // Transparan
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionArea.setPreferredSize(new Dimension(600, 100)); // Batasi lebar teks
        descriptionArea.setBorder(null);

        // 3. Tombol Kembali ke Menu
        JButton btnMenu = new JButton("RETURN TO MENU");
        btnMenu.setFont(new Font("Arial", Font.BOLD, 16));
        btnMenu.setBackground(Theme.COLOR);
        btnMenu.setForeground(Color.WHITE);
        btnMenu.setFocusPainted(false);
        btnMenu.setPreferredSize(new Dimension(200, 50));
        
        btnMenu.addActionListener(e -> {
            main.showPage("HomePage"); // Kembali ke halaman awal
        });

        // Masukkan komponen ke Panel
        overlayPanel.add(Box.createVerticalStrut(200), gbc); // Spacer agar teks agak ke bawah
        overlayPanel.add(titleLabel, gbc);
        overlayPanel.add(descriptionArea, gbc);
        overlayPanel.add(Box.createVerticalStrut(20), gbc);
        overlayPanel.add(btnMenu, gbc);

        add(overlayPanel, BorderLayout.CENTER);
    }

    public void setEnding(String title, String description, String imagePath, boolean isWin) {
        titleLabel.setText(title);
        descriptionArea.setText(description);

        // Atur warna judul
        if (isWin) {
            titleLabel.setForeground(Color.GREEN);
        } else {
            titleLabel.setForeground(Color.RED);
        }

        // --- PERBAIKAN LOAD GAMBAR ---
        try {
            // 1. Pastikan path dimulai dengan "/" agar dibaca dari root folder src
            if (!imagePath.startsWith("/")) {
                imagePath = "/" + imagePath;
            }

            // 2. Gunakan getResource untuk mencari file di dalam classpath (src)
            java.net.URL imgUrl = getClass().getResource(imagePath);

            if (imgUrl != null) {
                endingImage = new ImageIcon(imgUrl).getImage();
                System.out.println("Sukses load gambar: " + imagePath);
            } else {
                System.err.println("ERROR: Gambar tidak ditemukan di path: " + imagePath);
                endingImage = null; // Ini yang bikin layar hitam
            }
        } catch (Exception e) {
            System.err.println("Exception saat load gambar: " + e.getMessage());
            endingImage = null;
        }

        repaint(); // Gambar ulang layar
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Gambar Background Full Screen
        if (endingImage != null) {
            // Gambar direntangkan memenuhi layar
            g.drawImage(endingImage, 0, 0, getWidth(), getHeight(), this);
            
            // Tambahkan overlay hitam transparan agar teks lebih terbaca
            g.setColor(new Color(0, 0, 0, 150)); // Hitam opacity 150
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}