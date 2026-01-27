package gitgud.pfm.CRUDs;

import gitgud.pfm.interfaces.CRUD;
import gitgud.pfm.Models.Goals;
import gitgud.pfm.services.Database;

public class GoalsCRUD implements CRUD<Goals> {
    private static GoalsCRUD instance = null;
    private final Database database;

    private GoalsCRUD() {
        this.database = Database.getInstance();
    }

    public static GoalsCRUD getInstance() {
        if (instance == null) {
            synchronized (GoalsCRUD.class) {
                if (instance == null) {
                    instance = new GoalsCRUD();
                }
            }
        }
        return instance;
    }

    @Override
    public void create(Goals entity) {
        // TODO: Implement SQL INSERT
    }

    @Override
    public Goals read(String id) {
        // TODO: Implement SQL SELECT
        return null;
    }

    @Override
    public void update(Goals entity) {
        // TODO: Implement SQL UPDATE
    }

    @Override
    public void delete(String id) {
        // TODO: Implement SQL DELETE
    }
}