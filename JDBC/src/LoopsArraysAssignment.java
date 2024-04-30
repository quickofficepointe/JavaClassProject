import java.util.Scanner;

public class LoopsArraysAssignment {
    private String name;
    private double salary;

    // Constructor to initialize name and salary
    public LoopsArraysAssignment(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    // Method to get the salary of the employee
    public double getSalary() {
        return salary;
    }

    // Main program
    public static void main(String[] args) {
        // Create an array to store seven Employee objects
        LoopsArraysAssignment[] employees = new LoopsArraysAssignment[7];

        // Create a Scanner object to read user input
        Scanner scanner = new Scanner(System.in);

        // Prompt the user to enter inputs for each employee
        for (int i = 0; i < employees.length; i++) {
            System.out.println("Enter name for Employee " + (i + 1) + ":");
            String name = scanner.nextLine();
            System.out.println("Enter salary for Employee " + (i + 1) + ":");
            double salary = scanner.nextDouble();
            scanner.nextLine(); // Consume newline character

            // Initialize the Employee object with the user inputs
            employees[i] = new LoopsArraysAssignment(name, salary);
        }

        // Close the scanner
        scanner.close();

        // Iterate through the array of Employee objects
        for (LoopsArraysAssignment employee : employees) {
            // Display each employee's name and salary using the getSalary() method
            System.out.println("Employee Name: " + employee.name);
            System.out.println("Employee Salary: " + employee.getSalary());
            System.out.println();
        }
    }
}
