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

public class AdminFrame extends JFrame {

    private final ActivityController controller;
    private final Member admin;

    private JTable memberTable;
    private DefaultTableModel memberModel;
    private JTable activityTable;
    private DefaultTableModel activityModel;
    private JTable regTable;
    private DefaultTableModel regModel;
    private JTextArea statsArea;

    public AdminFrame(ActivityController controller, Member admin) {
        this.controller = controller;
        this.admin = admin;
        initUI();
        refreshAll();
    }

    private void initUI() {
        setTitle("Club Sportif – Administration (" + admin.getFullName() + ")");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 80, 150));
        header.setPreferredSize(new Dimension(1000, 58));
        JLabel lbl = new JLabel("   Club Sportif – Tableau de Bord Administrateur");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);
        JButton btnLogout = styledBtn("Deconnexion", new Color(180, 40, 40));
        btnLogout.addActionListener(e -> {
            if (confirm("Voulez-vous vraiment vous deconnecter ?")) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        JPanel hp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        hp.setOpaque(false);
        hp.add(btnLogout);
        header.add(lbl, BorderLayout.WEST);
        header.add(hp,  BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.addTab("Membres",      buildMembersTab());
        tabs.addTab("Activites",    buildActivitiesTab());
        tabs.addTab("Inscriptions", buildRegistrationsTab());
        tabs.addTab("Statistiques", buildStatsTab());

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }

    // ── MEMBERS TAB ──────────────────────────────────

    private JPanel buildMembersTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID","Login","Prenom","Nom","Naissance","Adresse","Telephone","Email","Poids"};
        memberModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        memberTable = new JTable(memberModel);
        memberTable.setRowHeight(24);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JButton btnAdd    = styledBtn("Ajouter",    new Color(39, 174, 96));
        JButton btnEdit   = styledBtn("Modifier",   new Color(41, 128, 185));
        JButton btnDelete = styledBtn("Supprimer",  new Color(192, 57, 43));
        JButton btnRefresh = styledBtn("Rafraichir", Color.GRAY);

        btnAdd.addActionListener(e    -> showMemberDialog(null));
        btnEdit.addActionListener(e   -> editSelectedMember());
        btnDelete.addActionListener(e -> deleteSelectedMember());
        btnRefresh.addActionListener(e -> refreshAll());

        panel.add(buttonBar(btnAdd, btnEdit, btnDelete, btnRefresh), BorderLayout.NORTH);
        panel.add(new JScrollPane(memberTable), BorderLayout.CENTER);
        return panel;
    }

    private void editSelectedMember() {
        int row = memberTable.getSelectedRow();
        if (row < 0) { warn("Veuillez selectionner un membre."); return; }
        int id = (int) memberModel.getValueAt(row, 0);
        showMemberDialog(controller.getMemberById(id));
    }

    private void deleteSelectedMember() {
        int row = memberTable.getSelectedRow();
        if (row < 0) { warn("Veuillez selectionner un membre a supprimer."); return; }
        String name = memberModel.getValueAt(row, 2) + " " + memberModel.getValueAt(row, 3);
        if (!confirm("Supprimer le membre \"" + name + "\" ?\nSes inscriptions seront supprimees.")) return;
        int id = (int) memberModel.getValueAt(row, 0);
        String err = controller.deleteMember(id);
        if (err != null) error(err);
        else { success("Membre supprime avec succes."); refreshAll(); }
    }

    private void showMemberDialog(Member existing) {
        boolean isEdit = existing != null;
        JTextField fLogin  = new JTextField(isEdit ? existing.getLogin()     : "");
        JTextField fPwd    = new JTextField(isEdit ? existing.getPassword()  : "");
        JTextField fFirst  = new JTextField(isEdit ? existing.getFirstName() : "");
        JTextField fLast   = new JTextField(isEdit ? existing.getLastName()  : "");
        JTextField fBirth  = new JTextField(isEdit ? existing.getBirthDate() : "dd/MM/yyyy");
        JTextField fAddr   = new JTextField(isEdit ? existing.getAddress()   : "");
        JTextField fPhone  = new JTextField(isEdit ? existing.getPhone()     : "");
        JTextField fEmail  = new JTextField(isEdit ? existing.getEmail()     : "");
        JTextField fWeight = new JTextField(isEdit ? String.valueOf(existing.getWeight()) : "");

        if (isEdit) { fLogin.setEditable(false); fLogin.setBackground(new Color(230, 230, 230)); }

        JPanel p = new JPanel(new GridLayout(9, 2, 8, 6));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(new JLabel("Login *:"));                    p.add(fLogin);
        p.add(new JLabel("Mot de passe *:"));             p.add(fPwd);
        p.add(new JLabel("Prenom *:"));                   p.add(fFirst);
        p.add(new JLabel("Nom *:"));                      p.add(fLast);
        p.add(new JLabel("Naissance * (dd/MM/yyyy):"));   p.add(fBirth);
        p.add(new JLabel("Adresse *:"));                  p.add(fAddr);
        p.add(new JLabel("Telephone * (8-15 chiffres):")); p.add(fPhone);
        p.add(new JLabel("Email *:"));                    p.add(fEmail);
        p.add(new JLabel("Poids (kg) *:"));               p.add(fWeight);

        int res = JOptionPane.showConfirmDialog(this, p,
                isEdit ? "Modifier le membre" : "Ajouter un membre",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String err;
        if (isEdit) {
            err = controller.updateMember(existing.getId(),
                    fFirst.getText(), fLast.getText(), fBirth.getText(),
                    fAddr.getText(), fPhone.getText(), fEmail.getText(), fWeight.getText());
        } else {
            err = controller.addMember(fLogin.getText(), fPwd.getText(),
                    fFirst.getText(), fLast.getText(), fBirth.getText(),
                    fAddr.getText(), fPhone.getText(), fEmail.getText(), fWeight.getText());
        }

        if (err != null) { error(err); showMemberDialog(existing); }
        else { success(isEdit ? "Membre modifie." : "Membre ajoute."); refreshAll(); }
    }

    // ── ACTIVITIES TAB ───────────────────────────────

    private JPanel buildActivitiesTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID","Nom","Description","Capacite max","Participants","Places restantes","Horaires","Statut"};
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
                    String statut = (String) t.getModel().getValueAt(row, 7);
                    c.setBackground("COMPLET".equals(statut) ? new Color(255, 200, 200) : Color.WHITE);
                }
                return c;
            }
        });

        JButton btnAdd    = styledBtn("Ajouter",    new Color(39, 174, 96));
        JButton btnEdit   = styledBtn("Modifier",   new Color(41, 128, 185));
        JButton btnDelete = styledBtn("Supprimer",  new Color(192, 57, 43));
        JButton btnRefresh = styledBtn("Rafraichir", Color.GRAY);

        btnAdd.addActionListener(e    -> showActivityDialog(null));
        btnEdit.addActionListener(e   -> editSelectedActivity());
        btnDelete.addActionListener(e -> deleteSelectedActivity());
        btnRefresh.addActionListener(e -> refreshAll());

        panel.add(buttonBar(btnAdd, btnEdit, btnDelete, btnRefresh), BorderLayout.NORTH);
        panel.add(new JScrollPane(activityTable), BorderLayout.CENTER);
        return panel;
    }

    private void editSelectedActivity() {
        int row = activityTable.getSelectedRow();
        if (row < 0) { warn("Veuillez selectionner une activite."); return; }
        int id = (int) activityModel.getValueAt(row, 0);
        showActivityDialog(controller.getActivityById(id));
    }

    private void deleteSelectedActivity() {
        int row = activityTable.getSelectedRow();
        if (row < 0) { warn("Veuillez selectionner une activite a supprimer."); return; }
        String name = (String) activityModel.getValueAt(row, 1);
        if (!confirm("Supprimer l'activite \"" + name + "\" ?\nToutes ses inscriptions seront supprimees.")) return;
        int id = (int) activityModel.getValueAt(row, 0);
        String err = controller.deleteActivity(id);
        if (err != null) error(err);
        else { success("Activite supprimee."); refreshAll(); }
    }

    private void showActivityDialog(Activity existing) {
        boolean isEdit = existing != null;
        JTextField fName  = new JTextField(isEdit ? existing.getName()        : "");
        JTextField fDesc  = new JTextField(isEdit ? existing.getDescription() : "");
        JTextField fCap   = new JTextField(isEdit ? String.valueOf(existing.getMaxCapacity()) : "");
        JTextField fSched = new JTextField(isEdit ? existing.getSchedule()    : "ex: Lundi 10h-11h");

        JPanel p = new JPanel(new GridLayout(4, 2, 8, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(new JLabel("Nom *:")); p.add(fName);
        p.add(new JLabel("Description *:")); p.add(fDesc);
        p.add(new JLabel("Capacite max *:")); p.add(fCap);
        p.add(new JLabel("Horaires *:")); p.add(fSched);

        int res = JOptionPane.showConfirmDialog(this, p,
                isEdit ? "Modifier l'activite" : "Ajouter une activite",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String err = isEdit
                ? controller.updateActivity(existing.getId(),
                    fName.getText(), fDesc.getText(), fCap.getText(), fSched.getText())
                : controller.addActivity(
                    fName.getText(), fDesc.getText(), fCap.getText(), fSched.getText());

        if (err != null) { error(err); showActivityDialog(existing); }
        else { success(isEdit ? "Activite modifiee." : "Activite ajoutee."); refreshAll(); }
    }

    // ── REGISTRATIONS TAB ────────────────────────────

    private JPanel buildRegistrationsTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID","Membre","Activite","Date","Statut"};
        regModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        regTable = new JTable(regModel);
        regTable.setRowHeight(24);
        regTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        regTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        regTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        JButton btnAccept  = styledBtn("Accepter",   new Color(39, 174, 96));
        JButton btnRefuse  = styledBtn("Refuser",    new Color(192, 57, 43));
        JButton btnDelete  = styledBtn("Supprimer",  Color.DARK_GRAY);
        JButton btnRefresh = styledBtn("Rafraichir", Color.GRAY);

        btnAccept.addActionListener(e  -> validateSelectedReg(true));
        btnRefuse.addActionListener(e  -> validateSelectedReg(false));
        btnDelete.addActionListener(e  -> deleteSelectedReg());
        btnRefresh.addActionListener(e -> refreshAll());

        panel.add(buttonBar(btnAccept, btnRefuse, btnDelete, btnRefresh), BorderLayout.NORTH);
        panel.add(new JScrollPane(regTable), BorderLayout.CENTER);
        return panel;
    }

    private void validateSelectedReg(boolean accept) {
        int row = regTable.getSelectedRow();
        if (row < 0) { warn("Veuillez selectionner une inscription."); return; }
        int id = (int) regModel.getValueAt(row, 0);
        String membre   = (String) regModel.getValueAt(row, 1);
        String activite = (String) regModel.getValueAt(row, 2);
        String action = accept ? "accepter" : "refuser";
        if (!confirm("Voulez-vous " + action + " l'inscription de\n\"" + membre + "\" a \"" + activite + "\" ?")) return;
        String err = controller.validateRegistration(id, accept);
        if (err != null) error(err);
        else { success("Inscription " + (accept ? "acceptee." : "refusee.")); refreshAll(); }
    }

    private void deleteSelectedReg() {
        int row = regTable.getSelectedRow();
        if (row < 0) { warn("Veuillez selectionner une inscription."); return; }
        if (!confirm("Supprimer definitivement cette inscription ?")) return;
        int id = (int) regModel.getValueAt(row, 0);
        String err = controller.deleteRegistration(id);
        if (err != null) error(err);
        else { success("Inscription supprimee."); refreshAll(); }
    }

    // ── STATS TAB ────────────────────────────────────

    private JPanel buildStatsTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        statsArea.setMargin(new Insets(10, 14, 10, 14));
        JButton btnRefresh = styledBtn("Actualiser les statistiques", new Color(41, 128, 185));
        btnRefresh.addActionListener(e -> refreshStats());
        panel.add(btnRefresh, BorderLayout.NORTH);
        panel.add(new JScrollPane(statsArea), BorderLayout.CENTER);
        return panel;
    }

    // ── REFRESH ──────────────────────────────────────

    private void refreshAll() {
        refreshMembers();
        refreshActivities();
        refreshRegistrations();
        refreshStats();
    }

    private void refreshMembers() {
        memberModel.setRowCount(0);
        for (Member m : controller.getAllMembers()) {
            memberModel.addRow(new Object[]{
                m.getId(), m.getLogin(), m.getFirstName(), m.getLastName(),
                m.getBirthDate(), m.getAddress(), m.getPhone(), m.getEmail(), m.getWeight()
            });
        }
    }

    private void refreshActivities() {
        activityModel.setRowCount(0);
        for (Activity a : controller.getAllActivities()) {
            int participants = controller.getParticipantCount(a.getId());
            activityModel.addRow(new Object[]{
                a.getId(), a.getName(), a.getDescription(),
                a.getMaxCapacity(), participants,
                a.getRemainingPlaces(), a.getSchedule(),
                a.isFull() ? "COMPLET" : "Disponible"
            });
        }
    }

    private void refreshRegistrations() {
        regModel.setRowCount(0);
        for (Registration r : controller.getAllRegistrations()) {
            Member   m = controller.getMemberById(r.getMemberId());
            Activity a = controller.getActivityById(r.getActivityId());
            regModel.addRow(new Object[]{
                r.getId(),
                m != null ? m.getFullName() : "Inconnu",
                a != null ? a.getName()     : "Inconnue",
                r.getRegistrationDate(),
                r.getStatusLabel()
            });
        }
    }

    private void refreshStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("===========================================\n");
        sb.append("        STATISTIQUES DU CLUB\n");
        sb.append("===========================================\n\n");
        sb.append(String.format("  Membres         : %d\n", controller.getAllMembers().size()));
        sb.append(String.format("  Activites       : %d\n", controller.getAllActivities().size()));
        sb.append(String.format("  Inscriptions    : %d\n\n", controller.getAllRegistrations().size()));

        Activity popular = controller.getMostPopularActivity();
        sb.append("-- Activite la plus populaire ----------\n");
        if (popular != null)
            sb.append(String.format("  %s  (%d participant(s))\n\n",
                    popular.getName(), controller.getParticipantCount(popular.getId())));
        else sb.append("  Aucune donnee.\n\n");

        sb.append("-- Participants par activite ------------\n");
        for (Activity a : controller.getAllActivities()) {
            int p = controller.getParticipantCount(a.getId());
            sb.append(String.format("  %-20s : %d\n", a.getName(), p));
        }

        sb.append("\n-- Activites completes -----------------\n");
        List<Activity> full = controller.getFullActivities();
        if (full.isEmpty()) sb.append("  Aucune.\n");
        else full.forEach(a -> sb.append("  [COMPLET] ").append(a.getName()).append("\n"));

        sb.append("\n-- Top 5 membres les plus actifs -------\n");
        int rank = 1;
        for (Member m : controller.getMostActiveMembers()) {
            long count = controller.getMemberActivityCount(m.getId());
            sb.append(String.format("  %d. %-25s %d activite(s)\n", rank++, m.getFullName(), count));
            if (rank > 5) break;
        }

        statsArea.setText(sb.toString());
        statsArea.setCaretPosition(0);
    }

    // ── HELPERS ──────────────────────────────────────

    private JButton styledBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel buttonBar(JButton... buttons) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        for (JButton b : buttons) p.add(b);
        return p;
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