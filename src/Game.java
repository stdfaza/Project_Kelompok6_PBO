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
            java.net.URL imgUrl = getClass().getResource("/assets/scene/background2.png");
            if (imgUrl != null) {
                bgImage = new ImageIcon(imgUrl).getImage();
            }
        } catch (Exception e) {
            System.out.println("Gagal load background.");
        }
    }

    private void setupUI() {
        // ... (Kode Setup UI SAMA SEPERTI SEBELUMNYA, TIDAK BERUBAH) ...
        // Untuk mempersingkat jawaban, bagian ini saya skip karena tidak ada logika yang berubah di UI
        // Salin saja bagian setupUI dari kode sebelumnya.
        
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
    
    // ... (Kode showStatusPopup SAMA SEPERTI SEBELUMNYA) ...
    // Salin method showStatusPopup dari kode sebelumnya di sini
    private void showStatusPopup() {
        JDialog popup = new JDialog(main, "System Status", true);
        popup.setSize(400, 550);
        popup.setLocationRelativeTo(main);
        popup.setUndecorated(true);
        popup.setBackground(new Color(0, 0, 0, 0));
        ((JComponent) popup.getContentPane()).setOpaque(false);

        Color metalDark = new Color(60, 65, 75);   
        Color metalLight = new Color(120, 130, 150); 
        Color metalButton = new Color(200, 60, 60); 
        Color metalButtonHover = new Color(230, 80, 80); 
        Color cyanNeon = new Color(100, 255, 255); 
        Color borderDark = new Color(40, 40, 40); 
        
        JPanel panel = new JPanel(new BorderLayout(0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(metalDark); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); 
                g2.setColor(metalLight); 
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        Font titleFont = Theme.FONT.deriveFont(Font.BOLD, 18f);
        Font logFont = Theme.FONT.deriveFont(Font.PLAIN, 14f);

        JPanel statsPanel = new JPanel(new GridLayout(4, 1));
        statsPanel.setBackground(metalDark.darker()); 
        statsPanel.setBorder(new CompoundBorder(
                new LineBorder(metalLight, 2), 
                new EmptyBorder(10, 15, 10, 15))); 
        
        statsPanel.add(createPopupLabel("DAY: " + day + "/10", titleFont));
        statsPanel.add(createPopupLabel("OXYGEN: " + oxygen + "%", oxygen < 20, titleFont));
        statsPanel.add(createPopupLabel("FOOD: " + food + "%", food < 20, titleFont));
        statsPanel.add(createPopupLabel("POWER: " + power + "%", power < 20, titleFont));
        
        JTextArea popupLog = new JTextArea();
        String textToShow = logHistory.length() > 0 ? logHistory.toString() : "System ready...\nNo logs yet.";
        popupLog.setText(textToShow); 
        popupLog.setEditable(false);
        popupLog.setOpaque(false);
        popupLog.setForeground(cyanNeon); 
        popupLog.setFont(logFont); 
        popupLog.setLineWrap(true);
        popupLog.setWrapStyleWord(true);
        popupLog.setBorder(new EmptyBorder(10, 5, 10, 5)); 
        
        JScrollPane scroll = new JScrollPane(popupLog);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(new LineBorder(metalLight, 2)); 
        popupLog.setCaretPosition(popupLog.getDocument().getLength());

        JButton btnClose = new JButton("CLOSE") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) { g2.setColor(metalButtonHover); } else { g2.setColor(metalButton); }
                g2.fillRect(0, 0, getWidth(), getHeight()); 
                g2.setColor(Theme.WHITE_TEXT);
                g2.setFont(Theme.FONT.deriveFont(Font.BOLD, 16f));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btnClose.setForeground(Theme.WHITE_TEXT);
        btnClose.setFocusPainted(false);
        btnClose.setBorder(new CompoundBorder(new LineBorder(borderDark, 3), new EmptyBorder(5, 15, 5, 15)));
        btnClose.setContentAreaFilled(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> popup.dispose());

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.add(btnClose);

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(footerPanel, BorderLayout.SOUTH);

        popup.add(panel);
        popup.setVisible(true);
    }
    
    // ... Helper Label, Button, PaintComponent SAMA SEPERTI SEBELUMNYA ...
    private JLabel createPopupLabel(String text, Font font) { return createPopupLabel(text, false, font); }
    private JLabel createPopupLabel(String text, boolean isCritical, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(isCritical ? new Color(231, 76, 60) : Theme.WHITE_TEXT);
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
            } else { btn.setText("O"); btn.setBackground(Color.GRAY); }
        } catch (Exception e) { btn.setText("Err"); }
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
        if (bgImage != null) { g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else { g.setColor(Theme.BACK_COLOR); g.fillRect(0, 0, getWidth(), getHeight()); }
    }

    // ==========================================
    // LOGIKA GAME & SAVE/LOAD
    // ==========================================

    public void startNewGame() {
        day = 1; 
        oxygen = 100; 
        food = 100; 
        power = 100; 
        starvationDays = 0;
        isGameOver = false; 
        running = true;
        
        logHistory.setLength(0);
        appendLog("=== NEW GAME STARTED ===");
        appendLog("System Online. Good luck, Astronaut.");
        
        // Simpan progress awal (Status: Ongoing / false)
        saveProgress(false); 
        
        startSceneEvent();
    }

    public void continueGame() {
        DatabaseConnection db = new DatabaseConnection();
        int userId = main.getCurrentAstronoutId();
        int[] data = db.loadGame(userId);

        if (data != null) {
            this.day = data[0];
            this.oxygen = data[1];
            this.food = data[2];
            this.power = data[3];
            
            this.starvationDays = 0;
            this.isGameOver = false;
            this.running = true;
            
            logHistory.setLength(0);
            appendLog("=== GAME LOADED ===");
            appendLog("Welcome back. Day " + day + " loaded.");
            
            startSceneEvent();
        } else {
            startNewGame();
        }
    }

    private void saveProgress(boolean isFinished) {
        DatabaseConnection db = new DatabaseConnection();
        // Simpan ke database dengan status selesai/tidak
        db.saveGame(main.getCurrentAstronoutId(), day, oxygen, food, power, isFinished);
        if (!isFinished) appendLog("[SYSTEM] Progress Auto-Saved.");
    }

    private void initScene() {
        scenes.add(new Scene(1, "/assets/bg_space.png", "/assets/scene/astronout.png", 
            new String[]{"Selamat datang, Astronot.", "Aku AI pendampingmu.", "Bertahanlah 10 hari."}));
    }

    public void startGame() {
        // Method ini hanya inisialisasi awal, 
        // Logic start ada di startNewGame / continueGame
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
        
        // AUTO SAVE: Masih ongoing (isFinished = false)
        saveProgress(false); 
        
        checkSceneTrigger();
        if (day > 10) winGame();
    }
    
    private void triggerRandomEvent() {
         if (Math.random() > 0.4) return; 

        int eventType = (int) (Math.random() * 3); 
        switch (eventType) {
            case 0: 
                // EVENT 1: METEOR MINI GAME
                JOptionPane.showMessageDialog(this, 
                    "⚠ WARNING: METEOR STORM DETECTED! ⚠\nKlik meteor yang muncul secepatnya!", 
                    "INCOMING THREAT", JOptionPane.WARNING_MESSAGE);
                startMeteorGame();
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
    
    // --- MINI GAME (SAMA SEPERTI SEBELUMNYA) ---
    private void startMeteorGame() {
        JDialog gameDialog = new JDialog(main, "⚠ METEOR STORM DETECTED! ⚠", true);
        gameDialog.setSize(800, 500); 
        gameDialog.setLocationRelativeTo(main);
        gameDialog.setUndecorated(true);
        gameDialog.setLayout(null);
        gameDialog.getRootPane().setBorder(new LineBorder(new Color(100, 255, 100), 4)); 
        gameDialog.getContentPane().setBackground(new Color(20, 20, 35)); 

        final int[] score = {0};
        final int[] timeHeader = {10}; 

        JLabel lblScore = new JLabel("HITS: 0");
        lblScore.setForeground(Color.GREEN);
        lblScore.setFont(new Font("Monospaced", Font.BOLD, 24));
        lblScore.setBounds(30, 20, 200, 30);
        gameDialog.add(lblScore);

        JLabel lblTime = new JLabel("TIME: 10");
        lblTime.setForeground(Color.RED);
        lblTime.setFont(new Font("Monospaced", Font.BOLD, 24));
        lblTime.setBounds(650, 20, 150, 30);
        gameDialog.add(lblTime);

        gameDialog.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        Timer spawnTimer = new Timer(600, e -> {
            JButton meteor = new JButton();
            try {
                java.net.URL imgUrl = getClass().getResource("/assets/icon/meteor.png");
                if (imgUrl != null) {
                    ImageIcon icon = new ImageIcon(imgUrl);
                    Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    meteor.setIcon(new ImageIcon(img));
                } else {
                    meteor.setText("☄️");
                    meteor.setForeground(Color.ORANGE);
                    meteor.setFont(new Font("Segoe UI Emoji", Font.BOLD, 30));
                }
            } catch (Exception ex) { meteor.setText("O"); }
            meteor.setBorderPainted(false);
            meteor.setContentAreaFilled(false);
            meteor.setFocusPainted(false);
            int x = (int) (Math.random() * 700);
            int y = (int) (Math.random() * 350) + 60; 
            meteor.setBounds(x, y, 80, 80);

            meteor.addActionListener(ev -> {
                score[0]++;
                lblScore.setText("HITS: " + score[0]);
                gameDialog.remove(meteor); 
                gameDialog.repaint();
            });
            gameDialog.add(meteor);
            gameDialog.repaint();

            Timer removeTimer = new Timer(1200, evt -> {
                if (meteor.getParent() != null) { gameDialog.remove(meteor); gameDialog.repaint(); }
            });
            removeTimer.setRepeats(false);
            removeTimer.start();
        });

        Timer gameLoop = new Timer(1000, e -> {
            timeHeader[0]--;
            lblTime.setText("TIME: " + timeHeader[0]);
            if (timeHeader[0] <= 0) {
                ((Timer)e.getSource()).stop();
                spawnTimer.stop();
                gameDialog.dispose(); 
                evaluateMiniGameResult(score[0]);
            }
        });

        spawnTimer.start();
        gameLoop.start();
        gameDialog.setVisible(true);
    }

    private void evaluateMiniGameResult(int score) {
        if (score >= 10) {
            appendLog("✅ PERTAHANAN SUKSES! (" + score + " hits)");
            appendLog("Meteor berhasil dihancurkan. Kapal aman.");
            JOptionPane.showMessageDialog(this, "Great Job! Ship Secured.", "Defense Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int damage = (10 - score) * 3;
            oxygen -= damage;
            appendLog("❌ PERTAHANAN GAGAL! (" + score + " hits)");
            appendLog("Lambung kapal bocor! Oxygen -" + damage + "%");
            JOptionPane.showMessageDialog(this, "Impact Detected! Oxygen Leaking...", "Defense Failed", JOptionPane.WARNING_MESSAGE);
        }
        if (oxygen < 0) oxygen = 0;
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
        
        // PENTING: Game Menang = Selesai (isFinished = true)
        saveProgress(true); 
        
        main.showEnding("MISSION SUCCESS", "Kamu berhasil bertahan hidup!", "assets/ending/win.jpg", true);
    }

    private void triggerGameOver(String reason) {
        isGameOver = true;
        running = false;
        
        // PENTING: Game Over = Selesai (isFinished = true)
        saveProgress(true);
        
        main.showEnding("GAME OVER", reason, "assets/ending/lose.jpg", false);
    }
}