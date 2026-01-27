package gitgud.pfm.CRUDs;

import gitgud.pfm.interfaces.CRUD;
import gitgud.pfm.Models.Budgets;
import gitgud.pfm.services.Database;

public class BudgetsCRUD implements CRUD<Budgets> {
    private static BudgetsCRUD instance = null;
    private final Database database;

    private BudgetsCRUD() {
        this.database = Database.getInstance();
    }

    public static BudgetsCRUD getInstance() {
        if (instance == null) {
            synchronized (BudgetsCRUD.class) {
                if (instance == null) {
                    instance = new BudgetsCRUD();
                }
            }
        }
        return instance;
    }

    @Override
    public void create(Budgets entity) {
        // TODO: Implement SQL INSERT
    }

    @Override
    public Budgets read(String id) {
        // TODO: Implement SQL SELECT
        return null;
    }

    @Override
    public void update(Budgets entity) {
        // TODO: Implement SQL UPDATE
    }

    @Override
    public void delete(String id) {
        // TODO: Implement SQL DELETE
    }
}