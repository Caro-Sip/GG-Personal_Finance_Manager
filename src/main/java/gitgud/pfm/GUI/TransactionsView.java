package gitgud.pfm.GUI;

import gitgud.pfm.GUI.data.DataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class TransactionsView extends BorderPane {
    
    private DataStore dataStore;
    
    public TransactionsView() {
        dataStore = DataStore.getInstance();
        
        setStyle("-fx-background-color: #f0f2f5;");
        setPadding(new Insets(28));
        
        HBox header = createHeader();
        setTop(header);
        
        Label content = new Label("Transactions View - Coming Soon");
        content.setStyle("-fx-font-size: 18px; -fx-text-fill: #1e293b;");
        setCenter(content);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 24, 0));
        
        Label title = new Label("All Transactions");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        header.getChildren().add(title);
        return header;
    }
}
