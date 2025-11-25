import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPage;
    private LoginPage loginPage;
    private Game gamePage; // Tambahkan referensi Game

    public Main() {
        setTitle("Astronout: Survive in the Outer Planet");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 700); // Sedikit diperbesar agar lega
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPage = new JPanel(cardLayout);

        // Inisialisasi Halaman
        loginPage = new LoginPage(this);
        gamePage = new Game(this); // Inisialisasi Game Page

        // Tambah ke mainPage dengan nama unik
        mainPage.add(loginPage, "LoginPage");
        mainPage.add(gamePage, "GamePage");

        add(mainPage);
        
        // Ubah ini ke "GamePage" jika ingin langsung tes game tanpa login, 
        // atau biarkan "LoginPage" untuk alur normal.
        showPage("GamePage"); 
    }

    public void showPage(String page) {
        cardLayout.show(mainPage, page);
        // Jika masuk ke GamePage, reset/mulai game
        if (page.equals("GamePage")) {
            gamePage.startGame();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}