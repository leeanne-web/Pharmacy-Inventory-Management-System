import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    JTextField user = new JTextField(15);
    JPasswordField pass = new JPasswordField(15);

    public LoginFrame() {
        super("HealthFirst Pharmacy - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        p.add(new JLabel("Username:")); p.add(user);
        p.add(new JLabel("Password:")); p.add(pass);
        JButton login = new JButton("Login");
        login.addActionListener(e -> doLogin());
        getRootPane().setDefaultButton(login);
        JLabel title = new JLabel("PIMS - HealthFirst", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);
        add(p, BorderLayout.CENTER);
        JPanel s = new JPanel(); s.add(login);
        add(s, BorderLayout.SOUTH);
        pack(); setLocationRelativeTo(null); setVisible(true);
    }

    void doLogin() {
        String sql = "SELECT user_id, role, full_name FROM users WHERE username=? AND password=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getText().trim());
            ps.setString(2, DB.hash(new String(pass.getPassword())));
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Session.userId = rs.getInt(1);
            Session.role = rs.getString(2);
            Session.fullName = rs.getString(3);
            dispose();
            if (Session.role.equals("Admin")) {
                new AppFrame("PIMS - Administrator",
                    new String[]{"Medicines", "Suppliers", "Users", "Reports", "Stock Check"},
                    new JPanel[]{new MedicinePanel(), new SupplierPanel(), new UserPanel(), new ReportsPanel(), new StockCheckPanel()});
            } else {
                new AppFrame("PIMS - Cashier",
                    new String[]{"Point of Sale", "Stock Check"},
                    new JPanel[]{new PosPanel(), new StockCheckPanel()});
            }
        } catch (SQLException ex) { DB.err(this, ex); }
    }
}
