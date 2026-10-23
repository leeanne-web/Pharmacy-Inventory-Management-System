import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Arrays;

public class MedicinePanel extends JPanel implements Refreshable {
    JTable table = new JTable();
    JTextField search = new JTextField(20);
    JTextField name = new JTextField(), company = new JTextField(), price = new JTextField(),
               qty = new JTextField(), reorder = new JTextField(), expiry = new JTextField();
    JComboBox<String> type = new JComboBox<>(new String[]{"Tablet", "Capsule", "Syrup", "Injection", "Cream"});
    JComboBox<String> supplier = new JComboBox<>();
    int selectedId = -1;

    public MedicinePanel() {
        setLayout(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton go = new JButton("Search");
        go.addActionListener(e -> loadTable());
        top.add(new JLabel("Name/Company:")); top.add(search); top.add(go);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) fillForm(); });

        JButton add = new JButton("Add"), upd = new JButton("Update"),
                del = new JButton("Delete"), clr = new JButton("Clear");
        add.addActionListener(e -> save(true));
        upd.addActionListener(e -> save(false));
        del.addActionListener(e -> delete());
        clr.addActionListener(e -> clear());

        JPanel form = DB.form("Name", name, "Company", company, "Type", type, "Price (R)", price,
                "Quantity", qty, "Reorder level", reorder, "Expiry (yyyy-mm-dd)", expiry, "Supplier", supplier);
        JPanel btns = new JPanel(); btns.add(add); btns.add(upd); btns.add(del); btns.add(clr);
        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER); south.add(btns, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    public void refresh() { loadSuppliers(); loadTable(); }

    void loadSuppliers() {
        supplier.removeAllItems();
        supplier.addItem("0 - (none)");
        try {
            TableModel m = DB.query("SELECT supplier_id, name FROM suppliers ORDER BY name");
            for (int i = 0; i < m.getRowCount(); i++) supplier.addItem(m.getValueAt(i, 0) + " - " + m.getValueAt(i, 1));
        } catch (Exception e) { DB.err(this, e); }
    }

    void loadTable() {
        String k = "%" + search.getText().trim() + "%";
        DB.load(this, table,
            "SELECT m.medicine_id, m.name, m.company, m.medicine_type, m.price, m.quantity_in_stock, " +
            "m.reorder_level, m.expiry_date, m.supplier_id, s.name AS supplier " +
            "FROM medicines m LEFT JOIN suppliers s ON s.supplier_id = m.supplier_id " +
            "WHERE m.name LIKE ? OR m.company LIKE ? ORDER BY m.name", k, k);
        if (table.getColumnCount() > 8) table.removeColumn(table.getColumnModel().getColumn(8)); // hide supplier_id
    }

    void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        TableModel m = table.getModel();
        selectedId = (int) m.getValueAt(r, 0);
        name.setText(String.valueOf(m.getValueAt(r, 1)));
        company.setText(String.valueOf(m.getValueAt(r, 2)));
        type.setSelectedItem(m.getValueAt(r, 3));
        price.setText(String.valueOf(m.getValueAt(r, 4)));
        qty.setText(String.valueOf(m.getValueAt(r, 5)));
        reorder.setText(String.valueOf(m.getValueAt(r, 6)));
        expiry.setText(String.valueOf(m.getValueAt(r, 7)));
        Object sid = m.getValueAt(r, 8);
        supplier.setSelectedIndex(0);
        for (int i = 0; i < supplier.getItemCount(); i++)
            if (sid != null && supplier.getItemAt(i).startsWith(sid + " - ")) supplier.setSelectedIndex(i);
    }

    Integer supplierId() {
        int id = Integer.parseInt(((String) supplier.getSelectedItem()).split(" - ")[0]);
        return id == 0 ? null : id;
    }

    void save(boolean adding) {
        try {
            if (name.getText().isBlank()) throw new Exception("Name is required.");
            Object[] p = {name.getText().trim(), company.getText().trim(), type.getSelectedItem(),
                    new BigDecimal(price.getText().trim()), Integer.parseInt(qty.getText().trim()),
                    Integer.parseInt(reorder.getText().trim()), java.sql.Date.valueOf(expiry.getText().trim()),
                    supplierId()};
            if (adding) {
                DB.update("INSERT INTO medicines(name,company,medicine_type,price,quantity_in_stock," +
                          "reorder_level,expiry_date,supplier_id) VALUES(?,?,?,?,?,?,?,?)", p);
            } else {
                if (selectedId < 0) throw new Exception("Select a medicine first.");
                Object[] q = Arrays.copyOf(p, 9); q[8] = selectedId;
                DB.update("UPDATE medicines SET name=?,company=?,medicine_type=?,price=?,quantity_in_stock=?," +
                          "reorder_level=?,expiry_date=?,supplier_id=? WHERE medicine_id=?", q);
            }
            clear(); loadTable();
        } catch (Exception e) { DB.err(this, e); }
    }

    void delete() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Select a medicine first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this medicine?", "Confirm", JOptionPane.YES_NO_OPTION) != 0) return;
        try {
            DB.update("DELETE FROM medicines WHERE medicine_id=?", selectedId);
            clear(); loadTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cannot delete: this medicine appears in past sales.\n" +
                    "Set its quantity to 0 instead.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void clear() {
        selectedId = -1;
        for (JTextField f : new JTextField[]{name, company, price, qty, reorder, expiry}) f.setText("");
        type.setSelectedIndex(0); supplier.setSelectedIndex(0); table.clearSelection();
    }
}
