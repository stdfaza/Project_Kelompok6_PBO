import java.awt.*;
import javax.swing.*;

public class Scene extends JPanel {
    public int triggerDay;
    public Image background;
    public Image character;
    public Image character2;
    public String[] dialog;

    public Scene(int triggerDay, String bgPath, String charPath, String[] dialog) {
        this.triggerDay = triggerDay;
        this.background = new ImageIcon(bgPath).getImage();
        this.character = new ImageIcon(charPath).getImage();
        this.dialog = dialog;
    }

    public Scene(int triggerDay, String bgPath, String charPath, String charPath2, String[] dialog) {
        this.triggerDay = triggerDay;
        this.background = new ImageIcon(bgPath).getImage();
        this.character = new ImageIcon(charPath).getImage();
        this.character2 = new ImageIcon(charPath2).getImage();
        this.dialog = dialog;
    }
}