import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {
    public AppFrame(String title, String[] names, JPanel[] panels) {
        super(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 680); setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        for (int i = 0; i < names.length; i++) tabs.addTab(names[i], panels[i]);
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() instanceof Refreshable r) r.refresh();
        });

        JButton out = new JButton("Logout");
        out.addActionListener(e -> { dispose(); new LoginFrame(); });
        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("  Logged in: " + Session.fullName + " (" + Session.role + ")"), BorderLayout.WEST);
        top.add(out, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        setVisible(true);
        if (panels[0] instanceof Refreshable r) r.refresh();
    }
}
