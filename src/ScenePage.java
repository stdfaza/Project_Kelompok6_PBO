import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ScenePage extends JPanel {
    private Main main;
    private Scene scene;
    private int dialogIndex = 0;

    public ScenePage(Main main) {
        this.main = main;
        setLayout(null);
        setBackground(Theme.BACK_COLOR);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (scene == null) return;

                dialogIndex++;
                
                // PERBAIKAN 1: Gunakan scene.dialog.length
                if(dialogIndex >= scene.dialog.length) {
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
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(scene == null) return;

        // 1. Background
        if (scene.background != null) {
            g.drawImage(scene.background, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. Character
        if (scene.character != null)
            g.drawImage(scene.character, 400, 120, 400, 500, this);

        // 3. Dialogue Box
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(50, 500, 1180, 150, 20, 20);

        // 4. Text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        
        // PERBAIKAN 2 (Baris 50): Gunakan scene.dialog
        if (scene.dialog != null && dialogIndex < scene.dialog.length) {
            g.drawString(scene.dialog[dialogIndex], 70, 560);
        }
    }
}