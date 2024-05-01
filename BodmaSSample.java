import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Stack;

public class BodmaSSample extends Application {

    private Stack<String> history = new Stack<>();

    @Override
    public void start(Stage primaryStage) {
        // Create UI elements
        TextField inputField = new TextField();
        inputField.setAlignment(Pos.CENTER_RIGHT);
        Label resultLabel = new Label();
        resultLabel.setAlignment(Pos.CENTER_RIGHT);
        resultLabel.setBackground(new Background(new BackgroundFill(Color.rgb(245, 245, 245), CornerRadii.EMPTY, Insets.EMPTY)));
        Label errorLabel = new Label();
        errorLabel.setAlignment(Pos.CENTER);
        Button calculateButton = new Button("=");
        calculateButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        // Create numeric buttons
        Button[] numericButtons = new Button[10];
        for (int i = 0; i < 10; i++) {
            final int digit = i;
            numericButtons[i] = new Button(String.valueOf(i));
            numericButtons[i].setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            numericButtons[i].setOnAction(event -> inputField.appendText(String.valueOf(digit)));
            numericButtons[i].setPrefWidth(70);
            numericButtons[i].setPrefHeight(70);
        }

        // Create operator buttons
        Button[] operatorButtons = {
            new Button("+"), new Button("-"), new Button("*"), new Button("/"), new Button("^")
        };
        for (Button button : operatorButtons) {
            button.setBackground(new Background(new BackgroundFill(Color.LIGHTCORAL, CornerRadii.EMPTY, Insets.EMPTY)));
            button.setOnAction(event -> inputField.appendText(button.getText()));
            button.setPrefWidth(70);
            button.setPrefHeight(70);
        }

        // Create clear button
        Button clearButton = new Button("Clear");
        clearButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        clearButton.setOnAction(event -> {
            inputField.clear();
            resultLabel.setText("");
        });
        clearButton.setPrefWidth(70);
        clearButton.setPrefHeight(70);

        // Create history area
        TextArea historyArea = new TextArea();
        historyArea.setEditable(false);
        historyArea.setPrefHeight(200);
        historyArea.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        historyArea.setStyle("-fx-font-size: 14px;");

        // Set button action
        calculateButton.setOnAction(event -> {
            try {
                String expression = inputField.getText();
                double result = evaluateExpression(expression);
                resultLabel.setText("Result: " + result);
                history.push(expression + " = " + result);
                inputField.clear();
                errorLabel.setText("");
                updateHistoryArea(historyArea);
            } catch (Exception e) {
                resultLabel.setText("");
                errorLabel.setText("Error: " + e.getMessage());
            }
        });

        // Create layout
        GridPane buttonGrid = new GridPane();
        buttonGrid.setPadding(new Insets(10));
        buttonGrid.setHgap(5);
        buttonGrid.setVgap(5);
        for (int i = 0; i < 10; i++) {
            buttonGrid.add(numericButtons[i], i % 3, 3 - i / 3);
        }
        buttonGrid.add(operatorButtons[0], 3, 0);
        buttonGrid.add(operatorButtons[1], 3, 1);
        buttonGrid.add(operatorButtons[2], 3, 2);
        buttonGrid.add(operatorButtons[3], 3, 3);
        buttonGrid.add(operatorButtons[4], 4, 0);
        buttonGrid.add(clearButton, 4, 3);
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(inputField, buttonGrid, calculateButton, resultLabel, errorLabel, historyArea);

        // Set scene
        Scene scene = new Scene(vbox, 400, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("MUCHINA'S BODMAS CALCULATOR");
        primaryStage.show();
    }

    public static double evaluateExpression(String expression) {
        // Stack to store operands
        Stack<Double> operands = new Stack<>();

        // Stack to store operators
        Stack<Character> operators = new Stack<>();

        // Remove whitespaces from the expression
        expression = expression.replaceAll("\\s", "");

        // Operator precedence map
        java.util.Map<Character, Integer> precedence = new java.util.HashMap<>();
        precedence.put('+', 1);
        precedence.put('-', 1);
        precedence.put('*', 2);
        precedence.put('/', 2);
        precedence.put('^', 3);

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isDigit(c)) {
                // Extract the operand
                StringBuilder operand = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    operand.append(expression.charAt(i++));
                }
                operands.push(Double.parseDouble(operand.toString()));
                i--;
            } else if (c == '(') {
                // Push opening bracket to operators stack
                operators.push(c);
            } else if (c == ')') {
                // Evaluate expression inside brackets
                while (!operators.isEmpty() && operators.peek() != '(') {
                    double result = applyOperation(operators.pop(), operands.pop(), operands.pop());
                    operands.push(result);
                }
                operators.pop(); // Pop opening bracket
            } else if (precedence.containsKey(c)) {
                // Process operators
                Integer prec = precedence.get(c);
                if (prec != null) {
                    while (!operators.isEmpty() && precedence.get(operators.peek()) != null && prec <= precedence.get(operators.peek())) {
                        double result = applyOperation(operators.pop(), operands.pop(), operands.pop());
                        operands.push(result);
                    }
                    operators.push(c);
                }
            }
        }

        // Evaluate remaining operators
        while (!operators.isEmpty()) {
            double result = applyOperation(operators.pop(), operands.pop(), operands.pop());
            operands.push(result);
        }

        // Result is the only element left in the operands stack
        return operands.pop();
    }

    private static double applyOperation(char operator, double operand2, double operand1) {
        switch (operator) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '*':
                return operand1 * operand2;
            case '/':
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return operand1 / operand2;
            case '^':
                return Math.pow(operand1, operand2);
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

    private void updateHistoryArea(TextArea historyArea) {
        historyArea.clear();
        for (String entry : history) {
            historyArea.appendText(entry + "\n");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

