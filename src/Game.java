import javax.swing.*;
import java.awt.*;

public class Game extends JPanel {
    private Main main;

    // Game Variables
    private int day;
    private int oxygen;
    private int food;
    private int power;
    private int starvationDays; // Counter hari tanpa makan
    private boolean isGameOver;

    // UI Components
    private JLabel dayLabel, oxygenLabel, foodLabel, powerLabel;
    private JTextArea logArea;
    private JButton btnOxygen, btnFood, btnNextDay;

    public Game(Main main) {
        this.main = main;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // 1. Top Panel (Stats)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4));
        statsPanel.setBackground(Color.DARK_GRAY);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dayLabel = createStatLabel("Day: 0");
        oxygenLabel = createStatLabel("Oxygen: 100%");
        foodLabel = createStatLabel("Food: 100%");
        powerLabel = createStatLabel("Power: 100%");

        statsPanel.add(dayLabel);
        statsPanel.add(oxygenLabel);
        statsPanel.add(foodLabel);
        statsPanel.add(powerLabel);

        add(statsPanel, BorderLayout.NORTH);

        // 2. Center Panel (Story Log)
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Bottom Panel (Actions)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        actionPanel.setBackground(Color.DARK_GRAY);

        btnOxygen = createActionButton("Recycle Oxygen (-20 Power)");
        btnFood = createActionButton("Synthesize Food (-20 Power)");
        btnNextDay = createActionButton("Solar Charge (END DAY)");

        btnOxygen.addActionListener(e -> performAction("OXYGEN"));
        btnFood.addActionListener(e -> performAction("FOOD"));
        btnNextDay.addActionListener(e -> endDay());

        actionPanel.add(btnOxygen);
        actionPanel.add(btnFood);
        actionPanel.add(btnNextDay);

        add(actionPanel, BorderLayout.SOUTH);
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

    private JButton createActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(Color.LIGHT_GRAY);
        btn.setFocusPainted(false);
        return btn;
    }

    // --- GAME LOGIC UTAMA ---

    public void startGame() {
        day = 1;
        oxygen = 100;
        food = 100;
        power = 100;
        starvationDays = 0;
        isGameOver = false;

        enableButtons(true);
        logArea.setText("");
        appendLog("=== DAY 1: SURVIVAL START ===");
        appendLog("Status: Oksigen Penuh, Makanan Penuh.");
        appendLog("PERINGATAN: Jika Makanan 0 selama 2 hari berturut-turut, kamu akan mati.");
        appendLog("------------------------------------------------\n");
        
        updateUIStats();
    }

    // Method 1: Aksi Instan (TIDAK Menambah Hari, TIDAK Memicu Kelaparan)
    private void performAction(String action) {
        if (isGameOver) return;

        if (power < 20) {
            appendLog("⚠️ GAGAL: Power tidak cukup (Butuh 20%). Akhiri hari untuk charge.");
            return;
        }

        power -= 20; // Cost Power

        if (action.equals("OXYGEN")) {
            oxygen = Math.min(100, oxygen + 30);
            appendLog("> Oksigen didaur ulang. (+30% O2)");
        } else if (action.equals("FOOD")) {
            food = Math.min(100, food + 30);
            appendLog("> Makanan disintesis. (+30% Food)");
        }

        // Cek hanya jika Oksigen habis mendadak (sangat jarang terjadi di sini, tapi safety)
        if (oxygen <= 0) triggerGameOver("KADAR OKSIGEN HABIS SAAT BEKERJA.");
        
        updateUIStats();
    }

    // Method 2: Ganti Hari (Di sini logika Starvation bekerja)
    private void endDay() {
        if (isGameOver) return;

        appendLog("\n=== MALAM HARI ===");
        
        // 1. Regenerasi Power & Konsumsi Resource
        power = Math.min(100, power + 50);
        int dailyDrain = 15;
        oxygen -= dailyDrain;
        food -= dailyDrain;

        appendLog("Tidur... (-" + dailyDrain + "% Oksigen, -" + dailyDrain + "% Makanan)");

        // 2. LOGIKA KEMATIAN (PENTING: Urutan Pengecekan)
        
        // A. Cek Oksigen (Langsung mati)
        if (oxygen <= 0) {
            oxygen = 0;
            triggerGameOver("KADAR OKSIGEN HABIS. Kamu mati dalam tidur.");
            return;
        }

        // B. Cek Makanan (Sistem Bertahap 2 Hari)
        if (food <= 0) {
            food = 0;
            starvationDays++; // Tambah counter kelaparan
            
            if (starvationDays >= 2) {
                triggerGameOver("KELAPARAN TOTAL. Tubuhmu tidak bisa bertahan lagi.");
                return;
            } else {
                appendLog("⚠️ PERINGATAN KRITIS: Makanan Habis! (" + starvationDays + "/2 hari sebelum mati).");
            }
        } else {
            // Jika ada makanan, reset counter kelaparan
            if (starvationDays > 0) appendLog("Status Nutrisi: Pulih.");
            starvationDays = 0;
        }

        // 3. Masuk Hari Baru
        day++;
        updateUIStats();
        
        if (day > 10) {
            winGame();
        } else {
            appendLog("\n=== DAY " + day + " ===");
        }
    }

    private void winGame() {
        isGameOver = true;
        enableButtons(false);
        JOptionPane.showMessageDialog(this, "SELAMAT! Bantuan telah datang!", "Victory", JOptionPane.INFORMATION_MESSAGE);
    }

    private void triggerGameOver(String reason) {
        isGameOver = true;
        enableButtons(false);
        updateUIStats(); // Update terakhir biar user lihat angka 0
        appendLog("\n💀 GAME OVER: " + reason);
        JOptionPane.showMessageDialog(this, reason, "Game Over", JOptionPane.ERROR_MESSAGE);
    }

    private void updateUIStats() {
        dayLabel.setText("Day: " + Math.min(day, 10) + "/10");
        oxygenLabel.setText("Oxygen: " + oxygen + "%");
        foodLabel.setText("Food: " + food + "%");
        powerLabel.setText("Power: " + power + "%");

        // Warna Merah jika kritis atau sedang kelaparan
        oxygenLabel.setForeground(oxygen < 20 ? Color.RED : Color.WHITE);
        foodLabel.setForeground(food <= 0 ? Color.RED : (food < 20 ? Color.YELLOW : Color.WHITE));
        
        // Indikator Power
        powerLabel.setForeground(power < 20 ? Color.RED : Color.YELLOW);
    }

    private void enableButtons(boolean enabled) {
        btnOxygen.setEnabled(enabled);
        btnFood.setEnabled(enabled);
        btnNextDay.setEnabled(enabled);
    }

    private void appendLog(String text) {
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}