package gitgud.pfm.Controllers;

import gitgud.pfm.GUI.data.DataStore;
import gitgud.pfm.Models.Category;
import gitgud.pfm.Models.Transaction;
import gitgud.pfm.Models.Wallet;
import gitgud.pfm.services.CategoryService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TransactionController implements Initializable {
    
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, String> nameColumn;
    @FXML private TableColumn<Transaction, String> categoryColumn;
    @FXML private TableColumn<Transaction, String> accountColumn;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, String> amountColumn;
    @FXML private TableColumn<Transaction, Void> actionsColumn;
    @FXML private Label balanceLabel;
    @FXML private Button addButton;
    
    private DataStore dataStore;
    private CategoryService categoryService;
    private ObservableList<Transaction> transactionData;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataStore = DataStore.getInstance();
        categoryService = new CategoryService();
        transactionData = FXCollections.observableArrayList();
        
        setupTableColumns();
        transactionTable.setItems(transactionData);
        
        loadTransactions();
    }
    
    private void setupTableColumns() {
        // Date column
        dateColumn.setCellValueFactory(cellData -> {
            String date = cellData.getValue().getCreateTime();
            return new SimpleStringProperty(date != null ? date : "");
        });
        
        // Name column
        nameColumn.setCellValueFactory(cellData -> {
            String name = cellData.getValue().getName();
            return new SimpleStringProperty(name != null ? name : "");
        });
        
        // Category column
        categoryColumn.setCellValueFactory(cellData -> {
            String categoryId = cellData.getValue().getCategoryId();
            String categoryName = getCategoryName(categoryId);
            return new SimpleStringProperty(categoryName);
        });
        
        // Account column
        accountColumn.setCellValueFactory(cellData -> {
            String walletId = cellData.getValue().getWalletId();
            String walletName = getWalletName(walletId);
            return new SimpleStringProperty(walletName);
        });
        
        // Type column
        typeColumn.setCellValueFactory(cellData -> {
            double income = cellData.getValue().getIncome();
            return new SimpleStringProperty(income == 1.0 ? "Income" : "Expense");
        });
        typeColumn.setCellFactory(column -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Income")) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        // Amount column
        amountColumn.setCellValueFactory(cellData -> {
            double amount = cellData.getValue().getAmount();
            return new SimpleStringProperty(String.format("$%.2f", amount));
        });
        amountColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        // Actions column
        actionsColumn.setCellFactory(column -> new TableCell<Transaction, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(8, editBtn, deleteBtn);
            
            {
                editBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; " +
                               "-fx-font-size: 12px; -fx-padding: 5 10; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; " +
                                  "-fx-font-size: 12px; -fx-padding: 5 10; -fx-cursor: hand;");
                pane.setAlignment(Pos.CENTER);
                
                editBtn.setOnAction(e -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    handleEditTransaction(transaction);
                });
                
                deleteBtn.setOnAction(e -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    handleDeleteTransaction(transaction);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }
    
    // Category info helper class
    private static class CategoryInfo {
        String id;
        String color;
        String icon;
        
        CategoryInfo(String id, String color, String icon) {
            this.id = id;
            this.color = color;
            this.icon = icon;
        }
    }
    
    // Category definitions
    private static final Map<String, CategoryInfo> EXPENSE_CATEGORIES = new LinkedHashMap<>();
    private static final Map<String, CategoryInfo> INCOME_CATEGORIES = new LinkedHashMap<>();
    
    static {
        EXPENSE_CATEGORIES.put("Food & Drinks", new CategoryInfo("1", "#ef4444", "🍔"));
        EXPENSE_CATEGORIES.put("Transport", new CategoryInfo("2", "#f97316", "🚗"));
        EXPENSE_CATEGORIES.put("Home Bills", new CategoryInfo("3", "#eab308", "🏠"));
        EXPENSE_CATEGORIES.put("Self-care", new CategoryInfo("4", "#84cc16", "💆"));
        EXPENSE_CATEGORIES.put("Shopping", new CategoryInfo("5", "#22c55e", "🛒"));
        EXPENSE_CATEGORIES.put("Health", new CategoryInfo("6", "#14b8a6", "💊"));
        EXPENSE_CATEGORIES.put("Subscription", new CategoryInfo("9", "#06b6d4", "📱"));
        EXPENSE_CATEGORIES.put("Entertainment & Sport", new CategoryInfo("10", "#3b82f6", "🎮"));
        EXPENSE_CATEGORIES.put("Traveling", new CategoryInfo("11", "#8b5cf6", "✈️"));
        
        INCOME_CATEGORIES.put("Salary", new CategoryInfo("7", "#10b981", "💰"));
        INCOME_CATEGORIES.put("Investment", new CategoryInfo("8", "#6366f1", "📈"));
    }
    
    @FXML
    private void handleAddTransaction() {
        // Create popup stage
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Add Transaction");
        
        // Create container that will switch between category view and form view
        BorderPane container = new BorderPane();
        container.setStyle("-fx-background-color: #f0f2f5;");
        
        // Show category cards view first
        showCategoryCardsInPopup(popupStage, container);
        
        Scene scene = new Scene(container, 700, 600);
        popupStage.setScene(scene);
        popupStage.showAndWait();
        
        // Refresh transactions after popup closes
        loadTransactions();
        updateBalanceDisplay();
    }
    
    private void showCategoryCardsInPopup(Stage popupStage, BorderPane container) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f0f2f5; -fx-background: #f0f2f5;");
        
        VBox mainContent = new VBox(24);
        mainContent.setPadding(new Insets(28));
        mainContent.setStyle("-fx-background-color: #f0f2f5;");
        
        // Header
        Label header = new Label("Add Transaction");
        header.setStyle("-fx-font-size: 28px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");
        
        Label subtitle = new Label("Select a category to add a new transaction");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
        
        VBox headerBox = new VBox(8);
        headerBox.getChildren().addAll(header, subtitle);
        
        // Expense Categories Section
        VBox expenseSection = createCategorySectionForPopup("Expenses", EXPENSE_CATEGORIES, Category.Type.EXPENSE, popupStage, container);
        
        // Income Categories Section
        VBox incomeSection = createCategorySectionForPopup("Income", INCOME_CATEGORIES, Category.Type.INCOME, popupStage, container);
        
        mainContent.getChildren().addAll(headerBox, expenseSection, incomeSection);
        scrollPane.setContent(mainContent);
        
        container.setCenter(scrollPane);
    }
    
    private VBox createCategorySectionForPopup(String title, Map<String, CategoryInfo> categories, Category.Type type, Stage popupStage, BorderPane container) {
        VBox section = new VBox(16);
        
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Region iconView = new Region();
        iconView.setPrefSize(18, 18);
        String iconColor = type == Category.Type.EXPENSE ? "#ef4444" : "#10b981";
        iconView.setStyle("-fx-background-color: " + iconColor + "; -fx-background-radius: 4;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");
        
        headerBox.getChildren().addAll(iconView, titleLabel);
        
        FlowPane cardsGrid = new FlowPane();
        cardsGrid.setHgap(16);
        cardsGrid.setVgap(16);
        cardsGrid.setPadding(new Insets(8, 0, 0, 0));
        
        for (Map.Entry<String, CategoryInfo> entry : categories.entrySet()) {
            VBox card = createCategoryCardForPopup(entry.getKey(), entry.getValue(), type, popupStage, container);
            cardsGrid.getChildren().add(card);
        }
        
        section.getChildren().addAll(headerBox, cardsGrid);
        return section;
    }
    
    private VBox createCategoryCardForPopup(String categoryName, CategoryInfo info, Category.Type type, Stage popupStage, BorderPane container) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(140);
        card.setPrefHeight(120);
        
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 16; " +
            "-fx-border-color: " + info.color + "40; " +
            "-fx-border-radius: 16; " +
            "-fx-border-width: 2; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);"
        );
        
        StackPane iconCircle = new StackPane();
        iconCircle.setPrefSize(48, 48);
        iconCircle.setStyle("-fx-background-color: " + info.color + "20; -fx-background-radius: 24;");
        
        Label iconLabel = new Label(info.icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        iconCircle.getChildren().add(iconLabel);
        
        Label nameLabel = new Label(categoryName);
        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #374151;");
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(120);
        
        card.getChildren().addAll(iconCircle, nameLabel);
        
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: " + info.color + "10; " +
            "-fx-background-radius: 16; " +
            "-fx-border-color: " + info.color + "; " +
            "-fx-border-radius: 16; " +
            "-fx-border-width: 2; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, " + info.color + "40, 12, 0, 0, 4);"
        ));
        
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 16; " +
            "-fx-border-color: " + info.color + "40; " +
            "-fx-border-radius: 16; " +
            "-fx-border-width: 2; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);"
        ));
        
        card.setOnMouseClicked(e -> showTransactionFormInPopup(categoryName, info, type, popupStage, container));
        
        return card;
    }
    
    private void showTransactionFormInPopup(String categoryName, CategoryInfo info, Category.Type type, Stage popupStage, BorderPane container) {
        VBox formContent = new VBox(20);
        formContent.setPadding(new Insets(24));
        formContent.setStyle("-fx-background-color: white;");
        
        // Back button
        Button backBtn = new Button("← Back to Categories");
        backBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #3b82f6; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: 500; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 8 0;"
        );
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(
            "-fx-background-color: #eff6ff; " +
            "-fx-text-fill: #2563eb; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: 500; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 8 12; " +
            "-fx-background-radius: 8;"
        ));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #3b82f6; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: 500; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 8 0;"
        ));
        backBtn.setOnAction(e -> showCategoryCardsInPopup(popupStage, container));
        
        // Header with category info
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        
        StackPane iconCircle = new StackPane();
        iconCircle.setPrefSize(48, 48);
        iconCircle.setStyle("-fx-background-color: " + info.color + "20; -fx-background-radius: 24;");
        Label iconLabel = new Label(info.icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        iconCircle.getChildren().add(iconLabel);
        
        VBox headerText = new VBox(4);
        Label titleLabel = new Label(categoryName);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");
        Label typeLabel = new Label(type == Category.Type.EXPENSE ? "Expense" : "Income");
        typeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + info.color + "; -fx-font-weight: 500;");
        headerText.getChildren().addAll(titleLabel, typeLabel);
        
        header.getChildren().addAll(iconCircle, headerText);
        
        // Form fields
        VBox formFields = new VBox(16);
        
        // Amount field
        VBox amountBox = new VBox(8);
        Label amountLabel = new Label("Amount");
        amountLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #374151;");
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount...");
        amountField.setStyle(
            "-fx-background-color: #f8fafc; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #e2e8f0; " +
            "-fx-border-radius: 10; " +
            "-fx-padding: 12 16; " +
            "-fx-font-size: 14px;"
        );
        amountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                amountField.setText(oldVal);
            }
        });
        amountBox.getChildren().addAll(amountLabel, amountField);
        
        // Date field
        VBox dateBox = new VBox(8);
        Label dateLabel = new Label("Date");
        dateLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #374151;");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-font-size: 14px;");
        dateBox.getChildren().addAll(dateLabel, datePicker);
        
        // Description field
        VBox descBox = new VBox(8);
        Label descLabel = new Label("Description");
        descLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #374151;");
        TextField descField = new TextField();
        descField.setPromptText("Enter description (optional)...");
        descField.setStyle(
            "-fx-background-color: #f8fafc; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #e2e8f0; " +
            "-fx-border-radius: 10; " +
            "-fx-padding: 12 16; " +
            "-fx-font-size: 14px;"
        );
        descBox.getChildren().addAll(descLabel, descField);
        
        // Wallet selector
        VBox walletBox = new VBox(8);
        Label walletLabel = new Label("Wallet");
        walletLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #374151;");
        ComboBox<String> walletCombo = new ComboBox<>();
        walletCombo.setPromptText("Select wallet...");
        walletCombo.setMaxWidth(Double.MAX_VALUE);
        walletCombo.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-font-size: 14px;");
        
        List<Wallet> wallets = dataStore.getWallets();
        Map<String, String> walletIdMap = new HashMap<>();
        for (Wallet wallet : wallets) {
            String displayName = wallet.getName() + " ($" + String.format("%.2f", wallet.getBalance()) + ")";
            walletCombo.getItems().add(displayName);
            walletIdMap.put(displayName, wallet.getId());
        }
        if (!wallets.isEmpty()) {
            walletCombo.getSelectionModel().selectFirst();
        }
        walletBox.getChildren().addAll(walletLabel, walletCombo);
        
        formFields.getChildren().addAll(amountBox, dateBox, descBox, walletBox);
        
        // Buttons
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(8, 0, 0, 0));
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle(
            "-fx-background-color: #f1f5f9; " +
            "-fx-text-fill: #64748b; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 12 24; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: 600; " +
            "-fx-cursor: hand;"
        );
        cancelBtn.setOnAction(e -> popupStage.close());
        
        Button saveBtn = new Button("Save Transaction");
        saveBtn.setStyle(
            "-fx-background-color: " + info.color + "; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 12 24; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: 600; " +
            "-fx-cursor: hand;"
        );
        
        saveBtn.setOnAction(e -> {
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                showAlert("Error", "Please enter an amount");
                return;
            }
            
            double amount;
            try {
                amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    showAlert("Error", "Amount must be greater than 0");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid amount format");
                return;
            }
            
            LocalDate date = datePicker.getValue();
            if (date == null) {
                showAlert("Error", "Please select a date");
                return;
            }
            
            String selectedWallet = walletCombo.getValue();
            if (selectedWallet == null || selectedWallet.isEmpty()) {
                showAlert("Error", "Please select a wallet");
                return;
            }
            
            String walletId = walletIdMap.get(selectedWallet);
            String description = descField.getText().trim();
            if (description.isEmpty()) {
                description = categoryName;
            }
            
            String createTime = date.atTime(java.time.LocalTime.now())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            double incomeValue = type == Category.Type.INCOME ? 1.0 : 0.0;
            Transaction transaction = new Transaction(
                info.id,
                amount,
                description,
                incomeValue,
                walletId,
                createTime
            );
            
            // Update wallet balance
            Wallet wallet = dataStore.getWalletById(walletId);
            if (wallet != null) {
                double newBalance = wallet.getBalance() + (type == Category.Type.INCOME ? amount : -amount);
                wallet.setBalance(newBalance);
                dataStore.updateWallet(wallet);
            }
            
            dataStore.addTransaction(transaction);
            
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Transaction added successfully!");
            successAlert.showAndWait();
            
            popupStage.close();
        });
        
        buttonBox.getChildren().addAll(cancelBtn, saveBtn);
        
        formContent.getChildren().addAll(backBtn, header, new Separator(), formFields, buttonBox);
        
        ScrollPane scrollPane = new ScrollPane(formContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
        
        container.setCenter(scrollPane);
    }
    
    private void handleEditTransaction(Transaction transaction) {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Edit Transaction");
        dialog.setHeaderText("Edit transaction details");
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField(transaction.getName());
        TextField amountField = new TextField(String.valueOf(transaction.getAmount()));
        
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Income", "Expense");
        typeCombo.setValue(transaction.getIncome() == 1.0 ? "Income" : "Expense");
        
        ComboBox<Category> categoryCombo = new ComboBox<>();
        List<Category> allCategories = categoryService.getAllCategories();
        
        // Helper method to filter categories by type
        Runnable updateCategoryCombo = () -> {
            String selectedType = typeCombo.getValue();
            Category.Type categoryType = selectedType.equals("Income") ? Category.Type.INCOME : Category.Type.EXPENSE;
            Category currentSelection = categoryCombo.getValue();
            categoryCombo.getItems().clear();
            for (Category cat : allCategories) {
                if (cat.getType() == categoryType) {
                    categoryCombo.getItems().add(cat);
                }
            }
            // Try to keep current selection if still valid
            if (currentSelection != null && categoryCombo.getItems().contains(currentSelection)) {
                categoryCombo.setValue(currentSelection);
            } else if (!categoryCombo.getItems().isEmpty()) {
                categoryCombo.setValue(categoryCombo.getItems().get(0));
            }
        };
        
        // Set up listener for type changes
        typeCombo.setOnAction(e -> updateCategoryCombo.run());
        
        categoryCombo.setConverter(new javafx.util.StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getName() : "";
            }
            @Override
            public Category fromString(String string) {
                return null;
            }
        });
        
        // Initialize categories based on transaction's type
        updateCategoryCombo.run();
        
        // Set the current category
        for (Category cat : categoryCombo.getItems()) {
            if (cat.getId().equals(transaction.getCategoryId())) {
                categoryCombo.setValue(cat);
                break;
            }
        }
        
        ComboBox<Wallet> accountCombo = new ComboBox<>();
        List<Wallet> wallets = dataStore.getWallets();
        accountCombo.getItems().addAll(wallets);
        accountCombo.setConverter(new javafx.util.StringConverter<Wallet>() {
            @Override
            public String toString(Wallet wallet) {
                return wallet != null ? wallet.getName() : "";
            }
            @Override
            public Wallet fromString(String string) {
                return null;
            }
        });
        for (Wallet wallet : wallets) {
            if (wallet.getId().equals(transaction.getWalletId())) {
                accountCombo.setValue(wallet);
                break;
            }
        }
        
        DatePicker datePicker = new DatePicker();
        try {
            String dateStr = transaction.getCreateTime().split(" ")[0];
            datePicker.setValue(java.time.LocalDate.parse(dateStr));
        } catch (Exception e) {
            datePicker.setValue(java.time.LocalDate.now());
        }
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(typeCombo, 1, 2);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(categoryCombo, 1, 3);
        grid.add(new Label("Account:"), 0, 4);
        grid.add(accountCombo, 1, 4);
        grid.add(new Label("Date:"), 0, 5);
        grid.add(datePicker, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Revert old balance
                    updateWalletBalance(transaction.getWalletId(), transaction.getAmount(), 
                                      transaction.getIncome() != 1.0);
                    
                    transaction.setName(nameField.getText());
                    transaction.setAmount(Double.parseDouble(amountField.getText()));
                    transaction.setIncome(typeCombo.getValue().equals("Income") ? 1.0 : 0.0);
                    transaction.setCategoryId(categoryCombo.getValue().getId());
                    transaction.setWalletId(accountCombo.getValue().getId());
                    transaction.setCreateTime(datePicker.getValue().atStartOfDay()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    
                    return transaction;
                } catch (Exception e) {
                    showAlert("Invalid input", "Please check your input values.");
                    return null;
                }
            }
            return null;
        });
        
        Optional<Transaction> result = dialog.showAndWait();
        result.ifPresent(updatedTransaction -> {
            // Apply new balance
            updateWalletBalance(updatedTransaction.getWalletId(), updatedTransaction.getAmount(), 
                              updatedTransaction.getIncome() == 1.0);
            
            dataStore.updateTransaction(updatedTransaction);
            loadTransactions();
            updateBalanceDisplay();
        });
    }
    
    private void handleDeleteTransaction(Transaction transaction) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Transaction");
        alert.setHeaderText("Are you sure you want to delete this transaction?");
        alert.setContentText(transaction.getName() + " - $" + transaction.getAmount());
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Revert wallet balance
            updateWalletBalance(transaction.getWalletId(), transaction.getAmount(), 
                              transaction.getIncome() != 1.0);
            
            dataStore.deleteTransaction(transaction.getId());
            loadTransactions();
            updateBalanceDisplay();
        }
    }
    
    private void updateWalletBalance(String walletId, double amount, boolean isIncome) {
        Wallet wallet = dataStore.getWalletById(walletId);
        if (wallet != null) {
            double newBalance = wallet.getBalance() + (isIncome ? amount : -amount);
            wallet.setBalance(newBalance);
            dataStore.updateWallet(wallet);
        }
    }
    
    private void loadTransactions() {
        List<Transaction> transactions = dataStore.getTransactions();
        transactionData.clear();
        transactionData.addAll(transactions);
        updateBalanceDisplay();
    }
    
    private void updateBalanceDisplay() {
        double income = dataStore.getTotalIncome();
        double expenses = dataStore.getTotalExpenses();
        double balance = income - expenses;
        
        balanceLabel.setText(String.format("Balance: $%.2f", balance));
        
        if (balance >= 0) {
            balanceLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #10b981;");
        } else {
            balanceLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ef4444;");
        }
    }
    
    private String getCategoryName(String categoryId) {
        if (categoryId == null) return "Unknown";
        List<Category> categories = categoryService.getAllCategories();
        for (Category category : categories) {
            if (category.getId().equals(categoryId)) {
                return category.getName();
            }
        }
        return "Unknown";
    }
    
    private String getWalletName(String walletId) {
        if (walletId == null) return "Unknown";
        Wallet wallet = dataStore.getWalletById(walletId);
        return wallet != null ? wallet.getName() : "Unknown";
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
