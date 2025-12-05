import java.awt.*;
import javax.swing.*;

public class Scene {
    // Trigger Hari
    public int triggerDay;

    // Aset Gambar dan Audio
    public Image background;
    public Image character;
    public Image character2;
    public String sceneClipPath;

    // Data Percakapan: {{Speaker, Text, Position}, {Speaker, Text, Position}}
    public String[][] script; 

    // Status Check
    public boolean hasPlayed = false;

    // Constructor Baru
    public Scene(int triggerDay, String bgPath, String charPath, String char2Path, String[][] script, String sceneClipPath) {
        this.triggerDay = triggerDay;
        this.script = script;
        this.sceneClipPath = sceneClipPath;
        
        // Load gambar
        try {
            if (bgPath != null) this.background = new ImageIcon(getClass().getResource(bgPath)).getImage();
            if (charPath != null) this.character = new ImageIcon(getClass().getResource(charPath)).getImage();
            if (char2Path != null) this.character2 = new ImageIcon(getClass().getResource(char2Path)).getImage();

        } catch (Exception e) {
            System.out.println("Error loading scene images: " + e.getMessage());
        }
    }
}