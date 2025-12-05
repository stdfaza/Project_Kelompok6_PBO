import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ScenePage extends JPanel implements Page {
    private Main main;
    private Scene scene;
    private int dialogIndex = 0;
    private Clip bgmClip;

    public ScenePage(Main main) {
        this.main = main;
        setLayout(null);
        setBackground(Theme.BACK_COLOR);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (scene == null) return;

                dialogIndex++;
                
                // Cek apakah dialog sudah habis
                if(dialogIndex >= scene.script.length) {
                    stopBGM();
                    main.showGame(); 
                } else {
                    repaint(); 
                }
            }
        });
    }

    public void loadScene(Scene scene) {
        this.scene = scene;
        this.dialogIndex = 0;

        if (scene.sceneClipPath != null) {
            loadBGM(scene.sceneClipPath);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(scene == null) return;

        // 1. Gambar Background
        if (scene.background != null) {
            g.drawImage(scene.background, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Ambil Data Dialog Saat Ini
        String speaker = "";
        String text = "";
        String position = "left"; // default

        if (dialogIndex < scene.script.length) {
            speaker = scene.script[dialogIndex][0];
            text = scene.script[dialogIndex][1];
            if (scene.script[dialogIndex].length > 2) {
                position = scene.script[dialogIndex][2];  // "left" atau "right"
            }
        }

        // 2. Gambar Karakter Berdasarkan Posisi
        Image characterImage = scene.character;
        Image character2Image = scene.character2;
        
        if (!speaker.equalsIgnoreCase("Narrator")) {
            if (position.equalsIgnoreCase("right")) {
                if (character2Image != null) {
                    g.drawImage(character2Image, getWidth() - 450, 100, 400, 500, this);
                }
            } else {
                if (characterImage != null) {
                    g.drawImage(characterImage, 50, 100, 400, 500, this);
                }
            }
        }

        // 3. Kotak Dialog
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(0, 0, 0, 200)); // Hitam transparan
        g2.fillRoundRect(50, getHeight() - 200, getWidth() - 100, 150, 20, 20);
        
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(50, getHeight() - 200, getWidth() - 100, 150, 20, 20);

        // 4. Nama Speaker (Kuning agar menonjol)
        g.setFont(Theme.FONT.deriveFont(Font.BOLD, 28));
        g.setColor(new Color(255, 215, 0)); // Gold
        g.drawString(speaker, 80, getHeight() - 160);

        // 5. Teks Dialog
        g.setFont(Theme.FONT.deriveFont(Font.PLAIN, 22));
        g.setColor(Color.WHITE);
        g.drawString(text, 80, getHeight() - 120);
        
        // Hint klik
        g.setFont(new Font("Arial", Font.ITALIC, 14));
        g.setColor(Color.GRAY);
        g.drawString("[Klik untuk lanjut]", getWidth() - 200, getHeight() - 70);
    }

    @Override
    public void loadBGM(String path) {
        try {
            if (bgmClip != null) { bgmClip.stop(); bgmClip.close(); }

            AudioInputStream audioStream =
                    AudioSystem.getAudioInputStream(getClass().getResource(path));

            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioStream);

        } catch (Exception e) {
            System.out.println("Error load BGM: " + path + " | " + e.getMessage());
        }
    }

    @Override
    public void stopBGM() {
        if (bgmClip != null) {
            bgmClip.stop();
        }
    }
}