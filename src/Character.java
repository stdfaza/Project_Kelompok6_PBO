import javax.swing.*;
import java.awt.*;

public class Character {
    private String name;
    private Image characterImage;
    private String[] dialogues;

    public Character(String name, String characterPath, String[] dialogues) {
        this.name = name;
        this.characterImage = new ImageIcon(getClass().getResource(characterPath)).getImage();
        this.dialogues = dialogues;
    }

    public String getDialog(int index) {
        return dialogues[index];
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return characterImage;
    }
}