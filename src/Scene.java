import java.awt.*;
import javax.swing.*;

public class Scene {
    // Trigger
    public int triggerDay;

    // Path
    public Image background;
    public String music;

    // SceneLine
    public SceneLine[] lines;

    // Check
    public boolean hasPlayed = false;

    public Scene(int triggerDay, String backgroundPath, String music, SceneLine[] lines) {
        this.triggerDay = triggerDay;
        this.background = new ImageIcon(backgroundPath).getImage();
        this.music = music;
        this.lines = lines;
    }
}