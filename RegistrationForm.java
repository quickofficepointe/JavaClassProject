import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import javafx.stage.Stage;

public class RegistrationForm extends Application {

    private Connection connection;
    private Label feedbackLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Registration Form");

        // Initialize database connection
        initializeDatabase();

        // Registration form
        GridPane registrationGrid = createRegistrationForm();

        // Login form
        GridPane loginGrid = createLoginForm();

        // Create feedback label
        feedbackLabel = new Label();
        feedbackLabel.setPadding(new Insets(10));
        feedbackLabel.setStyle("-fx-text-fill: green;");

        // Create scenes
        Scene registrationScene = new Scene(registrationGrid, 300, 250);
        Scene loginScene = new Scene(loginGrid, 300, 250);

        // Set initial scene to registration form
        primaryStage.setScene(registrationScene);

        primaryStage.show();
    }

    private GridPane createRegistrationForm() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Registration form fields
        Label nameLabel = new Label("Name:");
        GridPane.setConstraints(nameLabel, 0, 0);
        TextField nameField = new TextField();
        GridPane.setConstraints(nameField, 1, 0);

        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 1);
        TextField usernameField = new TextField();
        GridPane.setConstraints(usernameField, 1, 1);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 2);
        PasswordField passwordField = new PasswordField();
        GridPane.setConstraints(passwordField, 1, 2);

        Button submitButton = new Button("Submit");
        GridPane.setConstraints(submitButton, 1, 3);


         // Initialize feedbackLabel
    feedbackLabel = new Label();
    feedbackLabel.setPadding(new Insets(10));
    feedbackLabel.setStyle("-fx-text-fill: green;");

        grid.getChildren().addAll(nameLabel, nameField, usernameLabel, usernameField, passwordLabel, passwordField, submitButton, feedbackLabel);

        // Handle submit button action
        submitButton.setOnAction(event -> {
            String name = nameField.getText();
            String username = usernameField.getText();
            String password = hashPassword(passwordField.getText());

            // Insert data into the database
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO users (name, username, password) VALUES (?, ?, ?)");
                statement.setString(1, name);
                statement.setString(2, username);
                statement.setString(3, password);
                statement.executeUpdate();
                feedbackLabel.setText("Registration successful!");
                // Switch to login scene after successful registration
                Stage stage = (Stage) submitButton.getScene().getWindow();
                stage.setScene(createRegistrationScene());
            } catch (SQLException e) {
                feedbackLabel.setText("Error registering: " + e.getMessage());
            }
        });

        return grid;
    }

    private GridPane createLoginForm() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Login form fields
        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);
        TextField usernameField = new TextField();
        GridPane.setConstraints(usernameField, 1, 0);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);
        PasswordField passwordField = new PasswordField();
        GridPane.setConstraints(passwordField, 1, 1);

        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);

        Button logoutButton = new Button("Logout"); // Adding logout button
        GridPane.setConstraints(logoutButton, 1, 3);
        // Initialize feedbackLabel
    feedbackLabel = new Label();
    feedbackLabel.setPadding(new Insets(10));
    feedbackLabel.setStyle("-fx-text-fill: green;");

        grid.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton, feedbackLabel);

        // Handle login button action
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = hashPassword(passwordField.getText());

            // Check if username and password match database records
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
                statement.setString(1, username);
                statement.setString(2, password);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    feedbackLabel.setText("Login successful!");
                    // Add logic to proceed after successful login
                    openCalculator();
                } else {
                    feedbackLabel.setText("Invalid username or password.");
                }
            } catch (SQLException e) {
                feedbackLabel.setText("Error logging in: " + e.getMessage());
            }
        });

           // Handle logout button action
        logoutButton.setOnAction(event -> {
        feedbackLabel.setText("Logged out successfully!");
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.setScene(createRegistrationScene());
    });

        return grid;
    }

    private void openCalculator() {
        BodmaSSample calculator = new BodmaSSample();
        calculator.start(new Stage());
    }

    private Scene createRegistrationScene() {
        return new Scene(createLoginForm(), 300, 250);
    }

    private void initializeDatabase() {
        String url = "jdbc:mysql://localhost:3306/bodmas_db";
        String username = "root";
        String password = "";

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to database!");
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
