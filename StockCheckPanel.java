import javax.swing.*;
import java.awt.*;

public class StockCheckPanel extends JPanel implements Refreshable {
    JTextField search = new JTextField(20);
    JTable table = new JTable();

    public StockCheckPanel() {
        setLayout(new BorderLayout());
        JButton go = new JButton("Check");
        go.addActionListener(e -> refresh());
        search.addActionListener(e -> refresh());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Medicine name / company:")); top.add(search); top.add(go);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void refresh() {
        String k = "%" + search.getText().trim() + "%";
        DB.load(this, table,
            "SELECT name, company, medicine_type AS type, price, quantity_in_stock AS in_stock, " +
            "expiry_date, IF(expiry_date < CURDATE(), 'EXPIRED', IF(quantity_in_stock > 0, 'Available', 'Out of stock')) AS status " +
            "FROM medicines WHERE name LIKE ? OR company LIKE ? ORDER BY name", k, k);
    }
}
