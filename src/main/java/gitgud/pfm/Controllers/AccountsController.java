package gitgud.pfm.Controllers;

import gitgud.pfm.GUI.data.DataStore;
import gitgud.pfm.Models.Wallet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AccountsController implements Initializable {

    @FXML private ScrollPane rootPane;
    @FXML private VBox mainContent;
    @FXML private Button addAccountButton;
    @FXML private HBox summarySection;
    @FXML private Label totalAssetsLabel;
    @FXML private Label totalLiabilitiesLabel;
    @FXML private Label netWorthLabel;
    @FXML private VBox accountsList;

    private DataStore dataStore;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataStore = DataStore.getInstance();
        
        addAccountButton.setOnAction(e -> showAddAccountDialog());
        
        loadAccounts();
        updateSummary();
    }

    private void updateSummary() {
        List<Wallet> wallets = dataStore.getWallets();

        double totalBalance = wallets.stream().mapToDouble(Wallet::getBalance).sum();
        double totalAssets = Math.max(0, totalBalance);
        double totalLiabilities = Math.abs(Math.min(0, totalBalance));

        totalAssetsLabel.setText(String.format("$%.2f", totalAssets));
        totalLiabilitiesLabel.setText(String.format("$%.2f", totalLiabilities));
        netWorthLabel.setText(String.format("$%.2f", totalBalance));
    }

    private void loadAccounts() {
        accountsList.getChildren().clear();
        List<Wallet> wallets = dataStore.getWallets();
        
        for (Wallet wallet : wallets) {
            HBox accountCard = createAccountCard(wallet);
            accountsList.getChildren().add(accountCard);
        }

        if (wallets.isEmpty()) {
            Label emptyLabel = new Label("No accounts yet. Click 'Add Account' to create one.");
            emptyLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
            accountsList.getChildren().add(emptyLabel);
        }
    }

    private HBox createAccountCard(Wallet wallet) {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12;");

        Region avatar = new Region();
        avatar.setPrefSize(56, 56);
        avatar.setMinSize(56, 56);
        avatar.setStyle("-fx-background-color: #3b82f6; -fx-background-radius: 12;");

        VBox details = new VBox(4);
        HBox.setHgrow(details, Priority.ALWAYS);

        Label name = new Label(wallet.getName());
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");

        Label type = new Label(wallet.getColor() != null ? wallet.getColor() : "wallet");
        type.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

        details.getChildren().addAll(name, type);

        VBox balanceBox = new VBox(4);
        balanceBox.setAlignment(Pos.CENTER_RIGHT);

        String balanceColor = wallet.getBalance() >= 0 ? "#22c55e" : "#ef4444";
        Label balance = new Label(String.format("$%.2f", Math.abs(wallet.getBalance())));
        balance.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: " + balanceColor + ";");

        Label balanceLabel = new Label(wallet.getBalance() >= 0 ? "Available" : "Due");
        balanceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

        balanceBox.getChildren().addAll(balance, balanceLabel);

        card.getChildren().addAll(avatar, details, balanceBox);
        return card;
    }

    @FXML
    private void showAddAccountDialog() {
        Dialog<Wallet> dialog = new Dialog<>();
        dialog.setTitle("Add New Wallet");
        dialog.setHeaderText("Enter wallet details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Wallet name");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("bank", "cash", "credit");
        typeBox.setValue("bank");

        TextField balanceField = new TextField();
        balanceField.setPromptText("0.00");
        balanceField.setText("0");

        grid.add(new Label("Wallet Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Wallet Type:"), 0, 1);
        grid.add(typeBox, 1, 1);
        grid.add(new Label("Initial Balance:"), 0, 2);
        grid.add(balanceField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    double balance = Double.parseDouble(balanceField.getText());
                    return new Wallet(typeBox.getValue(), balance, nameField.getText());
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Invalid balance!");
                    alert.show();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(wallet -> {
            dataStore.addWallet(wallet);
            refresh();
        });
    }

    public void refresh() {
        loadAccounts();
        updateSummary();
    }
}
