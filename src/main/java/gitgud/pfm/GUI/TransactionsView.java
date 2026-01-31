package gitgud.pfm.GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class TransactionsView extends BorderPane {
    
    public TransactionsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gitgud/pfm/transactions.fxml"));
            BorderPane root = loader.load();
            
            // Copy the loaded content to this BorderPane
            this.setTop(root.getTop());
            this.setCenter(root.getCenter());
            this.setBottom(root.getBottom());
            this.setLeft(root.getLeft());
            this.setRight(root.getRight());
            this.setStyle(root.getStyle());
            this.setPadding(root.getPadding());
        } catch (IOException e) {
            System.err.println("Error loading transactions.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
