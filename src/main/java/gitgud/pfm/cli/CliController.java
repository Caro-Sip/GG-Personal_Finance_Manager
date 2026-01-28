package gitgud.pfm.cli;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import gitgud.pfm.Models.Transaction;
import gitgud.pfm.Models.Budget;
import gitgud.pfm.services.GenericSQLiteService;
import gitgud.pfm.services.CategoryService;
import gitgud.pfm.Models.Category;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * CLI Controller - Manages the command-line interface and user interactions
 * Handles menu flow and delegates business logic to services
 */
public class CliController {
    private Scanner scanner;
    private boolean running = true;
    private final CategoryService categoryService = new CategoryService();
    
    public CliController() {
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Start the CLI application
     */
    public void start() {
        printWelcomeMessage();
        mainMenuLoop();
        shutdown();
    }
    
    /**
     * Main menu loop - handles user input and navigation
     */
    private void mainMenuLoop() {
        while (running) {
            printMainMenu();
            System.out.println();
            System.out.print("Please select an option: ");
            String input = scanner.nextLine().trim();
            
            switch (input) {
                case "1":
                    handleViewAccounts();
                    break;
                case "2":
                    handleAddTransaction();
                    break;
                case "5":
                    handleAddBudget();
                    break;
                case "3":
                    handleViewReports();
                    break;
                case "4":
                    // looks for users input then call exit program
                    // then changes running to false to exit loop
                    handleExit();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
            
            System.out.println();
            pauseConsole();
            clearConsole();
        }
    }
    
    // "handle" prefix refers to dealing with user input rather than logic

    /**
     * Handle View Accounts menu option
     */
    private void handleViewAccounts() {
        System.out.println("View Accounts feature is not implemented yet.");
        // TODO: Call AccountService to get and display accounts
    }
    
    /**
     * Handle Add Transaction menu option
     */
    private void handleAddTransaction() {
        System.out.println("=== Add Transaction ===");

        // Get transaction ID
        System.out.print("Enter transaction ID: ");
        String id = scanner.nextLine().trim();
        
        // Get amount
        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine().trim());
        
        // Get transaction name
        System.out.print("Enter transaction name: ");
        String name = scanner.nextLine().trim();
        

        // Display predefined categories and prompt for selection
        var defaultCategories = categoryService.getDefaultCategories();
        System.out.println("Select a category:");
        int idx = 1;
        System.out.println("Expense Categories:");
        for (Category cat : defaultCategories) {
            if (cat.getType() == Category.Type.EXPENSE) {
                System.out.printf("  %d. %s\n", idx, cat.getName());
            }
            idx++;
        }
        idx = 1;
        System.out.println("Income Categories:");
        for (Category cat : defaultCategories) {
            if (cat.getType() == Category.Type.INCOME) {
                System.out.printf("  %d. %s\n", idx, cat.getName());
            }
            idx++;
        }
        int selected = -1;
        Category selectedCategory = null;
        while (selectedCategory == null) {
            System.out.print("Enter the number of the category: ");
            String input = scanner.nextLine().trim();
            try {
                int num = Integer.parseInt(input);
                if (num >= 1 && num <= defaultCategories.size()) {
                    selectedCategory = defaultCategories.get(num - 1);
                } else {
                    System.out.println("Invalid number. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        String category = selectedCategory.getName();
        

        // Get account ID
        System.out.print("Enter account ID: ");
        String accountID = scanner.nextLine().trim();

        double income = 0.0;
        if (selectedCategory.getType() == Category.Type.INCOME) {
            System.out.print("Enter income amount: ");
            income = Double.parseDouble(scanner.nextLine().trim());
        } else {
            System.out.println("Income amount set to 0 for expense category.");
        }

        // Get current timestamp
        String timestamp = java.time.LocalDateTime.now().toString();

        // Call service to add transaction
        Transaction transaction = new Transaction(id, category, amount, name, income, accountID, timestamp);
        
        Map<String, Object> config = new HashMap<>();
        config.put("class", Transaction.class);
        config.put("table", "transaction_records");
        config.put("entity", transaction);
        GenericSQLiteService.create(config);
        
        System.out.println("Transaction added successfully!");
    }
    
    /**
     * Handle View Reports menu option
     */
    private void handleViewReports() {
        System.out.println("View Reports feature is not implemented yet.");
        // TODO: Call ReportService to display reports
    }
    
    /**
     * Handle Exit menu option
     */
    private void handleExit() {
        System.out.println("Exiting the Personal Finance Manager CLI. Goodbye!");
        exitProgram();
    }
    
    private void printWelcomeMessage() {
        System.out.println("Welcome to the Personal Finance Manager CLI!");
        System.out.println("-------------------------------------------");
    }

    private void printMainMenu() {
        System.out.println("Main Menu:");
        System.out.println("1. View Accounts");
        System.out.println("2. Add Transaction");
        System.out.println("3. View Reports");
        System.out.println("4. Exit");
        System.out.println("5. Add Budget");
    }

    /**
     * Handle Add Budget menu option
     */
    private void handleAddBudget() {
        System.out.println("=== Add Budget ===");

        System.out.print("Enter budget ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter budget name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter limit amount: ");
        double limits = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Enter starting balance: ");
        double balance = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDate = scanner.nextLine().trim();

        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDate = scanner.nextLine().trim();

        System.out.print("Enter tracked categories (comma-separated): ");
        String tracked = scanner.nextLine().trim();

        Budget budget = new Budget(id, name, limits, balance, startDate, endDate, tracked);

        System.out.println("Budget created: " + budget.getName());
    }

    private void exitProgram() {
        running = false;
    }

    private void pauseConsole() {
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    // Source - https://stackoverflow.com/a
    // Posted by Muhammed Gül
    // Retrieved 2026-01-27, License - CC BY-SA 4.0
    private void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else {
                System.out.print("\033\143");
            }
        } catch (IOException | InterruptedException ex) {
            // Ignore errors in clearing console
        }
    }

    private void shutdown() {
        scanner.close();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
