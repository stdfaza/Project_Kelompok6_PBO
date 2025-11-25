import javax.swing.*;
import java.awt.*;

public class LoginPage extends JPanel{
    private Main mainPage;

    public LoginPage(Main mainPage) {
        this.mainPage = mainPage;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.DARK_GRAY.darker());
    }
}