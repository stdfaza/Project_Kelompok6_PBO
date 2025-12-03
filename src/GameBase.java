import javax.swing.*;
import java.awt.*;

public abstract class GameBase extends JPanel {
    // Variable yang bisa diakses oleh anak kelas (protected)
    protected Main main;
    protected StringBuilder logHistory = new StringBuilder();

    public GameBase(Main main) {
        this.main = main;
        setLayout(new BorderLayout());
    }

    // --- ABSTRACT METHODS (Kontrak) ---
    // Game.java WAJIB memiliki fungsi ini
    public abstract void startNewGame();
    public abstract void endDay();

    // --- HELPER METHODS (Dipindah dari Game.java) ---
    
    // 1. Fungsi Mencatat Log
    protected void appendLog(String text) {
        logHistory.append(text).append("\n");
    }

    // 2. Fungsi Membuat Label Pop-up (Standard)
    protected JLabel createPopupLabel(String text, Font font) {
        return createPopupLabel(text, false, font);
    }
    
    // 3. Fungsi Membuat Label Pop-up (Dengan Logika Warna Kritis)
    protected JLabel createPopupLabel(String text, boolean isCritical, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        // Menggunakan warna merah jika kritis, putih jika normal
        lbl.setForeground(isCritical ? new Color(231, 76, 60) : Theme.WHITE_TEXT);
        return lbl;
    }

    // 4. Fungsi Membuat Tombol Gambar dengan Error Handling
    protected JButton createImageButton(String path, String tooltip) {
        JButton btn = new JButton();
        try {
            java.net.URL imgUrl = getClass().getResource(path);
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
            } else {
                btn.setText("O"); 
                btn.setBackground(Color.GRAY);
            }
        } catch (Exception e) {
            btn.setText("Err");
        }
        btn.setToolTipText(tooltip);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}