package gitgud.pfm.GUI;

import gitgud.pfm.GUI.data.DataStore;
import gitgud.pfm.Models.Goal;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class GoalsView extends ScrollPane {
    
    private DataStore dataStore;
    private VBox mainContent;
    private VBox goalsList;
    
    public GoalsView() {
        dataStore = DataStore.getInstance();
        
        mainContent = new VBox(24);
        mainContent.setPadding(new Insets(28));
        mainContent.setStyle("-fx-background-color: #f0f2f5;");
        
        HBox header = createHeader();
        VBox goalsCard = createGoalsCard();
        
        mainContent.getChildren().addAll(header, goalsCard);
        
        setContent(mainContent);
        setFitToWidth(true);
        setStyle("-fx-background-color: #f0f2f5; -fx-background: #f0f2f5;");
        
        loadGoals();
    }
    
    private HBox createHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("Financial Goals");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addButton = new Button("Add Goal");
        addButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; " +
                "-fx-padding: 10 20; -fx-font-size: 14px; -fx-font-weight: 600; " +
                "-fx-border-radius: 6; -fx-background-radius: 6;");
        addButton.setOnAction(e -> showAddGoalDialog());
        
        header.getChildren().addAll(title, spacer, addButton);
        return header;
    }
    
    private VBox createGoalsCard() {
        VBox card = new VBox(16);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 3, 0, 0, 1);");
        card.setPadding(new Insets(24));
        
        Label cardTitle = new Label("All Goals");
        cardTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");
        
        goalsList = new VBox(12);
        
        card.getChildren().addAll(cardTitle, goalsList);
        return card;
    }
    
    private void loadGoals() {
        goalsList.getChildren().clear();
        List<Goal> goals = dataStore.getGoals();
        
        for (Goal goal : goals) {
            VBox goalCard = createGoalCard(goal);
            goalsList.getChildren().add(goalCard);
        }
    }
    
    private VBox createGoalCard(Goal goal) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; " +
                "-fx-border-color: #e2e8f0; -fx-border-radius: 12;");
        
        HBox titleBox = new HBox(12);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label name = new Label(goal.getName());
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");
        
        Label priority = new Label("Priority: " + (int)goal.getPriority());
        priority.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label deadline = new Label(goal.getDeadline() != null ? goal.getDeadline() : "No deadline");
        deadline.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
        
        titleBox.getChildren().addAll(name, spacer, priority, deadline);
        
        // Progress bar
        double progress = goal.getTarget() > 0 ? goal.getBalance() / goal.getTarget() : 0;
        ProgressBar progressBar = new ProgressBar(Math.min(1.0, progress));
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.setStyle("-fx-control-inner-background: #e2e8f0;");
        
        HBox progressBox = new HBox(12);
        progressBox.setAlignment(Pos.CENTER_LEFT);
        progressBox.getChildren().add(progressBar);
        
        Label progressLabel = new Label(String.format("$%.2f / $%.2f", goal.getBalance(), goal.getTarget()));
        progressLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        progressBox.getChildren().add(progressLabel);
        
        card.getChildren().addAll(titleBox, progressBox);
        return card;
    }
    
    private void showAddGoalDialog() {
        Dialog<Goal> dialog = new Dialog<>();
        dialog.setTitle("Add New Goal");
        dialog.setHeaderText("Enter goal details");
        
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Goal name");
        
        TextField targetField = new TextField();
        targetField.setPromptText("Target amount");
        targetField.setText("1000");
        
        TextField deadlineField = new TextField();
        deadlineField.setPromptText("Deadline (YYYY-MM-DD)");
        
        Spinner<Integer> prioritySpinner = new Spinner<>(1, 10, 5);
        
        grid.add(new Label("Goal Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Target Amount:"), 0, 1);
        grid.add(targetField, 1, 1);
        grid.add(new Label("Deadline:"), 0, 2);
        grid.add(deadlineField, 1, 2);
        grid.add(new Label("Priority:"), 0, 3);
        grid.add(prioritySpinner, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    double target = Double.parseDouble(targetField.getText());
                    return new Goal(nameField.getText(), target, 0, 
                                  deadlineField.getText(), prioritySpinner.getValue(), 
                                  java.time.LocalDateTime.now().toString());
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Invalid input!");
                    alert.show();
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(goal -> {
            dataStore.addGoal(goal);
            loadGoals();
        });
    }
}
