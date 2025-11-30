import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ScenePage extends JPanel {
    private Main main;
    private Scene scene;
    private int dialogIndex = 0;

    public ScenePage(Main main) {
        this.main = main;

        setLayout(null);
        setBackground(Color.BLACK);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (scene == null) return;

                dialogIndex++;

                if (dialogIndex >= scene.lines.length) {
                    main.showGame();
                } else {
                    repaint();
                }
            }
        });
    }

    /** Load scene */
    public void loadScene(Scene scene) {
        this.scene = scene;
        this.dialogIndex = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (scene == null) return;

        // ====== Draw Background ======
        g.drawImage(scene.background, 0, 0, getWidth(), getHeight(), null);

        // ====== Current Line ======
        SceneLine current = scene.lines[dialogIndex];
        Character speaker = current.speaker;

        // ====== Draw Character Image ======
        if (speaker != null && speaker.getImage() != null) {

            int x = 0;

            if ("LEFT".equals(current.position)) {
                x = 100;  // posisi kiri
            } else if ("RIGHT".equals(current.position)) {
                x = getWidth() - 600; // posisi kanan (menempel kanan)
            }

            g.drawImage(speaker.getImage(), x, 80, 500, 600, null);
        }

        // ====== Draw Dialogue Box ======
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(50, 500, 1180, 180, 25, 25);

        // ====== Draw Speaker Name ======
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));

        if (speaker != null) {
            g.drawString(speaker.getName(), 70, 540);
        } else {
            g.drawString("NARRATOR", 70, 540);
        }

        // ====== Draw Dialogue Text ======
        g.setFont(new Font("Arial", Font.PLAIN, 24));

        String text;

        if (speaker == null) {
            // Narator — gunakan dialogueIndex sebagai teks langsung
            text = current.narrationText;
        } else {
            // Ambil dialog dari karakter
            text = speaker.getDialog(current.dialogueIndex);
        }

        g.drawString(text, 70, 585);
    }
}