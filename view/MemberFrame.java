package view;

import controller.ActivityController;
import model.Activity;
import model.Member;
import model.Registration;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MemberFrame extends JFrame {

    private final ActivityController controller;
    private final Member member;

    private JTable activityTable;
    private DefaultTableModel activityModel;
    private JTable myRegTable;
    private DefaultTableModel myRegModel;

    public MemberFrame(ActivityController controller, Member member) {
        this.controller = controller;
        this.member = member;
        initUI();
        refreshAll();
    }

    private void initUI() {
        setTitle("Club Sportif – Espace Membre : " + member.getFullName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 580);
        setLocationRelativeTo(null);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(39, 174, 96));
        header.setPreferredSize(new Dimension(860, 58));

        JLabel lbl = new JLabel("   Bienvenue, " + member.getFirstName() + " " + member.getLastName() + " !");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);

        JButton btnPwd    = styledBtn("Changer mot de passe", new Color(41, 128, 185));
        JButton btnLogout = styledBtn("Deconnexion",          new Color(180, 40, 40));
        btnPwd.addActionListener(e    -> showChangePwdDialog());
        btnLogout.addActionListener(e -> {
            if (confirm("Voulez-vous vraiment vous deconnecter ?")) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        JPanel hp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        hp.setOpaque(false);
        hp.add(btnPwd);
        hp.add(btnLogout);
        header.add(lbl, BorderLayout.WEST);
        header.add(hp,  BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.addTab("Activites disponibles", buildActivitiesTab());
        tabs.addTab("Mes inscriptions",       buildMyRegistrationsTab());
        tabs.addTab("Mon profil",             buildProfileTab());

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }

    // ── ACTIVITIES TAB ───────────────────────────────

    private JPanel buildActivitiesTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID","Nom","Description","Horaires","Places restantes","Statut"};
        activityModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        activityTable = new JTable(activityModel);
        activityTable.setRowHeight(24);
        activityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activityTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        activityTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    String statut = (String) t.getModel().getValueAt(row, 5);
                    c.setBackground("COMPLET".equals(statut) ? new Color(255, 200, 200) : Color.WHITE);
                }
                return c;
            }
        });

        JButton btnRegister = styledBtn("S'inscrire a l'activite", new Color(39, 174, 96));
        JButton btnRefresh  = styledBtn("Rafraichir",              Color.GRAY);
        btnRegister.addActionListener(e -> registerToSelected());
        btnRefresh.addActionListener(e  -> refreshAll());

        panel.add(buttonBar(btnRegister, btnRefresh), BorderLayout.NORTH);
        panel.add(new JScrollPane(activityTable), BorderLayout.CENTER);
        return panel;
    }

    private void registerToSelected() {
        int row = activityTable.getSelectedRow();
        if (row < 0) { warn("Veuillez selectionner une activite."); return; }
        String statut = (String) activityModel.getValueAt(row, 5);
        if ("COMPLET".equals(statut)) { warn("Cette activite est complete."); return; }
        int id = (int) activityModel.getValueAt(row, 0);
        String actName = (String) activityModel.getValueAt(row, 1);
        if (!confirm("Confirmer votre inscription a :\n\"" + actName + "\" ?")) return;
        String err = controller.registerMember(member.getId(), id);
        if (err != null) error(err);
        else { success("Inscription envoyee !\nEn attente de validation par l'administrateur."); refreshAll(); }
    }

    // ── MY REGISTRATIONS TAB ─────────────────────────

    private JPanel buildMyRegistrationsTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID","Activite","Horaires","Date inscription","Statut"};
        myRegModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        myRegTable = new JTable(myRegModel);
        myRegTable.setRowHeight(24);
        myRegTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myRegTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        myRegTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    String status = (String) t.getModel().getValueAt(row, 4);
                    if ("Acceptee".equals(status))    c.setBackground(new Color(198, 246, 213));
                    else if ("Refusee".equals(status)) c.setBackground(new Color(254, 202, 202));
                    else                               c.setBackground(new Color(254, 243, 199));
                }
                return c;
            }
        });

        JButton btnCancel  = styledBtn("Annuler l'inscription", new Color(192, 57, 43));
        JButton btnRefresh = styledBtn("Rafraichir",            Color.GRAY);
        btnCancel.addActionListener(e  -> cancelSelectedReg());
        btnRefresh.addActionListener(e -> refreshAll());

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        legend.add(colorLegend(new Color(198, 246, 213), "Acceptee"));
        legend.add(colorLegend(new Color(254, 243, 199), "En attente"));
        legend.add(colorLegend(new Color(254, 202, 202), "Refusee"));

        panel.add(buttonBar(btnCancel, btnRefresh), BorderLayout.NORTH);
        panel.add(new JScrollPane(myRegTable), BorderLayout.CENTER);
        panel.add(legend, BorderLayout.SOUTH);
        return panel;
    }

    private void cancelSelectedReg() {
        int row = myRegTable.getSelectedRow();
        if (row < 0) { warn("Veuillez selectionner une inscription a annuler."); return; }
        String actName = (String) myRegTable.getValueAt(row, 1);
        if (!confirm("Annuler votre inscription a :\n\"" + actName + "\" ?")) return;
        int regId = (int) myRegModel.getValueAt(row, 0);
        List<Registration> regs = controller.getRegistrationsByMember(member.getId());
        for (Registration r : regs) {
            if (r.getId() == regId) {
                String err = controller.cancelRegistration(member.getId(), r.getActivityId());
                if (err != null) error(err);
                else { success("Inscription annulee."); refreshAll(); }
                return;
            }
        }
        error("Inscription introuvable.");
    }

    // ── PROFILE TAB ──────────────────────────────────

    private JPanel buildProfileTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));
        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setFont(new Font("SansSerif", Font.PLAIN, 14));
        info.setBackground(new Color(245, 245, 245));
        info.setMargin(new Insets(16, 16, 16, 16));
        info.setText(
            "MON PROFIL\n" +
            "===================================\n\n" +
            "  Nom complet   : " + member.getFullName()  + "\n" +
            "  Login         : " + member.getLogin()     + "\n" +
            "  Naissance     : " + member.getBirthDate() + "\n" +
            "  Adresse       : " + member.getAddress()   + "\n" +
            "  Telephone     : " + member.getPhone()     + "\n" +
            "  Email         : " + member.getEmail()     + "\n" +
            "  Poids         : " + member.getWeight()    + " kg\n\n" +
            "  Activites acceptees : " + controller.getMemberActivityCount(member.getId())
        );
        panel.add(info, BorderLayout.CENTER);
        return panel;
    }

    // ── CHANGE PASSWORD ──────────────────────────────

    private void showChangePwdDialog() {
        JPasswordField oldPwd  = new JPasswordField();
        JPasswordField newPwd  = new JPasswordField();
        JPasswordField confPwd = new JPasswordField();
        JPanel p = new JPanel(new GridLayout(3, 2, 8, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(new JLabel("Ancien mot de passe :")); p.add(oldPwd);
        p.add(new JLabel("Nouveau (min 6 car. + 1 chiffre) :")); p.add(newPwd);
        p.add(new JLabel("Confirmer :")); p.add(confPwd);
        int res = JOptionPane.showConfirmDialog(this, p, "Changer le mot de passe", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;
        String err = controller.changeMemberPassword(member.getId(),
                new String(oldPwd.getPassword()),
                new String(newPwd.getPassword()),
                new String(confPwd.getPassword()));
        if (err != null) { error(err); showChangePwdDialog(); }
        else success("Mot de passe change avec succes !");
    }

    // ── REFRESH ──────────────────────────────────────

    private void refreshAll() { refreshActivities(); refreshMyRegistrations(); }

    private void refreshActivities() {
        activityModel.setRowCount(0);
        for (Activity a : controller.getAllActivities()) {
            activityModel.addRow(new Object[]{
                a.getId(), a.getName(), a.getDescription(),
                a.getSchedule(), a.getRemainingPlaces(),
                a.isFull() ? "COMPLET" : "Disponible"
            });
        }
    }

    private void refreshMyRegistrations() {
        myRegModel.setRowCount(0);
        for (Registration r : controller.getRegistrationsByMember(member.getId())) {
            Activity a = controller.getActivityById(r.getActivityId());
            myRegModel.addRow(new Object[]{
                r.getId(),
                a != null ? a.getName()    : "Inconnue",
                a != null ? a.getSchedule(): "-",
                r.getRegistrationDate(),
                r.getStatusLabel()
            });
        }
    }

    // ── HELPERS ──────────────────────────────────────

    private JButton styledBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel buttonBar(JButton... buttons) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        for (JButton b : buttons) p.add(b);
        return p;
    }

    private JPanel colorLegend(Color c, String label) {
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(16, 16));
        box.setBackground(c);
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        row.add(box); row.add(new JLabel(label));
        return row;
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Attention", JOptionPane.WARNING_MESSAGE);
    }
    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    private void success(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Succes", JOptionPane.INFORMATION_MESSAGE);
    }
    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}