import util.FileUtil;
import view.LoginFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FileUtil.initDataDirectory();
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}