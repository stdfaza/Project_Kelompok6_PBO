import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// SEKARANG TURUNAN DARI GameBase
public class Game extends GameBase implements Page {

    // --- GAME VARIABLES ---
    private int day;
    private int oxygen;
    private int food;
    private int power;
    private int starvationDays;
    private boolean isGameOver;
    private boolean running = true;
    
    // Scene System
    private List<Scene> scenes = new ArrayList<>();
    private Thread sceneThread;

    // UI Assets
    private Image bgImage;
    private JButton btnNotification;
    
    // --- AUDIO CLIPS ---
    private Clip bgmClip;       // Musik Background Utama
    private Clip miniGameClip;  // Musik saat Mini Game Meteor

    public Game(Main main) {
        super(main); // Panggil constructor GameBase

        // Load Musik Utama (Ganti path sesuai file kamu)
        loadBGM("/assets/audio/meet-the-princess.wav"); 
        
        loadAssets();
        initScene();
        setupUI();
    }

    private void loadAssets() {
        try {
            java.net.URL imgUrl = getClass().getResource("/assets/scene/background.png");
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

        // PANGGIL FUNGSI DARI GAMEBASE
        JButton btnOxygen = createImageButton("/assets/icon/oxygenicon.png", "Recycle Oxygen", 100, 100);
        JButton btnFood = createImageButton("/assets/icon/foodicon.png", "Synthesize Food", 100, 100);

        btnOxygen.addActionListener(e -> performAction("OXYGEN"));
        btnFood.addActionListener(e -> performAction("FOOD"));

        leftPanel.add(btnOxygen);
        leftPanel.add(Box.createVerticalStrut(15)); 
        leftPanel.add(btnFood);

        // 2. PANEL KANAN ATAS (Tombol Notifikasi)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(20, 0, 0, 20));

        btnNotification = createImageButton("/assets/icon/notificationicon.png", "Check Status & Log", 150, 150);
        btnNotification.addActionListener(e -> showStatusPopup());
        
        rightPanel.add(btnNotification);

        // 3. PANEL KANAN BAWAH (Tombol Next Day)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(0, 0, 20, 20));

        JButton btnNextDay = createImageButton("/assets/icon/daychangeicon.png", "Solar Charge (Next Day)", 120, 120);
        btnNextDay.addActionListener(e -> endDay());
        bottomPanel.add(btnNextDay);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    // ... (Kode showStatusPopup SAMA SEPERTI SEBELUMNYA) ...
    // ... Agar kode tidak terlalu panjang, saya persingkat bagian yang tidak berubah ...
    private void showStatusPopup() {
        // (Copy isi showStatusPopup dari kode sebelumnya di sini)
        // Pastikan kodenya sama persis dengan versi "Industrial Metal" terakhir
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
        statsPanel.setBorder(new CompoundBorder(new LineBorder(metalLight, 2), new EmptyBorder(10, 15, 10, 15))); 
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) { g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else { g.setColor(Theme.BACK_COLOR); g.fillRect(0, 0, getWidth(), getHeight()); }
    }

    // ==========================================
    // LOGIKA GAME & SAVE/LOAD
    // ==========================================

    @Override
    public void startNewGame() {
        day = 1; oxygen = 100; food = 100; power = 100; 
        starvationDays = 0; isGameOver = false; running = true;
        
        for(Scene s : scenes) s.hasPlayed = false;
        
        logHistory.setLength(0);
        appendLog("=== NEW GAME STARTED ==="); 
        appendLog("System Online. Good luck, Astronaut.");
        
        saveProgress(false); 
        
        boolean sceneFound = checkSceneTrigger();
        if (!sceneFound) {
            main.showGame();
        }
        
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
        db.saveGame(main.getCurrentAstronoutId(), day, oxygen, food, power, isFinished);
        if (!isFinished) appendLog("[SYSTEM] Progress Auto-Saved.");
    }

    private void initScene() {
        String[][] introScript = {
            {"Azkal", "Ugh... kepalaku sakit sekali. Dimana ini?"},
            {"AI", "Sistem pendukung hidup: Online. Selamat pagi, Kapten Azkal."},
            {"Azkal", "Suara itu... AI Pesawat? Apa yang terjadi? Dimana Bumi?"},
            {"AI", "Laporan status: Meteor besar telah menghantam Bumi."},
            {"Azkal", "Tidak mungkin... Hancur? Semuanya?"},
            {"AI", "Afirmatif. Kita adalah satu-satunya unit yang selamat."},
            {"Azkal", "Berapa lama kita bisa bertahan?"},
            {"AI", "Estimasi: 10 hari sebelum kegagalan sistem total."},
            {"Azkal", "10 hari... Baik. Mari kita mulai bekerja."}
        };

        scenes.add(new Scene(1, 
            "/assets/scene/background.png", 
            "/assets/scene/astronout.png", 
            "/assets/scene/ai.png", 
            introScript
        ));
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

    @Override
    public void endDay() {
        if (isGameOver) return;
        appendLog("\n[SYSTEM] : Day " + day + " Ended.");
        power = Math.min(100, power + 50);
        int drain = 15;
        oxygen -= drain;
        food -= drain;
        triggerRandomEvent();
        appendLog("[SYSTEM] : Resource decreased (-" + drain + "%)");
        if (oxygen <= 0) { oxygen = 0; triggerGameOver("Kehabisan Oksigen."); return; }
        if (food <= 0) {
            food = 0;
            starvationDays++;
            if (starvationDays >= 2) { triggerGameOver("Mati Kelaparan."); return; }
            appendLog("⚠ WARNING: Makanan Habis!");
        } else {
            starvationDays = 0;
        }
        day++;
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
    
    // --- MINI GAME: METEOR DEFENSE (DENGAN MUSIK KHUSUS) ---
    private void startMeteorGame() {
        // 1. STOP Musik Game Utama
        stopBGM();
        // 2. PLAY Musik Mini Game (Tegang)
        // Pastikan file ini ada di folder assets
        playMiniGameBGM("/assets/audio/battle.wav"); 

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
        lblScore.setFont(Theme.FONT.deriveFont(Font.BOLD, 24));
        lblScore.setBounds(30, 20, 200, 30);
        gameDialog.add(lblScore);

        JLabel lblTime = new JLabel("TIME: 10");
        lblTime.setForeground(Color.RED);
        lblTime.setFont(Theme.FONT.deriveFont(Font.BOLD, 24));
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
                
                // 3. STOP Musik Mini Game
                stopMiniGameBGM();
                // 4. RESUME Musik Game Utama
                if (running) {
                    bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
                    bgmClip.start();
                }

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
            JOptionPane.showMessageDialog(this, "Great Job! Ship Secured.", "Defense Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int damage = (10 - score) * 3;
            oxygen -= damage;
            appendLog("❌ PERTAHANAN GAGAL! (" + score + " hits)");
            JOptionPane.showMessageDialog(this, "Impact Detected! Oxygen Leaking...", "Defense Failed", JOptionPane.WARNING_MESSAGE);
        }
        if (oxygen < 0) oxygen = 0;
    }
    
    private void startSceneEvent() {
        sceneThread = new Thread(() -> {
            while (running) {
                try { Thread.sleep(500); } catch (InterruptedException e) { break; }
            }
        });
        sceneThread.start();
    }

    private boolean checkSceneTrigger() {
        for (Scene s : scenes) {
            if (s.triggerDay == day && !s.hasPlayed) {
                s.hasPlayed = true;
                SwingUtilities.invokeLater(() -> main.showScene(s));
                return true; 
            }
        }
        return false; 
    }

    private void winGame() {
        isGameOver = true;
        running = false;
        stopBGM(); // Stop music
        saveProgress(true); 
        main.showEnding("MISSION SUCCESS", "Kamu berhasil bertahan hidup!", "/assets/ending/image.jpg", true);
    }

    private void triggerGameOver(String reason) {
        isGameOver = true;
        running = false;
        stopBGM(); // Stop music
        saveProgress(true);
        main.showEnding("GAME OVER", reason, "/assets/scene/background2.png", false);
    }

    // --- AUDIO HELPER METHODS ---

    @Override
    public void loadBGM(String path) {
        try {
            if (bgmClip != null) { bgmClip.stop(); bgmClip.close(); }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource(path));
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioStream);
        } catch (Exception e) {
            System.out.println("Error load BGM: " + path);
        }
    }

    @Override
    public void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
        }
    }

    // Helper khusus untuk Mini Game BGM
    public void playMiniGameBGM(String path) {
        try {
            if (miniGameClip != null) { miniGameClip.stop(); miniGameClip.close(); }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource(path));
            miniGameClip = AudioSystem.getClip();
            miniGameClip.open(audioStream);
            miniGameClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop selama mini game
            miniGameClip.start();
        } catch (Exception e) {
            System.out.println("Error load Mini Game BGM");
        }
    }

    public void stopMiniGameBGM() {
        if (miniGameClip != null && miniGameClip.isRunning()) {
            miniGameClip.stop();
        }
    }

    @Override
    public void setVisible(boolean flag) {
        super.setVisible(flag);
        if (flag) {
            // Resume musik saat halaman game muncul kembali
            if (bgmClip != null) {
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
                bgmClip.start();
            }
        } else {
            // Stop musik saat pindah ke halaman lain
            stopBGM();
        }
    }
}