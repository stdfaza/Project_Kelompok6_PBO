import javax.swing.*;
import java.awt.*;
import java.awt.event.*;;

public class ScenePage extends JPanel{
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
                dialogIndex++;
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

        // Background
        g.drawImage(scene.background, 0, 0, getWidth(), getHeight(), null);

        // Character
        if (scene.character != null)
            g.drawImage(scene.character, 400, 120, 400, 500, null);

        // Dialogue box
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(50, 500, 1180, 150, 20, 20);

        // Text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString(scene.dialog[dialogIndex], 70, 560);
    }
}
