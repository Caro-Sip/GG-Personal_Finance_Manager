package gitgud.pfm.Controllers;

import gitgud.pfm.GUI.data.DataStore;
import gitgud.pfm.Models.Goal;
import gitgud.pfm.Models.Transaction;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {

    @FXML private ScrollPane rootPane;
    @FXML private Label totalSpentLabel;
    @FXML private Label goalPercentLabel;
    @FXML private ProgressBar goalProgress;
    @FXML private Label goalHintLabel;
    @FXML private VBox priorityGoalsList;
    @FXML private ComboBox<String> periodSelect;
    @FXML private LineChart<Number, Number> spendingChart;
    @FXML private VBox transactionsList;
    @FXML private Hyperlink viewAllTransactions;

    private DataStore dataStore;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataStore = DataStore.getInstance();
        
        updateBudgetGoal();
        loadPriorityGoals();
        loadSpendingChart();
        loadRecentTransactions();
        
        // Period selector listener
        if (periodSelect != null) {
            periodSelect.setOnAction(e -> loadSpendingChart());
        }
    }

    private void updateBudgetGoal() {
        double budgetLimit = 3000.0;
        double totalSpent = dataStore.getTotalExpenses();
        
        double percent = Math.min(100, (totalSpent / budgetLimit) * 100);
        double remaining = Math.max(0, budgetLimit - totalSpent);
        
        totalSpentLabel.setText(String.format("$%.2f", totalSpent));
        goalPercentLabel.setText(String.format("%.0f%%", percent));
        goalProgress.setProgress(percent / 100.0);
        goalHintLabel.setText(String.format("$%.2f remaining this month", remaining));
    }

    private void loadPriorityGoals() {
        priorityGoalsList.getChildren().clear();
        
        List<Goal> priorityGoals = dataStore.getGoals().stream()
                .filter(g -> g.getPriority() > 5 && g.getBalance() < g.getTarget())
                .collect(Collectors.toList());

        for (Goal goal : priorityGoals) {
            HBox goalItem = createPriorityGoalItem(goal);
            priorityGoalsList.getChildren().add(goalItem);
        }

        if (priorityGoals.isEmpty()) {
            Label emptyLabel = new Label("No priority goals");
            emptyLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
            priorityGoalsList.getChildren().add(emptyLabel);
        }
    }

    private HBox createPriorityGoalItem(Goal goal) {
        HBox item = new HBox(16);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(16));
        item.setStyle("-fx-background-color: linear-gradient(to right, #fffbeb, #fef3c7); " +
                     "-fx-background-radius: 12; -fx-border-color: #f59e0b; " +
                     "-fx-border-width: 0 0 0 4; -fx-border-radius: 12;");

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label titleLabel = new Label(goal.getName());
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");

        HBox meta = new HBox(8);
        meta.setAlignment(Pos.CENTER_LEFT);

        Label amounts = new Label(String.format("$%.2f / $%.2f", goal.getBalance(), goal.getTarget()));
        amounts.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        Label dot = new Label("•");
        dot.setStyle("-fx-text-fill: #64748b;");

        String deadlineStr = goal.getDeadline() != null ? goal.getDeadline() : "No deadline";
        Label deadline = new Label(deadlineStr);
        deadline.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        meta.getChildren().addAll(amounts, dot, deadline);
        info.getChildren().addAll(titleLabel, meta);

        HBox progressBox = new HBox(12);
        progressBox.setAlignment(Pos.CENTER_LEFT);
        progressBox.setPrefWidth(140);

        double progressPercent = goal.getTarget() > 0 ? (goal.getBalance() / goal.getTarget()) * 100 : 0;
        ProgressBar miniProgress = new ProgressBar(Math.min(1.0, progressPercent / 100.0));
        miniProgress.setPrefHeight(8);
        miniProgress.setPrefWidth(80);
        miniProgress.setStyle("-fx-accent: #f59e0b;");

        Label percent = new Label(String.format("%.0f%%", progressPercent));
        percent.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");

        progressBox.getChildren().addAll(miniProgress, percent);

        item.getChildren().addAll(info, progressBox);
        return item;
    }

    private void loadSpendingChart() {
        spendingChart.getData().clear();

        XYChart.Series<Number, Number> thisMonth = new XYChart.Series<>();
        thisMonth.setName("This Month");
        thisMonth.getData().add(new XYChart.Data<>(1, 350));
        thisMonth.getData().add(new XYChart.Data<>(2, 420));
        thisMonth.getData().add(new XYChart.Data<>(3, 380));
        thisMonth.getData().add(new XYChart.Data<>(4, 450));

        XYChart.Series<Number, Number> lastMonth = new XYChart.Series<>();
        lastMonth.setName("Last Month");
        lastMonth.getData().add(new XYChart.Data<>(1, 300));
        lastMonth.getData().add(new XYChart.Data<>(2, 380));
        lastMonth.getData().add(new XYChart.Data<>(3, 340));
        lastMonth.getData().add(new XYChart.Data<>(4, 400));

        spendingChart.getData().addAll(thisMonth, lastMonth);
    }

    private void loadRecentTransactions() {
        transactionsList.getChildren().clear();

        List<Transaction> transactions = dataStore.getTransactions().stream()
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .limit(10)
                .collect(Collectors.toList());

        for (Transaction tx : transactions) {
            HBox txItem = createTransactionItem(tx);
            transactionsList.getChildren().add(txItem);
        }
    }

    private HBox createTransactionItem(Transaction tx) {
        HBox item = new HBox();
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(14, 0, 14, 0));
        item.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");

        StackPane icon = createTransactionIcon(tx.getCategoryId());

        VBox details = new VBox(3);
        HBox.setHgrow(details, Priority.ALWAYS);
        HBox.setMargin(details, new Insets(0, 14, 0, 14));

        Label titleLabel = new Label(tx.getName());
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 500; -fx-text-fill: #1e293b;");

        Label timeLabel = new Label(tx.getCreateTime());
        timeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        details.getChildren().addAll(titleLabel, timeLabel);

        String sign = tx.getIncome() > 0 ? "+" : "-";
        String color = tx.getIncome() > 0 ? "#22c55e" : "#ef4444";

        Label amount = new Label(sign + String.format("$%.2f", tx.getAmount()));
        amount.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: " + color + ";");

        item.getChildren().addAll(icon, details, amount);
        return item;
    }

    private StackPane createTransactionIcon(String category) {
        StackPane pane = new StackPane();
        pane.setPrefSize(44, 44);

        String bgColor, emoji;

        switch (category != null ? category : "") {
            case "food":
                emoji = "🍴";
                bgColor = "#fef3c7";
                break;
            case "transport":
                emoji = "🚗";
                bgColor = "#dbeafe";
                break;
            case "shopping":
                emoji = "🛍";
                bgColor = "#fce7f3";
                break;
            case "bills":
                emoji = "📄";
                bgColor = "#fee2e2";
                break;
            case "income":
                emoji = "↓";
                bgColor = "#dcfce7";
                break;
            case "entertainment":
                emoji = "🎬";
                bgColor = "#ede9fe";
                break;
            default:
                emoji = "●";
                bgColor = "#f1f5f9";
        }

        pane.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 12;");

        Label iconView = new Label(emoji);
        iconView.setStyle("-fx-font-size: 20px;");

        pane.getChildren().add(iconView);
        return pane;
    }

    public void refresh() {
        updateBudgetGoal();
        loadPriorityGoals();
        loadSpendingChart();
        loadRecentTransactions();
    }
}
