import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Game extends JPanel {
    private Main main;

    // --- GAME VARIABLES ---
    private int day;
    private int oxygen;
    private int food;
    private int power;
    private int starvationDays;
    private boolean isGameOver;
    private boolean running = true;
    
    // Log Storage
    private StringBuilder logHistory = new StringBuilder();
    
    // Scene System
    private List<Scene> scenes = new ArrayList<>();
    private Thread sceneThread;

    // UI Assets
    private Image bgImage;
    private JButton btnNotification;

    public Game(Main main) {
        this.main = main;
        setLayout(new BorderLayout()); 

        loadAssets();
        initScene();
        setupUI();
    }

    private void loadAssets() {
        try {
            // Pastikan gambar ini ada. Jika tidak, background jadi hitam.
            java.net.URL imgUrl = getClass().getResource("/assets/scene/image.jpg");
            if (imgUrl != null) {
                bgImage = new ImageIcon(imgUrl).getImage();
            }
        } catch (Exception e) {
            System.out.println("Gagal load background.");
        }
    }

    private void setupUI() {
        // 1. PANEL KIRI (Tombol Aksi)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false); 
        leftPanel.setBorder(new EmptyBorder(20, 20, 0, 0)); 

        JButton btnOxygen = createImageButton("/assets/icon/image2.png", "Recycle Oxygen");
        JButton btnFood = createImageButton("/assets/icon/image.jpg", "Synthesize Food");

        btnOxygen.addActionListener(e -> performAction("OXYGEN"));
        btnFood.addActionListener(e -> performAction("FOOD"));

        leftPanel.add(btnOxygen);
        leftPanel.add(Box.createVerticalStrut(15)); 
        leftPanel.add(btnFood);

        // 2. PANEL KANAN ATAS (Tombol Notifikasi)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(20, 0, 0, 20));

        btnNotification = createImageButton("/assets/icon/image3.png", "Check Status & Log");
        btnNotification.addActionListener(e -> showStatusPopup());
        
        rightPanel.add(btnNotification);

        // 3. PANEL KANAN BAWAH (Tombol Next Day)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(0, 0, 20, 20));

        JButton btnNextDay = createImageButton("/assets/btn_next.png", "Solar Charge (Next Day)");
        btnNextDay.addActionListener(e -> endDay());
        bottomPanel.add(btnNextDay);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    // --- POP-UP STATUS SYSTEM (FIXED LAYOUT) ---
    private void showStatusPopup() {
        JDialog popup = new JDialog(main, "System Status", true);
        popup.setSize(400, 500);
        
        // Atur posisi pop-up di tengah
        popup.setLocationRelativeTo(main);
        popup.setUndecorated(true);
        
        // PERBAIKAN 1: Gunakan gap (jarak) horizontal 0 dan vertikal 15 di BorderLayout
        // Ini menggantikan fungsi "Box.createVerticalStrut" yang bikin error kemarin.
        JPanel panel = new JPanel(new BorderLayout(0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background Biru Kartun
                g2.setColor(new Color(60, 90, 140)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                
                // Border Luar Biru Muda
                g2.setColor(new Color(100, 200, 255)); 
                g2.setStroke(new BasicStroke(4));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 30, 30);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        // Padding agar konten tidak menempel ke pinggir border
        panel.setBorder(new EmptyBorder(20, 20, 20, 20)); 
        
        // Font Setup
        Font cartoonFont = new Font("Comic Sans MS", Font.BOLD, 16);
        if (!cartoonFont.getFamily().equals("Comic Sans MS")) {
            cartoonFont = new Font("Arial", Font.BOLD, 16); 
        }

        // --- 1. BAGIAN ATAS (STATS) ---
        JPanel statsPanel = new JPanel(new GridLayout(4, 1));
        statsPanel.setBackground(new Color(80, 110, 160)); 
        statsPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(100, 200, 255), 2, true), 
                new EmptyBorder(10, 15, 10, 15))); 
        
        statsPanel.add(createPopupLabel("📅 DAY: " + day + "/10", cartoonFont));
        statsPanel.add(createPopupLabel("💨 OXYGEN: " + oxygen + "%", oxygen < 20, cartoonFont));
        statsPanel.add(createPopupLabel("🍖 FOOD: " + food + "%", food < 20, cartoonFont));
        statsPanel.add(createPopupLabel("⚡ POWER: " + power + "%", power < 20, cartoonFont));
        
        // --- 2. BAGIAN TENGAH (LOG AREA) ---
        JTextArea popupLog = new JTextArea();
        // Ambil text dari history, jika kosong beri pesan default
        String textToShow = logHistory.length() > 0 ? logHistory.toString() : "System ready...\nNo logs yet.";
        popupLog.setText(textToShow); 
        popupLog.setEditable(false);
        
        // Styling agar terlihat jelas (Hitam transparan dikit)
        popupLog.setOpaque(true);
        popupLog.setBackground(new Color(30, 30, 45)); 
        popupLog.setForeground(new Color(100, 255, 100)); // Hijau Matrix
        
        popupLog.setFont(new Font("Monospaced", Font.BOLD, 13)); 
        popupLog.setLineWrap(true);
        popupLog.setWrapStyleWord(true);
        popupLog.setBorder(new EmptyBorder(10, 10, 10, 10)); 
        
        JScrollPane scroll = new JScrollPane(popupLog);
        scroll.setBorder(new LineBorder(new Color(100, 200, 255), 2)); // Border pembungkus log
        
        // Scroll ke bawah otomatis
        popupLog.setCaretPosition(popupLog.getDocument().getLength());

        // --- 3. BAGIAN BAWAH (TOMBOL CLOSE) ---
        JButton btnClose = new JButton("CLOSE") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 100, 100)); 
                } else {
                    g2.setColor(new Color(220, 60, 60)); 
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25); 
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setPreferredSize(new Dimension(120, 45));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> popup.dispose());

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.add(btnClose);

        // PERBAIKAN 2: Add component langsung ke posisi yang benar
        // Spacer sudah diurus oleh `new BorderLayout(0, 15)` di atas
        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(footerPanel, BorderLayout.SOUTH);

        popup.add(panel);
        popup.setVisible(true);
    }

    private JLabel createPopupLabel(String text, Font font) {
        return createPopupLabel(text, false, font);
    }
    
    private JLabel createPopupLabel(String text, boolean isCritical, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(isCritical ? new Color(255, 80, 80) : Color.WHITE);
        return lbl;
    }

    private JButton createImageButton(String path, String tooltip) {
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // ==========================================
    // LOGIKA GAME
    // ==========================================

    private void initScene() {
        scenes.add(new Scene(1, "/assets/bg_space.png", "/assets/scene/astronout.png", 
            new String[]{"Selamat datang, Astronot.", "Aku AI pendampingmu.", "Bertahanlah 10 hari."}));
    }

    public void startGame() {
        day = 1; oxygen = 100; food = 100; power = 100; starvationDays = 0;
        isGameOver = false; running = true;
        
        logHistory.setLength(0); 
        appendLog("=== SYSTEM BOOT ===");
        appendLog("Status: Online.");
        
        startSceneEvent();
    }

    private void performAction(String action) {
        if (isGameOver) return;
        if (power < 20) {
            appendLog("⚠ POWER LOW! Action Failed.");
            return;
        }

        power -= 20;

        if (action.equals("OXYGEN")) {
            oxygen = Math.min(100, oxygen + 30);
            appendLog(">>> Player: Menambah Oksigen (+30%).");
        } else if (action.equals("FOOD")) {
            food = Math.min(100, food + 30);
            appendLog(">>> Player: Menambah Makanan (+30%).");
        }
    }

    private void endDay() {
        if (isGameOver) return;

        appendLog("\n[SYSTEM] : Day " + day + " Ended.");
        power = Math.min(100, power + 50);
        int drain = 15;
        oxygen -= drain;
        food -= drain;

        triggerRandomEvent();
        appendLog("[SYSTEM] : Resource decreased (-" + drain + "%)");

        if (oxygen <= 0) { 
            oxygen = 0; 
            triggerGameOver("Kehabisan Oksigen."); 
            return; 
        }
        
        if (food <= 0) {
            food = 0;
            starvationDays++;
            if (starvationDays >= 2) { triggerGameOver("Mati Kelaparan."); return; }
            appendLog("⚠ WARNING: Makanan Habis!");
        } else {
            starvationDays = 0;
        }

        day++;
        checkSceneTrigger();
        if (day > 10) winGame();
    }
    
    private void triggerRandomEvent() {
         if (Math.random() > 0.4) return; 

        int eventType = (int) (Math.random() * 3); 
        switch (eventType) {
            case 0: 
                oxygen -= 15;
                appendLog("⚠ ALARM: Meteoroid Impact! Oxygen -15%.");
                break;
            case 1: 
                food -= 20;
                appendLog("⚠ ALARM: Food Storage Fail! Food -20%.");
                break;
            case 2: 
                power -= 25;
                appendLog("⚠ ALARM: Solar Flare! Power -25%.");
                break;
        }
        oxygen = Math.max(0, oxygen);
        food = Math.max(0, food);
        power = Math.max(0, power);
    }

    private void appendLog(String text) {
        logHistory.append(text).append("\n");
    }
    
    private void startSceneEvent() {
        sceneThread = new Thread(() -> {
            while (running) {
                try { Thread.sleep(500); } catch (InterruptedException e) { break; }
            }
        });
        sceneThread.start();
    }

    private void checkSceneTrigger() {
        for (Scene s : scenes) {
            if (s.triggerDay == day && !s.hasPlayed) {
                s.hasPlayed = true;
                SwingUtilities.invokeLater(() -> main.showScene(s));
            }
        }
    }

    private void winGame() {
        isGameOver = true;
        running = false;
        main.showEnding("MISSION SUCCESS", "Kamu berhasil bertahan hidup!", "assets/ending/win.jpg", true);
    }

    private void triggerGameOver(String reason) {
        isGameOver = true;
        running = false;
        main.showEnding("GAME OVER", reason, "assets/ending/lose.jpg", false);
    }
}