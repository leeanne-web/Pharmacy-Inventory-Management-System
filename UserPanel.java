import javax.swing.*;
import java.awt.*;

public class UserPanel extends JPanel implements Refreshable {
    JTable table = new JTable();
    JTextField username = new JTextField(), fullName = new JTextField();
    JPasswordField password = new JPasswordField();
    int selectedId = -1;

    public UserPanel() {
        setLayout(new BorderLayout());
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) fillForm(); });
        JButton add = new JButton("Create Cashier"), upd = new JButton("Update (name / reset password)"),
                del = new JButton("Delete"), clr = new JButton("Clear");
        add.addActionListener(e -> create());
        upd.addActionListener(e -> update());
        del.addActionListener(e -> delete());
        clr.addActionListener(e -> clear());
        JPanel form = DB.form("Username", username, "Full name", fullName,
                "Password (blank = keep)", password, "", new JLabel());
        JPanel btns = new JPanel(); btns.add(add); btns.add(upd); btns.add(del); btns.add(clr);
        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER); south.add(btns, BorderLayout.SOUTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    public void refresh() {
        DB.load(this, table, "SELECT user_id, username, full_name, role FROM users WHERE role='Cashier' ORDER BY username");
    }

    void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        selectedId = (int) table.getValueAt(r, 0);
        username.setText(table.getValueAt(r, 1).toString());
        fullName.setText(table.getValueAt(r, 2).toString());
        password.setText("");
    }

    void create() {
        try {
            String pw = new String(password.getPassword());
            if (username.getText().isBlank() || fullName.getText().isBlank() || pw.length() < 6)
                throw new Exception("Username, full name and a password of 6+ characters are required.");
            DB.update("INSERT INTO users(username,password,role,full_name) VALUES(?,?,'Cashier',?)",
                      username.getText().trim(), DB.hash(pw), fullName.getText().trim());
            clear(); refresh();
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "That username already exists.");
        } catch (Exception e) { DB.err(this, e); }
    }

    void update() {
        try {
            if (selectedId < 0) throw new Exception("Select a cashier first.");
            String pw = new String(password.getPassword());
            DB.update("UPDATE users SET full_name=? WHERE user_id=? AND role='Cashier'", fullName.getText().trim(), selectedId);
            if (!pw.isEmpty()) {
                if (pw.length() < 6) throw new Exception("Password must be 6+ characters.");
                DB.update("UPDATE users SET password=? WHERE user_id=? AND role='Cashier'", DB.hash(pw), selectedId);
            }
            clear(); refresh();
        } catch (Exception e) { DB.err(this, e); }
    }

    void delete() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Select a cashier first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this cashier?", "Confirm", JOptionPane.YES_NO_OPTION) != 0) return;
        try {
            DB.update("DELETE FROM users WHERE user_id=? AND role='Cashier'", selectedId);
            clear(); refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cannot delete: this cashier has processed sales (kept for audit records).");
        }
    }

    void clear() {
        selectedId = -1; username.setText(""); fullName.setText(""); password.setText(""); table.clearSelection();
    }
}
