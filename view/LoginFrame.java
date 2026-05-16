package view;

import controller.ActivityController;
import model.Member;
import util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final ActivityController controller = new ActivityController();
    private JTextField txtLogin;
    private JPasswordField txtPassword;
    private JLabel lblError;

    public LoginFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Club Sportif – Connexion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel header = new JPanel();
        header.setBackground(new Color(25, 80, 150));
        header.setPreferredSize(new Dimension(420, 65));
        header.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));
        JLabel title = new JLabel("  Club Sportif");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 50, 10, 50));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 6, 8, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.weightx = 0.3;
        form.add(new JLabel("Login :"), g);
        g.gridx = 1; g.weightx = 0.7;
        txtLogin = new JTextField(16);
        form.add(txtLogin, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0.3;
        form.add(new JLabel("Mot de passe :"), g);
        g.gridx = 1; g.weightx = 0.7;
        txtPassword = new JPasswordField(16);
        form.add(txtPassword, g);

        JButton btnLogin = new JButton("Se connecter");
        btnLogin.setBackground(new Color(25, 80, 150));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));

        lblError = new JLabel(" ");
        lblError.setForeground(Color.RED);
        lblError.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel footer = new JPanel(new BorderLayout(0, 6));
        footer.setBorder(new EmptyBorder(0, 30, 20, 30));
        footer.add(btnLogin, BorderLayout.CENTER);
        footer.add(lblError, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(form,   BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> attemptLogin());
        txtPassword.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        String login    = txtLogin.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (!Validator.isNotEmpty(login)) {
            lblError.setText("Le login est obligatoire.");
            txtLogin.requestFocus();
            return;
        }
        if (!Validator.isNotEmpty(password)) {
            lblError.setText("Le mot de passe est obligatoire.");
            txtPassword.requestFocus();
            return;
        }

        Member member = controller.login(login, password);

        if (member == null) {
            lblError.setText("Login ou mot de passe incorrect.");
            txtPassword.setText("");
            return;
        }

        dispose();

        if (member.isMustChangePassword()) {
            showChangePasswordDialog(member);
        } else if (member.isAdmin()) {
            new AdminFrame(controller, member).setVisible(true);
        } else {
            new MemberFrame(controller, member).setVisible(true);
        }
    }

    private void showChangePasswordDialog(Member member) {
        JPasswordField oldPwd  = new JPasswordField();
        JPasswordField newPwd  = new JPasswordField();
        JPasswordField confPwd = new JPasswordField();

        JPanel p = new JPanel(new GridLayout(3, 2, 8, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(new JLabel("Ancien mot de passe :")); p.add(oldPwd);
        p.add(new JLabel("Nouveau mot de passe :")); p.add(newPwd);
        p.add(new JLabel("Confirmer :")); p.add(confPwd);

        int result = JOptionPane.showConfirmDialog(null, p,
                "Changement de mot de passe obligatoire",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            new LoginFrame().setVisible(true);
            return;
        }

        String error = controller.changeMemberPassword(
                member.getId(),
                new String(oldPwd.getPassword()),
                new String(newPwd.getPassword()),
                new String(confPwd.getPassword()));

        if (error != null) {
            JOptionPane.showMessageDialog(null, error, "Erreur", JOptionPane.ERROR_MESSAGE);
            showChangePasswordDialog(member);
        } else {
            JOptionPane.showMessageDialog(null, "Mot de passe changé avec succès !");
            Member updated = controller.getMemberById(member.getId());
            new MemberFrame(controller, updated).setVisible(true);
        }
    }
}