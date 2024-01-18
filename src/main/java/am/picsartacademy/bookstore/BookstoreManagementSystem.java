package am.picsartacademy.bookstore;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BookstoreManagementSystem {
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/BookstoreDB";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            BookstoreManagementSystem system = new BookstoreManagementSystem(connection);
            system.setupDatabase();
            system.startCLI();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private final Connection connection;

    public BookstoreManagementSystem(Connection connection) {
        this.connection = connection;
    }

    private void setupDatabase() {
        try (Statement statement = connection.createStatement()) {
            // Drop existing trigger if it exists
            statement.executeUpdate("DROP TRIGGER IF EXISTS update_books_quantity ON sales");

            // Create tables
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Books ("
                    + "BookID SERIAL PRIMARY KEY,"
                    + "Title TEXT NOT NULL,"
                    + "Author VARCHAR(40) NOT NULL,"
                    + "Genre VARCHAR(30) NOT NULL,"
                    + "Price REAL NOT NULL CHECK(Price > 0),"
                    + "QuantityInStock INTEGER NOT NULL CHECK(QuantityInStock >= 0)"
                    + ")");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Customers ("
                    + "CustomerID SERIAL PRIMARY KEY,"
                    + "Name VARCHAR(20) NOT NULL,"
                    + "Email VARCHAR(60) UNIQUE,"
                    + "Phone VARCHAR(20) NOT NULL"
                    + ")");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Sales ("
                    + "SaleID SERIAL PRIMARY KEY,"
                    + "BookID INTEGER,"
                    + "CustomerID INTEGER,"
                    + "DateOfSale DATE,"
                    + "QuantitySold INTEGER NOT NULL CHECK(QuantitySold >= 0),"
                    + "TotalPrice REAL NOT NULL CHECK(TotalPrice >= 0),"
                    + "CONSTRAINT fk_book FOREIGN KEY (BookID) REFERENCES Books(BookID) ON DELETE SET NULL,"
                    + "CONSTRAINT fk_customer FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE SET NULL"
                    + ")");

            // Create trigger
            statement.executeUpdate("CREATE OR REPLACE FUNCTION update_books_quantity_in_stock()"
                    + " RETURNS TRIGGER AS $$"
                    + " BEGIN"
                    + " UPDATE Books"
                    + " SET QuantityInStock = QuantityInStock - NEW.QuantitySold"
                    + " WHERE BookID = NEW.BookID;"
                    + " RETURN NEW;"
                    + " END;"
                    + " $$ LANGUAGE plpgsql;");

            statement.executeUpdate("CREATE TRIGGER update_books_quantity"
                    + " AFTER INSERT ON Sales"
                    + " FOR EACH ROW"
                    + " EXECUTE FUNCTION update_books_quantity_in_stock()");


            // Add 10 world-famous books
            addWorldFamousBooks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addWorldFamousBooks() {
// Insert 10 world-famous books if the Books table is empty
        String checkQuery = "SELECT COUNT(*) FROM Books";
        String insertQuery = "INSERT INTO Books (Title, Author, Genre, Price, QuantityInStock) VALUES (?, ?, ?, ?, ?)";

        try (Statement statement = connection.createStatement();
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            try (ResultSet resultSet = statement.executeQuery(checkQuery)) {
                resultSet.next();
                int bookCount = resultSet.getInt(1);

                if (bookCount == 0) {
                    // Insert 10 world-famous books
                    insertStatement.setString(1, "To Kill a Mockingbird");
                    insertStatement.setString(2, "Harper Lee");
                    insertStatement.setString(3, "Fiction");
                    insertStatement.setDouble(4, 15.99);
                    insertStatement.setInt(5, 50);
                    insertStatement.executeUpdate();

                    insertStatement.setString(1, "1984");
                    insertStatement.setString(2, "George Orwell");
                    insertStatement.setString(3, "Dystopian Fiction");
                    insertStatement.setDouble(4, 12.99);
                    insertStatement.setInt(5, 40);
                    insertStatement.executeUpdate();

                    insertStatement.setString(1, "The Great Gatsby");
                    insertStatement.setString(2, "F. Scott Fitzgerald");
                    insertStatement.setString(3, "Classic");
                    insertStatement.setDouble(4, 17.99);
                    insertStatement.setInt(5, 30);
                    insertStatement.executeUpdate();

                    insertStatement.setString(1, "One Hundred Years of Solitude");
                    insertStatement.setString(2, "Gabriel Garcia Marquez");
                    insertStatement.setString(3, "Magical Realism");
                    insertStatement.setDouble(4, 14.99);
                    insertStatement.setInt(5, 45);
                    insertStatement.executeUpdate();

                    insertStatement.setString(1, "Pride and Prejudice");
                    insertStatement.setString(2, "Jane Austen");
                    insertStatement.setString(3, "Romance");
                    insertStatement.setDouble(4, 11.99);
                    insertStatement.setInt(5, 55);
                    insertStatement.executeUpdate();

                    insertStatement.setString(1, "The Catcher in the Rye");
                    insertStatement.setString(2, "J.D. Salinger");
                    insertStatement.setString(3, "Coming-of-Age");
                    insertStatement.setDouble(4, 13.99);
                    insertStatement.setInt(5, 35);
                    insertStatement.executeUpdate();

                    insertStatement.setString(1, "To the Lighthouse");
                    insertStatement.setString(2, "Virginia Woolf");
                    insertStatement.setString(3, "Modernist");
                    insertStatement.setDouble(4, 16.99);
                    insertStatement.setInt(5, 25);
                    insertStatement.executeUpdate();

                    insertStatement.setString(1, "Brave New World");
                    insertStatement.setString(2, "Aldous Huxley");
                    insertStatement.setString(3, "Dystopian Fiction");
                    insertStatement.setDouble(4, 18.99);
                    insertStatement.setInt(5, 20);
                    insertStatement.executeUpdate();

                    insertStatement.setString(1, "The Lord of the Rings");
                    insertStatement.setString(2, "J.R.R. Tolkien");
                    insertStatement.setString(3, "Fantasy");
                    insertStatement.setDouble(4, 24.99);
                    insertStatement.setInt(5, 60);
                    insertStatement.executeUpdate();

                    insertStatement.setString(1, "The Chronicles of Narnia");
                    insertStatement.setString(2, "C.S. Lewis");
                    insertStatement.setString(3, "Fantasy");
                    insertStatement.setDouble(4, 19.99);
                    insertStatement.setInt(5, 50);
                    insertStatement.executeUpdate();

                    System.out.println("Added 10 world-famous books to the Books table.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startCLI() {
        while (true) {
            System.out.println("===== Bookstore Management System =====");
            System.out.println("1. Add Book");
            System.out.println("2. Update Book");
            System.out.println("3. List Books by Genre");
            System.out.println("4. Update Customer");
            System.out.println("5. View Purchase History");
            System.out.println("6. Process Sale");
            System.out.println("7. Calculate Revenue by Genre");
            System.out.println("8. Generate Sale Report");
            System.out.println("9. Generate Revenue Report by Genre");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            int choice = getValidMenuChoice(0, 9);
            scanner.nextLine(); // Consume the newline

            switch (choice) {
                case 1:
                    // Add Book
                    addBook();
                    break;
                case 2:
                    // Update Book
                    updateBook();
                    break;
                case 3:
                    // List Books by Genre
                    listBooksByGenre();
                    break;
                case 4:
                    // Update Customer
                    updateCustomer();
                    break;
                case 5:
                    // View Purchase History
                    viewPurchaseHistory();
                    break;
                case 6:
                    // Process Sale
                    processSale();
                    break;
                case 7:
                    // Calculate Revenue by Genre
                    calculateRevenueByGenre();
                    break;
                case 8:
                    // Generate Sale Report
                    generateSaleReport();
                    break;
                case 9:
                    // Generate Revenue Report by Genre
                    generateRevenueReportByGenre();
                    break;
                case 0:
                    // Exit
                    System.out.println("Exiting Bookstore Management System. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private void addBook() {
        System.out.println("===== Add Book =====");
        try {
            // Collect book details from the user
            System.out.print("Enter Title: ");
            String title = getNonEmptyInput();

            System.out.print("Enter Author: ");
            String author = getNonEmptyInput();

            System.out.print("Enter Genre: ");
            String genre = getNonEmptyInput();

            System.out.print("Enter Price: ");
            double price = getPositiveDoubleInput();

            System.out.print("Enter Quantity In Stock: ");
            int quantityInStock = getNonNegativeIntInput();

            // Insert the book into the database
            String insertQuery = "INSERT INTO Books (Title, Author, Genre, Price, QuantityInStock) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, author);
                preparedStatement.setString(3, genre);
                preparedStatement.setDouble(4, price);
                preparedStatement.setInt(5, quantityInStock);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Book added successfully!");
                } else {
                    System.out.println("Failed to add the book. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to add the book. Please try again.");
        } finally {
            scanner.nextLine(); // Consume the newline
        }
    }

    private void updateBook() {
        System.out.println("===== Update Book =====");
        try {
            // Collect book ID from the user
            System.out.print("Enter Book ID to update: ");
            int bookId = getNonNegativeIntInput();
            scanner.nextLine(); // Consume the newline

            // Retrieve current book details from the database
            String selectQuery = "SELECT * FROM Books WHERE BookID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, bookId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Display current details and prompt for updates
                        System.out.println("Current Book Details:");
                        System.out.println("Title: " + resultSet.getString("Title"));
                        System.out.println("Author: " + resultSet.getString("Author"));
                        System.out.println("Genre: " + resultSet.getString("Genre"));
                        System.out.println("Price: " + resultSet.getDouble("Price"));
                        System.out.println("Quantity In Stock: " + resultSet.getInt("QuantityInStock"));

                        // Collect updated book details from the user
                        String newTitle = getInput("Enter new Title (or press Enter to keep current):", resultSet.getString("Title"));
                        String newAuthor = getInput("Enter new Author (or press Enter to keep current):", resultSet.getString("Author"));
                        String newGenre = getInput("Enter new Genre (or press Enter to keep current):", resultSet.getString("Genre"));
                        System.out.println("Enter new Price (or press Enter to keep current):");
                        double newPrice = getPositiveDoubleInput();
                        System.out.println("Enter new Quantity In Stock (or press Enter to keep current):");
                        int newQuantityInStock = getNonNegativeIntInput();

                        // Update the book details in the database
                        String updateQuery = "UPDATE Books SET Title = ?, Author = ?, Genre = ?, Price = ?, QuantityInStock = ? WHERE BookID = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setString(1, newTitle);
                            updateStatement.setString(2, newAuthor);
                            updateStatement.setString(3, newGenre);
                            updateStatement.setDouble(4, newPrice);
                            updateStatement.setInt(5, newQuantityInStock);
                            updateStatement.setInt(6, bookId);

                            int rowsAffected = updateStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Book updated successfully!");
                            } else {
                                System.out.println("Failed to update the book. Please try again.");
                            }
                        }
                    } else {
                        System.out.println("Book not found with ID: " + bookId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update the book. Please try again.");
        }
    }

    private void listBooksByGenre() {
        System.out.println("===== List Books by Genre =====");
        try {
            // Collect genre from the user
            System.out.print("Enter Genre: ");
            String genre = getNonEmptyInput();

            // Retrieve and display a list of books based on the genre from the database
            String selectQuery = "SELECT * FROM Books WHERE Genre = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, genre);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Books in " + genre + " genre:");
                        do {
                            System.out.println("BookID: " + resultSet.getInt("BookID"));
                            System.out.println("Title: " + resultSet.getString("Title"));
                            System.out.println("Author: " + resultSet.getString("Author"));
                            System.out.println("Price: " + resultSet.getDouble("Price"));
                            System.out.println("Quantity In Stock: " + resultSet.getInt("QuantityInStock"));
                            System.out.println("------------------------");
                        } while (resultSet.next());
                    } else {
                        System.out.println("No books found in the " + genre + " genre.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to list books. Please try again.");
        }
    }

    private void updateCustomer() {
        System.out.println("===== Update Customer =====");
        try {
            // Collect customer ID from the user
            System.out.print("Enter Customer ID to update: ");
            int customerId = getNonNegativeIntInput();
            scanner.nextLine(); // Consume the newline

            // Retrieve current customer details from the database
            String selectQuery = "SELECT * FROM Customers WHERE CustomerID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, customerId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Display current details and prompt for updates
                        System.out.println("Current Customer Details:");
                        System.out.println("Name: " + resultSet.getString("Name"));
                        System.out.println("Email: " + resultSet.getString("Email"));
                        System.out.println("Phone: " + resultSet.getString("Phone"));

                        // Collect updated customer details from the user
                        System.out.print("Enter new Name (or press Enter to keep current): ");
                        String newName = getNonEmptyInput();
                        newName = newName.isEmpty() ? resultSet.getString("Name") : newName;

                        System.out.print("Enter new Email (or press Enter to keep current): ");
                        String newEmail = getNonEmptyInput();
                        newEmail = newEmail.isEmpty() ? resultSet.getString("Email") : newEmail;

                        System.out.print("Enter new Phone (or press Enter to keep current): ");
                        String newPhone = getNonEmptyInput();
                        newPhone = newPhone.isEmpty() ? resultSet.getString("Phone") : newPhone;

                        // Update the customer details in the database
                        String updateQuery = "UPDATE Customers SET Name = ?, Email = ?, Phone = ? WHERE CustomerID = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setString(1, newName);
                            updateStatement.setString(2, newEmail);
                            updateStatement.setString(3, newPhone);
                            updateStatement.setInt(4, customerId);

                            int rowsAffected = updateStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Customer updated successfully!");
                            } else {
                                System.out.println("Failed to update the customer. Please try again.");
                            }
                        }
                    } else {
                        System.out.println("Customer not found with ID: " + customerId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update the customer. Please try again.");
        }
    }

    private void viewPurchaseHistory() {
        System.out.println("===== View Purchase History =====");
        try {
            // Collect customer ID from the user
            System.out.print("Enter Customer ID: ");
            int customerId = getNonNegativeIntInput();
            scanner.nextLine(); // Consume the newline

            // Retrieve and display the purchase history for the customer from the database
            String selectQuery = "SELECT Sales.DateOfSale, Books.Title, Customers.Name " +
                    "FROM Sales " +
                    "JOIN Books ON Sales.BookID = Books.BookID " +
                    "JOIN Customers ON Sales.CustomerID = Customers.CustomerID " +
                    "WHERE Sales.CustomerID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, customerId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Purchase History for Customer ID " + customerId + ":");
                        do {
                            System.out.println("Date of Sale: " + resultSet.getDate("DateOfSale"));
                            System.out.println("Book Title: " + resultSet.getString("Title"));
                            System.out.println("Customer Name: " + resultSet.getString("Name"));
                            System.out.println("------------------------");
                        } while (resultSet.next());
                    } else {
                        System.out.println("No purchase history found for Customer ID " + customerId + ".");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to view purchase history. Please try again.");
        }
    }

    private void processSale() {
        System.out.println("===== Process Sale =====");
        try {
            // Get user input for sale details
            System.out.print("Enter Book ID: ");
            int bookId = getNonNegativeIntInput();

            System.out.print("Enter Customer ID: ");
            int customerId = getNonNegativeIntInput();

            System.out.print("Enter Quantity Sold: ");
            int quantitySold = getNonNegativeIntInput();

            // Check if the requested quantity is available in stock
            if (!isStockAvailable(bookId, quantitySold)) {
                System.out.println("Insufficient stock. Sale cannot be processed.");
                return;
            }

            double totalPrice = calculateTotalPrice(bookId, quantitySold);

            // Insert the sale details into the Sales table
            insertSale(bookId, customerId, quantitySold, totalPrice);

            // Update the quantity in stock for the corresponding book
            updateStock(bookId, quantitySold);

            System.out.println("Sale processed successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to process the sale. Please try again.");
        } finally {
            scanner.nextLine(); // Consume the newline
        }
    }

    private boolean isStockAvailable(int bookId, int quantitySold) throws SQLException {
        // Check if the requested quantity is available in stock
        String checkStockQuery = "SELECT QuantityInStock FROM Books WHERE BookID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(checkStockQuery)) {
            preparedStatement.setInt(1, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int availableStock = resultSet.getInt("QuantityInStock");
                return availableStock >= quantitySold;
            }
        }
        return false;
    }

    private double calculateTotalPrice(int bookId, int quantitySold) throws SQLException {
        // Calculate total price based on the book's price and quantity sold
        String getPriceQuery = "SELECT Price FROM Books WHERE BookID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(getPriceQuery)) {
            preparedStatement.setInt(1, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double bookPrice = resultSet.getDouble("Price");
                return bookPrice * quantitySold;
            }
        }
        return 0.0;
    }

    private void insertSale(int bookId, int customerId, int quantitySold, double totalPrice) throws SQLException {
        // Insert the sale details into the Sales table
        String insertSaleQuery = "INSERT INTO Sales (BookID, CustomerID, DateOfSale, QuantitySold, TotalPrice) " +
                "VALUES (?, ?, CURRENT_DATE, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSaleQuery)) {
            preparedStatement.setInt(1, bookId);
            preparedStatement.setInt(2, customerId);
            preparedStatement.setInt(3, quantitySold);
            preparedStatement.setDouble(4, totalPrice);
            preparedStatement.executeUpdate();
        }
    }

    private void updateStock(int bookId, int quantitySold) throws SQLException {
        // Update the quantity in stock for the corresponding book
        String updateStockQuery = "UPDATE Books SET QuantityInStock = QuantityInStock - ? WHERE BookID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateStockQuery)) {
            preparedStatement.setInt(1, quantitySold);
            preparedStatement.setInt(2, bookId);
            preparedStatement.executeUpdate();
        }
    }

    private void calculateRevenueByGenre() {
        System.out.println("===== Calculate Revenue by Genre =====");
        try {
            // Query to calculate total revenue by genre
            String calculateRevenueQuery = "SELECT Books.Genre, SUM(Sales.TotalPrice) as Revenue " +
                    "FROM Sales " +
                    "JOIN Books ON Sales.BookID = Books.BookID " +
                    "GROUP BY Books.Genre";

            // Execute the query
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(calculateRevenueQuery)) {

                // Map to store genre-wise total revenue
                Map<String, Double> revenueByGenre = new HashMap<>();

                // Process the result set
                while (resultSet.next()) {
                    String genre = resultSet.getString("Genre");
                    double revenue = resultSet.getDouble("Revenue");
                    revenueByGenre.put(genre, revenue);
                }

                // Display the result
                for (Map.Entry<String, Double> entry : revenueByGenre.entrySet()) {
                    System.out.println("Genre: " + entry.getKey() + ", Revenue: " + entry.getValue());
                }

                System.out.println("Revenue calculation by genre completed successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to calculate revenue by genre. Please try again.");
        }
    }

    private void generateSaleReport() {
        System.out.println("===== Generate Sale Report =====");
        try {
            // Query to generate a report of all books sold
            String generateReportQuery = "SELECT Sales.DateOfSale, Books.Title, Customers.Name " +
                    "FROM Sales " +
                    "JOIN Books ON Sales.BookID = Books.BookID " +
                    "JOIN Customers ON Sales.CustomerID = Customers.CustomerID";

            // Execute the query
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(generateReportQuery)) {

                // Display the report header
                System.out.printf("%-20s%-40s%-20s%n", "Date of Sale", "Book Title", "Customer Name");
                System.out.println("===================================================================");

                // Process the result set and display the report
                while (resultSet.next()) {
                    Date dateOfSale = resultSet.getDate("DateOfSale");
                    String bookTitle = resultSet.getString("Title");
                    String customerName = resultSet.getString("Name");
                    System.out.printf("%-20s%-40s%-20s%n", dateOfSale, bookTitle, customerName);
                }

                System.out.println("Sale report generated successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to generate the sale report. Please try again.");
        }
    }

    private void generateRevenueReportByGenre() {
        System.out.println("===== Generate Revenue Report by Genre =====");
        try {
            // Query to generate a report of total revenue by genre
            String generateReportQuery = "SELECT Books.Genre, SUM(Sales.TotalPrice) as Revenue " +
                    "FROM Sales " +
                    "JOIN Books ON Sales.BookID = Books.BookID " +
                    "GROUP BY Books.Genre";

            // Execute the query
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(generateReportQuery)) {

                // Display the report header
                System.out.printf("%-20s%-20s%n", "Genre", "Total Revenue");
                System.out.println("===================================");

                // Process the result set and display the report
                while (resultSet.next()) {
                    String genre = resultSet.getString("Genre");
                    double revenue = resultSet.getDouble("Revenue");
                    System.out.printf("%-20s%-20.2f%n", genre, revenue);
                }

                System.out.println("Revenue report by genre generated successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to generate the revenue report by genre. Please try again.");
        }
    }

    private int getValidMenuChoice(int min, int max) {
        int choice;
        do {
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next(); // Consume the invalid input
            }
            choice = scanner.nextInt();
            if (choice < min || choice > max) {
                System.out.println("Invalid choice. Please enter a number between " + min + " and " + max + ".");
            }
        } while (choice < min || choice > max);
        return choice;
    }

    private String getInput(String prompt, String defaultValue) {
        System.out.print(prompt);
        String userInput = scanner.nextLine().trim();
        return userInput.isEmpty() ? defaultValue : userInput;
    }

    private String getNonEmptyInput() {
        String input;
        do {
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please enter a value.");
            }
        } while (input.isEmpty());
        return input;
    }

    private double getPositiveDoubleInput() {
        double value;
        do {
            while (!scanner.hasNextDouble()) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next(); // Consume the invalid input
            }
            value = scanner.nextDouble();
            if (value <= 0) {
                System.out.println("Value must be greater than 0. Please enter a valid number.");
            }
            scanner.nextLine(); // Consume the newline
        } while (value <= 0);
        return value;
    }

    private int getNonNegativeIntInput() {
        int value;
        do {
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next(); // Consume the invalid input
            }
            value = scanner.nextInt();
            if (value < 0) {
                System.out.println("Value cannot be negative. Please enter a non-negative integer.");
            }
            scanner.nextLine(); // Consume the newline
        } while (value < 0);
        return value;
    }
}
