package gitgud.pfm.Controllers;

import gitgud.pfm.GUI.data.DataStore;
import gitgud.pfm.Models.Transaction;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ReportsController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private VBox mainContent;
    @FXML private ComboBox<String> reportPeriodCombo;
    @FXML private Button exportButton;
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpensesLabel;
    @FXML private Label netSavingsLabel;
    @FXML private Label savingsRateLabel;
    @FXML private Label incomeChangeLabel;
    @FXML private Label expenseChangeLabel;
    @FXML private Label savingsChangeLabel;
    @FXML private Label rateChangeLabel;
    @FXML private LineChart<String, Number> incomeExpenseChart;
    @FXML private PieChart expensePieChart;
    @FXML private BarChart<String, Number> monthlyTrendsChart;
    @FXML private VBox categoryBreakdownList;

    private DataStore dataStore;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataStore = DataStore.getInstance();
        
        if (reportPeriodCombo != null) {
            reportPeriodCombo.setOnAction(e -> loadReportData());
        }
        
        if (exportButton != null) {
            exportButton.setOnAction(e -> exportReport());
        }
        
        loadReportData();
    }

    private void loadReportData() {
        updateSummaryCards();
        loadIncomeExpenseChart();
        loadExpensePieChart();
        loadMonthlyTrendsChart();
        loadCategoryBreakdown();
    }

    private void updateSummaryCards() {
        double totalIncome = dataStore.getTotalIncome();
        double totalExpenses = dataStore.getTotalExpenses();
        double netSavings = totalIncome - totalExpenses;
        double savingsRate = totalIncome > 0 ? (netSavings / totalIncome) * 100 : 0;

        totalIncomeLabel.setText(String.format("$%.2f", totalIncome));
        totalExpensesLabel.setText(String.format("$%.2f", totalExpenses));
        netSavingsLabel.setText(String.format("$%.2f", netSavings));
        savingsRateLabel.setText(String.format("%.1f%%", savingsRate));

        // Placeholder change labels
        incomeChangeLabel.setText("+0% from last period");
        expenseChangeLabel.setText("+0% from last period");
        savingsChangeLabel.setText("+0% from last period");
        rateChangeLabel.setText("+0% from last period");
    }

    private void loadIncomeExpenseChart() {
        incomeExpenseChart.getData().clear();

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        incomeSeries.getData().add(new XYChart.Data<>("Week 1", 800));
        incomeSeries.getData().add(new XYChart.Data<>("Week 2", 1200));
        incomeSeries.getData().add(new XYChart.Data<>("Week 3", 600));
        incomeSeries.getData().add(new XYChart.Data<>("Week 4", 900));

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");
        expenseSeries.getData().add(new XYChart.Data<>("Week 1", 350));
        expenseSeries.getData().add(new XYChart.Data<>("Week 2", 420));
        expenseSeries.getData().add(new XYChart.Data<>("Week 3", 380));
        expenseSeries.getData().add(new XYChart.Data<>("Week 4", 450));

        incomeExpenseChart.getData().addAll(incomeSeries, expenseSeries);
    }

    private void loadExpensePieChart() {
        expensePieChart.getData().clear();

        // Get transactions and group by category
        List<Transaction> transactions = dataStore.getTransactions();
        Map<String, Double> categoryTotals = transactions.stream()
                .filter(tx -> tx.getIncome() <= 0)
                .collect(Collectors.groupingBy(
                        tx -> tx.getCategoryId() != null ? tx.getCategoryId() : "Other",
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        if (categoryTotals.isEmpty()) {
            // Sample data if no transactions
            categoryTotals.put("Food", 450.0);
            categoryTotals.put("Transport", 200.0);
            categoryTotals.put("Shopping", 350.0);
            categoryTotals.put("Bills", 500.0);
            categoryTotals.put("Entertainment", 150.0);
        }

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
            expensePieChart.getData().add(slice);
        }
    }

    private void loadMonthlyTrendsChart() {
        monthlyTrendsChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Spending");
        series.getData().add(new XYChart.Data<>("Jan", 1200));
        series.getData().add(new XYChart.Data<>("Feb", 1400));
        series.getData().add(new XYChart.Data<>("Mar", 1100));
        series.getData().add(new XYChart.Data<>("Apr", 1350));
        series.getData().add(new XYChart.Data<>("May", 1250));
        series.getData().add(new XYChart.Data<>("Jun", 1500));

        monthlyTrendsChart.getData().add(series);
    }

    private void loadCategoryBreakdown() {
        categoryBreakdownList.getChildren().clear();

        // Get transactions and group by category
        List<Transaction> transactions = dataStore.getTransactions();
        Map<String, Double> categoryTotals = transactions.stream()
                .filter(tx -> tx.getIncome() <= 0)
                .collect(Collectors.groupingBy(
                        tx -> tx.getCategoryId() != null ? tx.getCategoryId() : "Other",
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        if (categoryTotals.isEmpty()) {
            // Sample data if no transactions
            categoryTotals.put("Food", 450.0);
            categoryTotals.put("Transport", 200.0);
            categoryTotals.put("Shopping", 350.0);
            categoryTotals.put("Bills", 500.0);
            categoryTotals.put("Entertainment", 150.0);
        }

        double total = categoryTotals.values().stream().mapToDouble(Double::doubleValue).sum();

        // Category colors
        Map<String, String> categoryColors = new HashMap<>();
        categoryColors.put("food", "#f59e0b");
        categoryColors.put("transport", "#3b82f6");
        categoryColors.put("shopping", "#ec4899");
        categoryColors.put("bills", "#ef4444");
        categoryColors.put("entertainment", "#8b5cf6");
        categoryColors.put("other", "#64748b");

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            HBox categoryRow = createCategoryRow(
                    entry.getKey(),
                    entry.getValue(),
                    total,
                    categoryColors.getOrDefault(entry.getKey().toLowerCase(), "#64748b")
            );
            categoryBreakdownList.getChildren().add(categoryRow);
        }
    }

    private HBox createCategoryRow(String category, double amount, double total, String color) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 0, 12, 0));
        row.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");

        // Color indicator
        Region colorIndicator = new Region();
        colorIndicator.setPrefSize(12, 12);
        colorIndicator.setMinSize(12, 12);
        colorIndicator.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 6;");

        // Category name
        Label nameLabel = new Label(category.substring(0, 1).toUpperCase() + category.substring(1));
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: #1e293b;");
        nameLabel.setPrefWidth(120);

        // Progress bar
        double percentage = total > 0 ? amount / total : 0;
        ProgressBar progressBar = new ProgressBar(percentage);
        progressBar.setPrefHeight(8);
        progressBar.setStyle("-fx-accent: " + color + ";");
        HBox.setHgrow(progressBar, Priority.ALWAYS);

        // Amount and percentage
        VBox amountBox = new VBox(2);
        amountBox.setAlignment(Pos.CENTER_RIGHT);
        amountBox.setPrefWidth(100);

        Label amountLabel = new Label(String.format("$%.2f", amount));
        amountLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");

        Label percentLabel = new Label(String.format("%.1f%%", percentage * 100));
        percentLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        amountBox.getChildren().addAll(amountLabel, percentLabel);

        row.getChildren().addAll(colorIndicator, nameLabel, progressBar, amountBox);
        return row;
    }

    private void exportReport() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Report");
        alert.setHeaderText("Export Feature");
        alert.setContentText("Report export functionality coming soon!");
        alert.show();
    }

    public void refresh() {
        loadReportData();
    }
}
