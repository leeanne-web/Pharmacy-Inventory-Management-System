import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Vector;

public class DB {
    static final String URL  = "jdbc:mysql://localhost:3306/pims";
    static final String USER = "root";
    static final String PASS = ""; // change to your MySQL password

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static String hash(String s) {
        try {
            byte[] b = MessageDigest.getInstance("SHA-256").digest(s.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte x : b) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public static DefaultTableModel query(String sql, Object... p) throws SQLException {
        try (Connection c = get(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) ps.setObject(i + 1, p[i]);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int n = md.getColumnCount();
            Vector<String> cols = new Vector<>();
            for (int i = 1; i <= n; i++) cols.add(md.getColumnLabel(i));
            DefaultTableModel m = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= n; i++) row.add(rs.getObject(i));
                m.addRow(row);
            }
            return m;
        }
    }

    public static int update(String sql, Object... p) throws SQLException {
        try (Connection c = get(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) ps.setObject(i + 1, p[i]);
            return ps.executeUpdate();
        }
    }

    public static void load(Component parent, JTable t, String sql, Object... p) {
        try { t.setModel(query(sql, p)); } catch (Exception e) { err(parent, e); }
    }

    public static void err(Component c, Exception e) {
        String msg = e instanceof NumberFormatException ? "Please check the numeric fields."
                : e instanceof IllegalArgumentException ? "Invalid input (dates must be yyyy-mm-dd)."
                : e.getMessage();
        JOptionPane.showMessageDialog(c, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Builds a 4-column grid: label, field, label, field ... */
    public static JPanel form(Object... items) {
        JPanel p = new JPanel(new GridLayout(0, 4, 6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        for (Object o : items) p.add(o instanceof String s ? new JLabel(s) : (Component) o);
        return p;
    }
}
