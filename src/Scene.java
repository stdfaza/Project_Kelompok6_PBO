import java.awt.*;
import javax.swing.*;

public class Scene extends JPanel {
    // Trigger
    public int triggerDay;

    // Path & Data
    public Image background;
    public Image character;
    public String[] dialog;

    // Status Check
    public boolean hasPlayed = false;

    // Constructor ini yang dicari oleh error tersebut (4 Parameter)
    public Scene(int triggerDay, String bgPath, String charPath, String[] dialog) {
        this.triggerDay = triggerDay;
        
        // Load gambar dari path
        try {
            if (bgPath != null) {
                this.background = new ImageIcon(getClass().getResource(bgPath)).getImage();
            }
            if (charPath != null) {
                this.character = new ImageIcon(getClass().getResource(charPath)).getImage();
            }
        } catch (Exception e) {
            System.out.println("Error loading scene images: " + e.getMessage());
        }

        this.dialog = dialog;
    }
}