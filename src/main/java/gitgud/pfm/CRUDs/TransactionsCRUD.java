package gitgud.pfm.CRUDs;

import gitgud.pfm.interfaces.CRUD;
import gitgud.pfm.Models.Transactions;
import gitgud.pfm.services.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionsCRUD implements CRUD<Transactions> {
    private static TransactionsCRUD instance = null;
    private final Database database;

    private TransactionsCRUD() {
        this.database = Database.getInstance();
    }

    public static TransactionsCRUD getInstance() {
        if (instance == null) {
            synchronized (TransactionsCRUD.class) {
                if (instance == null) {
                    instance = new TransactionsCRUD();
                }
            }
        }
        return instance;
    }

    @Override
    public void create(Transactions entity) {
        // TODO: Implement SQL INSERT
        // Example: INSERT INTO transactions (type, title, category, amount, account_id, to_account_id, time) VALUES (?, ?, ?, ?, ?, ?, ?)
    }

    @Override
    public Transactions read(String id) {
        // TODO: Implement SQL SELECT
        // Example: SELECT * FROM transactions WHERE id = ?
        return null;
    }

    @Override
    public void update(Transactions entity) {
        // TODO: Implement SQL UPDATE
        // Example: UPDATE transactions SET ... WHERE id = ?
    }

    @Override
    public void delete(String id) {
        // TODO: Implement SQL DELETE
        // Example: DELETE FROM transactions WHERE id = ?
    }

}
