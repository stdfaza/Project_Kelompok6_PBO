import java.awt.*;
import javax.swing.*;

public class Scene {
    // Trigger Hari
    public int triggerDay;

    // Aset Gambar
    public Image background;
    public Image character;   // Gambar Astronot
    public Image aiCharacter; // Gambar AI (Baru)

    // Data Percakapan: {{Speaker, Text}, {Speaker, Text}}
    public String[][] script; 

    // Status Check
    public boolean hasPlayed = false;

    // Constructor Baru
    public Scene(int triggerDay, String bgPath, String charPath, String aiPath, String[][] script) {
        this.triggerDay = triggerDay;
        this.script = script;
        
        // Load gambar
        try {
            if (bgPath != null) this.background = new ImageIcon(getClass().getResource(bgPath)).getImage();
            if (charPath != null) this.character = new ImageIcon(getClass().getResource(charPath)).getImage();
            if (aiPath != null) this.aiCharacter = new ImageIcon(getClass().getResource(aiPath)).getImage();
        } catch (Exception e) {
            System.out.println("Error loading scene images: " + e.getMessage());
        }
    }
}