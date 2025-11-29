import java.awt.*;
import javax.swing.*;

public class Scene extends JPanel {
    // Trigger
    public int triggerDay;

    // Path
    public Image background;
    public Image character;
    public String[] dialog;

    // Check
    public boolean hasPlayed = false;

    public Scene(int triggerDay, String bgPath, String charPath, String[] dialog) {
        this.triggerDay = triggerDay;
        this.background = new ImageIcon(bgPath).getImage();
        this.character = new ImageIcon(charPath).getImage();
        this.dialog = dialog;
    }
}