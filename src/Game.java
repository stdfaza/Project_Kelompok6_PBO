import javax.swing.*;
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
            bgImage = new ImageIcon(getClass().getResource("/assets/scene/image.jpg")).getImage();
        } catch (Exception e) {
            System.out.println("Gagal load background.");
        }
    }

    private void setupUI() {
        // 1. PANEL KIRI (Action Buttons)
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

        // 2. PANEL KANAN ATAS (Notification Button)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(20, 0, 0, 20));

        btnNotification = createImageButton("/assets/icon/image3.png", "Check Status & Log");
        btnNotification.addActionListener(e -> showStatusPopup());
        
        rightPanel.add(btnNotification);

        // 3. PANEL KANAN BAWAH (Next Day Button)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(0, 0, 20, 20));

        JButton btnNextDay = createImageButton("/assets/btn_next.png", "Solar Charge (Next Day)");
        btnNextDay.addActionListener(e -> endDay());
        bottomPanel.add(btnNextDay);

        // Add to Main Layout
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    // --- POP-UP STATUS SYSTEM ---
    private void showStatusPopup() {
        JDialog popup = new JDialog(main, "System Status", true);
        popup.setSize(400, 500);
        popup.setLayout(new BorderLayout());
        popup.setLocationRelativeTo(main);
        popup.setUndecorated(true);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(new LineBorder(Color.WHITE, 2));
        
        // Header Stats
        JPanel statsPanel = new JPanel(new GridLayout(4, 1));
        statsPanel.setBackground(new Color(50, 50, 50));
        statsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        statsPanel.add(createPopupLabel("📅 DAY: " + day + "/10"));
        statsPanel.add(createPopupLabel("💨 OXYGEN: " + oxygen + "%", oxygen < 20));
        statsPanel.add(createPopupLabel("🍖 FOOD: " + food + "%", food < 20));
        statsPanel.add(createPopupLabel("⚡ POWER: " + power + "%", power < 20));
        
        // Log Content
        JTextArea popupLog = new JTextArea();
        popupLog.setText(logHistory.toString());
        popupLog.setEditable(false);
        popupLog.setBackground(new Color(20, 20, 20));
        popupLog.setForeground(Color.GREEN);
        popupLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        popupLog.setLineWrap(true);
        popupLog.setWrapStyleWord(true);
        
        JScrollPane scroll = new JScrollPane(popupLog);
        scroll.setBorder(null);
        popupLog.setCaretPosition(popupLog.getDocument().getLength());

        // Footer Button
        JButton btnClose = new JButton("CLOSE");
        btnClose.setBackground(new Color(200, 50, 50));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> popup.dispose());

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(btnClose, BorderLayout.SOUTH);

        popup.add(panel);
        popup.setVisible(true);
    }

    private JLabel createPopupLabel(String text) {
        return createPopupLabel(text, false);
    }
    
    private JLabel createPopupLabel(String text, boolean isCritical) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setForeground(isCritical ? Color.RED : Color.WHITE);
        return lbl;
    }

    private JButton createImageButton(String path, String tooltip) {
        JButton btn = new JButton();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            btn.setText("O"); 
            btn.setBackground(Color.GRAY);
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
    // GAME LOGIC
    // ==========================================

    private void initScene() {
        scenes.add(new Scene(1, "assets/bg_space.png", "src/assets/scene/astronout.png", 
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
        
        // Passive Drain
        int drain = 15;
        oxygen -= drain;
        food -= drain;

        triggerRandomEvent();
        appendLog("[SYSTEM] : Resource decreased (-" + drain + "%)");

        // Check Critical Conditions
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