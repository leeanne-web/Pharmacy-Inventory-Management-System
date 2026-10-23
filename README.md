# Pharmacy Inventory Management System (PIMS)

## HealthFirst Pharmacy

**Author:** Lee-Anne Moloi  
**Student Number:** 402312507  
**Course:** Programming 632

## 1. Introduction

The Pharmacy Inventory Management System (PIMS) is a Java desktop application developed for HealthFirst Pharmacy. It helps manage medicine stock, process sales and view business reports.

The application uses Java Swing for the interface, JDBC for database connectivity and MySQL for storing data. It has two user roles: Administrator and Cashier.

## 2. Features

| Module | Description |
|---|---|
| Login | Users log in with their username and password. The system opens the appropriate screen based on their role. |
| Cashier | Search medicines, add items to a cart, process sales, print or save receipts, and check stock. |
| Administrator | Manage medicines, suppliers and cashier accounts, and view reports. |

### Reports

Administrators can view:

1. Daily sales for the last selected number of days.
2. Sales by cashier.
3. Top 10 selling medicines.
4. Medicines at or below their reorder level.
5. Medicines expiring within a selected number of days.
6. Already expired medicines.
7. Inventory value by medicine type.

## 3. Technologies Used

- Java 17 or higher
- Java Swing and AWT
- JDBC
- MySQL 8
- MySQL Connector/J 8
- Visual Studio Code
- XAMPP for running the MySQL service

## 4. Project Structure

```text
PIMS/
├── lib/
│   └── mysql-connector-j-8.4.0.jar
├── src/
│   ├── Main.java
│   ├── LoginFrame.java
│   ├── AppFrame.java
│   ├── DB.java
│   ├── Session.java
│   ├── Refreshable.java
│   ├── MedicinePanel.java
│   ├── SupplierPanel.java
│   ├── UserPanel.java
│   ├── ReportsPanel.java
│   ├── PosPanel.java
│   └── StockCheckPanel.java
├── manifest.txt
└── schema.sql
```

The `src` folder contains the Java classes for the login screen, main application window, database connection, medicine and supplier management, cashier accounts, reports, sales and stock checking.

## 5. Database Design

The system uses a MySQL database called `pims`, with five tables:

- `users` – stores login details and user roles.
- `suppliers` – stores supplier information.
- `medicines` – stores medicine details, prices, quantities and expiry dates.
- `sales` – stores completed sales.
- `sale_items` – stores the medicines included in each sale.

Medicines are linked to suppliers, sales are linked to cashiers, and each sale can contain multiple medicines.

The `price_at_sale` field stores the medicine price at the time of purchase, so changes to prices do not affect previous sales. Medicines and cashiers with sales history cannot be permanently deleted, helping preserve sales records.

## 6. How to Install and Run the System

### Requirements

- JDK 17 or higher.
- Visual Studio Code with the Extension Pack for Java.
- XAMPP with MySQL enabled.
- MySQL Connector/J in the project's `lib` folder.

### Step 1: Set up the database

1. Open XAMPP and start MySQL.
2. Open phpMyAdmin in your browser.
3. Import `schema.sql` to create the database and tables.

### Step 2: Configure the connection

Open `src/DB.java` and update the MySQL username and password to match your setup.

### Step 3: Run the application

**From Visual Studio Code:** Open the PIMS project folder, open `Main.java` and click **Run**.

**From the packaged application:** Start MySQL, then open the packaged PIMS folder and double-click `PIMS.exe`.

The `pims` database must already exist on the computer running the application.

## 7. Packaging the Application (Optional)

The application was packaged into a runnable JAR and a Windows application using `jpackage`.

The packaged application includes a Java runtime, so Java does not need to be installed separately on the target computer. MySQL must still be installed and running, and the `pims` database must be created.

Keep the complete PIMS application folder together when sharing or submitting it.

## 8. Default Login

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | Admin |

Cashier accounts can be created from the Users tab after logging in as an administrator.

**Important:** Change the default administrator password before using the system with real pharmacy information.

## 9. How to Use the System

### Administrator

1. Log in using the administrator account.
2. Add suppliers under the Suppliers tab.
3. Add medicines under the Medicines tab, including their prices, quantities and expiry dates.
4. Create cashier accounts under Users.
5. View sales and stock reports under Reports.

Medicine expiry dates must use the `yyyy-mm-dd` format.

### Cashier

1. Log in using a cashier account.
2. Open Point of Sale and search for a medicine.
3. Select the medicine, enter the quantity and add it to the cart.
4. Click Checkout & Print Bill to complete the sale.
5. Print the receipt or save it as a text file.

The Stock Check tab can be used to check medicine prices and availability without processing a sale.

## 10. Security and Important Features

- Passwords are hashed using SHA-256.
- Prepared statements are used to help prevent SQL injection.
- Sales are processed using database transactions, allowing a rollback if checkout fails.
- Stock is checked before completing a sale to prevent overselling.
- Expired and out-of-stock medicines are hidden from the Point of Sale list.
- Cashiers only have access to cashier features, while administrators can access management features.

## 11. Known Limitations

- Passwords use unsalted SHA-256. BCrypt or PBKDF2 would provide stronger password protection.
- Database credentials are stored in `DB.java`.
- SQL queries are written directly in the GUI classes instead of using a separate database layer.
- Reports cannot currently be exported.
- Report date filtering is limited to the last selected number of days.
- Some input validation is basic, such as checking for invalid medicine prices or quantities.

## 12. Troubleshooting

| Problem | Possible Solution |
|---|---|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Check that the MySQL Connector JAR is in the `lib` folder. |
| `Access denied for user 'root'` | Check the database username and password in `DB.java`. |
| `Communications link failure` | Make sure MySQL is running. |
| `Unknown database 'pims'` | Import `schema.sql` to create the database. |
| `PIMS.exe` fails to open | Check that MySQL is running and the database connection details are correct. |

## 13. Conclusion

PIMS was developed as a university programming project to apply Java GUI development, database connectivity and SQL concepts.

The system provides basic features for managing pharmacy stock, processing sales and viewing reports. It also demonstrates how user roles can control access to different parts of an application.
