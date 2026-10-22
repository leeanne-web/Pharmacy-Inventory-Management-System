import javax.swing.*;
import java.awt.*;

public class ReportsPanel extends JPanel implements Refreshable {
    static final String[] NAMES = {
        "Daily sales (last N days)", "Sales by cashier (last N days)", "Top 10 selling medicines (last N days)",
        "Low stock (at/below reorder level)", "Expiring within N days", "Already expired stock", "Inventory value by type"};

    static final String SINCE = "s.sale_date >= DATE_SUB(NOW(), INTERVAL ? DAY)";
    static final String[] SQL = {
        "SELECT DATE(s.sale_date) AS day, COUNT(*) AS transactions, SUM(s.total_amount) AS revenue " +
            "FROM sales s WHERE " + SINCE + " GROUP BY DATE(s.sale_date) ORDER BY day DESC",
        "SELECT u.full_name AS cashier, COUNT(*) AS transactions, SUM(s.total_amount) AS revenue " +
            "FROM sales s JOIN users u ON u.user_id = s.user_id WHERE " + SINCE + " GROUP BY u.user_id ORDER BY revenue DESC",
        "SELECT m.name, SUM(si.quantity_sold) AS units_sold, SUM(si.quantity_sold*si.price_at_sale) AS revenue " +
            "FROM sale_items si JOIN sales s ON s.sale_id = si.sale_id JOIN medicines m ON m.medicine_id = si.medicine_id " +
            "WHERE " + SINCE + " GROUP BY m.medicine_id ORDER BY units_sold DESC LIMIT 10",
        "SELECT m.name, m.quantity_in_stock, m.reorder_level, s.name AS supplier, s.phone " +
            "FROM medicines m LEFT JOIN suppliers s ON s.supplier_id = m.supplier_id " +
            "WHERE m.quantity_in_stock <= m.reorder_level ORDER BY m.quantity_in_stock",
        "SELECT name, quantity_in_stock, expiry_date, DATEDIFF(expiry_date, CURDATE()) AS days_left FROM medicines " +
            "WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) ORDER BY expiry_date",
        "SELECT name, quantity_in_stock, expiry_date FROM medicines WHERE expiry_date < CURDATE() ORDER BY expiry_date",
        "SELECT medicine_type, COUNT(*) AS products, SUM(quantity_in_stock) AS units, " +
            "SUM(price*quantity_in_stock) AS stock_value FROM medicines GROUP BY medicine_type"};
    static final boolean[] NEEDS_DAYS = {true, true, true, false, true, false, false};

    JComboBox<String> report = new JComboBox<>(NAMES);
    JSpinner days = new JSpinner(new SpinnerNumberModel(30, 1, 3650, 1));
    JTable table = new JTable();
    JLabel summary = new JLabel(" ");

    public ReportsPanel() {
        setLayout(new BorderLayout());
        JButton run = new JButton("Run report");
        run.addActionListener(e -> run());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Report:")); top.add(report);
        top.add(new JLabel("N days:")); top.add(days); top.add(run);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(summary, BorderLayout.SOUTH);
    }

    public void refresh() { run(); }

    void run() {
        int i = report.getSelectedIndex();
        if (NEEDS_DAYS[i]) DB.load(this, table, SQL[i], (int) days.getValue());
        else DB.load(this, table, SQL[i]);
        summary.setText("  " + table.getRowCount() + " row(s)");
    }
}
