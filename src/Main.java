import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPage;
    private LoginPage loginPage;
    private HomePage homePage;
    private Game gamePage;
    private ScenePage scenePage;
    private EndingPage endingPage;

    // Variabel Login
    private int currentAstronoutId;
    private String currentAstronoutUsername;

    public Main() {
        setTitle("Astronout: Survive in the Outer Planet");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPage = new JPanel(cardLayout);

        // Inisialisasi halaman
        loginPage = new LoginPage(this);
        homePage = new HomePage(this);
        gamePage = new Game(this);
        scenePage = new ScenePage(this);
        endingPage = new EndingPage(this);

        // Add ke card layout
        mainPage.add(loginPage, "LoginPage");
        mainPage.add(homePage, "HomePage");
        mainPage.add(gamePage, "GamePage");
        mainPage.add(scenePage, "ScenePage");
        mainPage.add(endingPage, "EndingPage");

        add(mainPage);

        showPage("GamePage");
    }

    public void showPage(String page) {
        cardLayout.show(mainPage, page);

        if (page.equals("GamePage")) {
            gamePage.startGame();
        }
    }

    public void showScene(Scene s) {
        scenePage.loadScene(s);
        cardLayout.show(mainPage, "ScenePage");
    }

    public void showGame() {
        cardLayout.show(mainPage, "GamePage");
    }

    public void onLoginSuccess(int id, String username) {
        currentAstronoutId = id;
        currentAstronoutUsername = username;

        // ⬅ Setelah login → pindah ke HomePage
        showPage("HomePage");
    }
    public void showEnding(String title, String desc, String imgPath, boolean isWin) {
        endingPage.setEnding(title, desc, imgPath, isWin);
        cardLayout.show(mainPage, "EndingPage");
    }

    public int getCurrentAstronoutId() {
        return currentAstronoutId;
    }

    public String getCurrentAstronoutUsername() {
        return currentAstronoutUsername;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}