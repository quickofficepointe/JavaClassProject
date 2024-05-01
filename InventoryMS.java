import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class InventoryMS extends Application {
    // JDBC URL for MySQL database
    private static final String URL = "jdbc:mysql://localhost:3306/inventory";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private TextField nameField;
    private TextField quantityField;
    private TextField priceField;
    private TextField supplierField;
    

    @Override
    public void start(Stage primaryStage) {
        // Initialize UI components
        Label taskLabel = new Label("Select Task:");
        Button addButton = createStyledButton("Add Item");
        addButton.setOnAction(e -> showAddItemDialog(primaryStage));

        Button deleteButton = createStyledButton("Delete Item");
        deleteButton.setOnAction(e -> showDeleteItemDialog(primaryStage));

        Button updateButton = createStyledButton("Update Quantity");
        updateButton.setOnAction(e -> showUpdateQuantityDialog(primaryStage));

        Button retrieveButton = createStyledButton("Retrieve Information");
        retrieveButton.setOnAction(e -> retrieveInformation(primaryStage));

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(20));
        gridPane.addRow(0, taskLabel);
        gridPane.addRow(1, addButton);
        gridPane.addRow(2, deleteButton);
        gridPane.addRow(3, updateButton);
        gridPane.addRow(4, retrieveButton);

        Scene scene = new Scene(gridPane, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Inventory Management System");
        primaryStage.show();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 150px; -fx-padding: 10px; -fx-border-radius: 5px;");
        button.setOnAction(event -> {});
        return button;
    }
    
    private void showUpdateQuantityDialog(Stage primaryStage) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Quantity");
        dialog.setHeaderText("Enter item name to update quantity:");
        dialog.setContentText("Item Name:");

        // Show dialog and handle response
        dialog.showAndWait().ifPresent(itemName -> showUpdateQuantityInputDialog(primaryStage, itemName));
    }

    private void showUpdateQuantityInputDialog(Stage primaryStage, String itemName) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Quantity");
        dialog.setHeaderText("Enter new quantity for " + itemName + ":");
        dialog.setContentText("New Quantity:");

        // Show dialog and handle response
        dialog.showAndWait().ifPresent(newQuantity -> updateQuantity(primaryStage, itemName, newQuantity));
    }

    private void updateQuantity(Stage primaryStage, String itemName, String newQuantity) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "UPDATE inventory SET quantity = ? WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newQuantity);
            statement.setString(2, itemName);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                displayAlert(primaryStage, "Quantity updated successfully!");
            } else {
                displayAlert(primaryStage, "Item not found!");
            }
        } catch (SQLException e) {
            displayAlert(primaryStage, "Error updating quantity in the database: " + e.getMessage());
        }


    }


    private void showAddItemDialog(Stage primaryStage) {
        Dialog<Item> dialog = new Dialog<>();
        dialog.setTitle("Add Item");
        dialog.setHeaderText("Enter item details:");

        // Set the button types
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Create and configure text fields
        nameField = new TextField();
        nameField.setPromptText("Name");
        quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        priceField = new TextField();
        priceField.setPromptText("Price");
        supplierField = new TextField();
        supplierField.setPromptText("Supplier");

        // Add text fields to dialog
        dialog.getDialogPane().setContent(new VBox(8, nameField, quantityField, priceField, supplierField));

        // Request focus on the name field by default
        Platform.runLater(() -> nameField.requestFocus());

        // Convert result to an item when addButton clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                return new Item(nameField.getText(), Integer.parseInt(quantityField.getText()),
                        Double.parseDouble(priceField.getText()), supplierField.getText());
            }
            return null;
        });

        // Show dialog and handle response
        dialog.showAndWait().ifPresent(item -> addItem(primaryStage, item));
    }

    private void showDeleteItemDialog(Stage primaryStage) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Item");
        dialog.setHeaderText("Enter item name to delete:");
        dialog.setContentText("Item Name:");

        // Show dialog and handle response
        dialog.showAndWait().ifPresent(itemName -> deleteItem(primaryStage, itemName));
    }

    private void addItem(Stage primaryStage, Item item) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO inventory (name, quantity, price, supplier) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, item.getName());
            statement.setInt(2, item.getQuantity());
            statement.setDouble(3, item.getPrice());
            statement.setString(4, item.getSupplier());
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                displayAlert(primaryStage, "Item added successfully!");
            }
        } catch (SQLException e) {
            displayAlert(primaryStage, "Error adding item to the database: " + e.getMessage());
        }
    }

    private void deleteItem(Stage primaryStage, String itemName) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "DELETE FROM inventory WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, itemName);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                displayAlert(primaryStage, "Item deleted successfully!");
            } else {
                displayAlert(primaryStage, "Item not found!");
            }
        } catch (SQLException e) {
            displayAlert(primaryStage, "Error deleting item from the database: " + e.getMessage());
        }
    }

    private void retrieveInformation(Stage primaryStage) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM inventory";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            StringBuilder info = new StringBuilder();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");
                String supplier = resultSet.getString("supplier");
                info.append("Name: ").append(name).append(", Quantity: ").append(quantity)
                        .append(", Price: ").append(price).append(", Supplier: ").append(supplier).append("\n");
            }
            displayInformation(primaryStage, info.toString());
        } catch (SQLException e) {
            displayAlert(primaryStage, "Error retrieving information from the database: " + e.getMessage());
        }
    }

    private void displayAlert(Stage primaryStage, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }

    private void displayInformation(Stage primaryStage, String information) {
        TextArea textArea = new TextArea(information);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(10);
        textArea.setPrefColumnCount(30);
        textArea.setPrefWidth(300);
        textArea.setPrefHeight(200);

        ScrollPane scrollPane = new ScrollPane(textArea);

        VBox root = new VBox(scrollPane);
        Scene scene = new Scene(root, 320, 220);
        Stage informationStage = new Stage();
        informationStage.setScene(scene);
        informationStage.setTitle("Inventory Information");
        informationStage.initOwner(primaryStage);
        informationStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    static class Item {
        private final String name;
        private final int quantity;
        private final double price;
        private final String supplier;

        public Item(String name, int quantity, double price, String supplier) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
            this.supplier = supplier;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public String getSupplier() {
            return supplier;
        }
    }
}
