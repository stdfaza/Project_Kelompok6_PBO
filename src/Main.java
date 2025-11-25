import javax.swing.*;
import java.awt.*;

public class Main extends JFrame{
    private CardLayout cardLayout;
    private JPanel mainPage;
    private LoginPage loginPage;

    public Main() {
        setTitle("Astronout: Survive in the Outer Planet");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPage = new JPanel(cardLayout);  
        
        // Page
        loginPage = new LoginPage(this);
        
        // Tambah ke mainPage
        mainPage.add(loginPage, "LoginPage");

        // Tambah ke JFrame
        add(mainPage);
        showPage("LoginPage");
    }

    public void showPage(String page) {
        cardLayout.show(mainPage, page);
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}
