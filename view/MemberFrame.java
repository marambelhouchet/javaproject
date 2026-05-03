package projetjava.view;

import java.awt.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class MemberFrame extends JFrame {

    private JList<String> list;
    private Object controller;
    private String user;

    public MemberFrame(String user) {
        this.user = user;
        controller = createController();

        setTitle("Member");
        setSize(400,300);

        list = new JList<>(getActivities().toArray(new String[0]));

        JButton btn = new JButton("Register");

        setLayout(new BorderLayout());
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(btn, BorderLayout.SOUTH);

        btn.addActionListener(e -> register());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void register() {
        String selected = list.getSelectedValue();
        if(selected != null) {
            try {
                Files.writeString(
                        Path.of("registrations.txt"),
                        user + "," + selected + System.lineSeparator(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
                JOptionPane.showMessageDialog(this,"Registered!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,"Registration failed!");
            }
        }
    }

    private Object createController() {
        try {
            return Class.forName("projetjava.controller.ActivityController")
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> getActivities() {
        if (controller == null) {
            return Collections.emptyList();
        }

        try {
            Method method = controller.getClass().getMethod("getActivities");
            Object result = method.invoke(controller);
            if (result instanceof List) {
                return (List<String>) result;
            }
        } catch (Exception e) {
            // fall through
        }

        return Collections.emptyList();
    }
}