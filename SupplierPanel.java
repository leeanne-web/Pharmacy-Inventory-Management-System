import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

public class SupplierPanel extends JPanel implements Refreshable {
    JTable table = new JTable();
    JTextField name = new JTextField(), contact = new JTextField(), phone = new JTextField(),
               email = new JTextField(), address = new JTextField();
    int selectedId = -1;

    public SupplierPanel() {
        setLayout(new BorderLayout());
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) fillForm(); });
        JButton add = new JButton("Add"), upd = new JButton("Update"),
                del = new JButton("Delete"), clr = new JButton("Clear");
        add.addActionListener(e -> save(true));
        upd.addActionListener(e -> save(false));
        del.addActionListener(e -> delete());
        clr.addActionListener(e -> clear());
        JPanel form = DB.form("Name", name, "Contact person", contact, "Phone", phone, "Email", email,
                "Address", address, "", new JLabel(), "", new JLabel());
        JPanel btns = new JPanel(); btns.add(add); btns.add(upd); btns.add(del); btns.add(clr);
        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER); south.add(btns, BorderLayout.SOUTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    public void refresh() {
        DB.load(this, table, "SELECT * FROM suppliers ORDER BY name");
    }

    void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        TableModel m = table.getModel();
        selectedId = (int) m.getValueAt(r, 0);
        JTextField[] f = {name, contact, phone, email, address};
        for (int i = 0; i < f.length; i++) {
            Object v = m.getValueAt(r, i + 1);
            f[i].setText(v == null ? "" : v.toString());
        }
    }

    void save(boolean adding) {
        try {
            if (name.getText().isBlank()) throw new Exception("Supplier name is required.");
            Object[] p = {name.getText().trim(), contact.getText().trim(), phone.getText().trim(),
                          email.getText().trim(), address.getText().trim()};
            if (adding) DB.update("INSERT INTO suppliers(name,contact_person,phone,email,address) VALUES(?,?,?,?,?)", p);
            else {
                if (selectedId < 0) throw new Exception("Select a supplier first.");
                DB.update("UPDATE suppliers SET name=?,contact_person=?,phone=?,email=?,address=? WHERE supplier_id=?",
                          p[0], p[1], p[2], p[3], p[4], selectedId);
            }
            clear(); refresh();
        } catch (Exception e) { DB.err(this, e); }
    }

    void delete() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Select a supplier first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this supplier?", "Confirm", JOptionPane.YES_NO_OPTION) != 0) return;
        try { DB.update("DELETE FROM suppliers WHERE supplier_id=?", selectedId); clear(); refresh(); }
        catch (Exception e) { DB.err(this, e); }
    }

    void clear() {
        selectedId = -1;
        for (JTextField f : new JTextField[]{name, contact, phone, email, address}) f.setText("");
        table.clearSelection();
    }
}
