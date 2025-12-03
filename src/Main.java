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

    private int currentAstronoutId;
    private String currentAstronoutUsername;

    public Main() {
        setTitle("Astronout: Survive in the Outer Planet");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPage = new JPanel(cardLayout);

        loginPage = new LoginPage(this);
        homePage = new HomePage(this);
        gamePage = new Game(this);
        scenePage = new ScenePage(this);
        endingPage = new EndingPage(this);

        mainPage.add(loginPage, "LoginPage");
        mainPage.add(homePage, "HomePage");
        mainPage.add(gamePage, "GamePage");
        mainPage.add(scenePage, "ScenePage");
        mainPage.add(endingPage, "EndingPage");

        add(mainPage);
        showPage("HomePage");
    }

    public void showPage(String page) {
        cardLayout.show(mainPage, page);
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
        showPage("HomePage");
    }
    
    public void showEnding(String title, String desc, String imgPath, boolean isWin) {
        endingPage.setEnding(title, desc, imgPath, isWin);
        cardLayout.show(mainPage, "EndingPage");
    }

    public int getCurrentAstronoutId() {
        return currentAstronoutId;
    }

    // --- LOGIKA BARU ---

    public void startNewGame() {
        cardLayout.show(mainPage, "GamePage");
        gamePage.startNewGame();
    }

    public void continueGame() {
        DatabaseConnection db = new DatabaseConnection();
        
        // 1. Cek apakah ada Save Data
        if (!db.hasSaveData(currentAstronoutId)) {
            JOptionPane.showMessageDialog(this, "Tidak ada data permainan.", "Load Failed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Cek apakah Game Terakhir SUDAH SELESAI (Menang/Kalah)
        if (db.isGameFinished(currentAstronoutId)) {
            JOptionPane.showMessageDialog(this, 
                "Permainan sebelumnya sudah berakhir (Menang/Kalah).\nSilakan mulai New Game.", 
                "Game Finished", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 3. Jika Valid, Load Game
        cardLayout.show(mainPage, "GamePage");
        gamePage.continueGame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}