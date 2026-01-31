package gitgud.pfm.services;

import gitgud.pfm.Models.Budget;
import gitgud.pfm.Models.Category;
import gitgud.pfm.interfaces.CRUDInterface;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BudgetService - Explicit CRUD operations for Budget entity
 * All SQL queries explicitly show field mappings for clarity
 */
public class BudgetService implements CRUDInterface<Budget> {
    private final Connection connection;
    
    public BudgetService() {
        this.connection = Database.getInstance().getConnection();
    }
    
    /**
     * Create a new budget in the database
     * Explicit fields: id, name, limitAmount, balance, startDate, endDate
     */
    @Override
    public void create(Budget budget) {
        String sql = "INSERT INTO Budget (id, name, limitAmount, balance, startDate, endDate) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, budget.getId());
            pstmt.setString(2, budget.getName());
            pstmt.setDouble(3, budget.getLimitAmount());
            pstmt.setDouble(4, budget.getBalance());
            pstmt.setString(5, budget.getStartDate());
            pstmt.setString(6, budget.getEndDate());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating budget: " + e.getMessage());
        }
    }
    
    /**
     * Read a single budget by id
     * Explicit fields: id, name, limitAmount, balance, startDate, endDate
     */
    @Override
    public Budget read(String id) {
        String sql = "SELECT id, name, limitAmount, balance, startDate, endDate " +
                     "FROM Budget WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Budget budget = new Budget();
                    budget.setId(rs.getString("id"));
                    budget.setName(rs.getString("name"));
                    budget.setLimitAmount(rs.getDouble("limitAmount"));
                    budget.setBalance(rs.getDouble("balance"));
                    budget.setStartDate(rs.getString("startDate"));
                    budget.setEndDate(rs.getString("endDate"));
                    return budget;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading budget: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Read all budgets from the database
     * Explicit fields: id, name, limitAmount, balance, startDate, endDate
     */
    public List<Budget> readAll() {
        String sql = "SELECT id, name, limitAmount, balance, startDate, endDate " +
                     "FROM Budget ORDER BY name";
        List<Budget> budgets = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Budget budget = new Budget();
                budget.setId(rs.getString("id"));
                budget.setName(rs.getString("name"));
                budget.setLimitAmount(rs.getDouble("limitAmount"));
                budget.setBalance(rs.getDouble("balance"));
                budget.setStartDate(rs.getString("startDate"));
                budget.setEndDate(rs.getString("endDate"));
                budgets.add(budget);
            }
        } catch (SQLException e) {
            System.err.println("Error reading all budgets: " + e.getMessage());
        }
        return budgets;
    }
    
    /**
     * Update an existing budget
     * Explicit fields: name, limitAmount, balance, startDate, endDate (WHERE id = ?)
     */
    @Override
    public void update(Budget budget) {
        String sql = "UPDATE Budget SET name = ?, limitAmount = ?, balance = ?, startDate = ?, " +
                     "endDate = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, budget.getName());
            pstmt.setDouble(2, budget.getLimitAmount());
            pstmt.setDouble(3, budget.getBalance());
            pstmt.setString(4, budget.getStartDate());
            pstmt.setString(5, budget.getEndDate());
            pstmt.setString(6, budget.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating budget: " + e.getMessage());
        }
    }
    
    /**
     * Delete a budget by id
     */
    @Override
    public void delete(String id) {
        String sql = "DELETE FROM Budget WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting budget: " + e.getMessage());
        }
    }
    
    /**
     * Get budgets that are currently active (current date within startDate and endDate)
     */
    public List<Budget> getActiveBudgets() {
        String sql = "SELECT id, name, limitAmount, balance, startDate, endDate " +
                     "FROM Budget WHERE date('now') BETWEEN startDate AND endDate " +
                     "ORDER BY name";
        List<Budget> budgets = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Budget budget = new Budget();
                budget.setId(rs.getString("id"));
                budget.setName(rs.getString("name"));
                budget.setLimitAmount(rs.getDouble("limitAmount"));
                budget.setBalance(rs.getDouble("balance"));
                budget.setStartDate(rs.getString("startDate"));
                budget.setEndDate(rs.getString("endDate"));
                budgets.add(budget);
            }
        } catch (SQLException e) {
            System.err.println("Error reading active budgets: " + e.getMessage());
        }
        return budgets;
    }

    /**
     * Get all categories associated with a specific budget
     * Explicit fields: c.id, c.name, c.description (via Budget_Category junction)
     */
    public List<Category> getCategoriesForBudget(String budgetId) {
        String sql = "SELECT DISTINCT c.id, c.name, c.description " +
                     "FROM Category c " +
                     "INNER JOIN Budget_Category bc ON c.id = bc.categoryID " +
                     "WHERE bc.budgetID = ? " +
                     "ORDER BY c.name";
        List<Category> categories = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, budgetId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getString("id"));
                    category.setName(rs.getString("name"));
                    category.setDescription(rs.getString("description"));
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting categories for budget: " + e.getMessage());
        }
        return categories;
    }

    /**
     * Get all budgets associated with a specific category
     * Explicit fields: b.id, b.name, b.limitAmount, b.balance, b.startDate, b.endDate (via Budget_Category junction)
     */
    public List<Budget> getBudgetsForCategory(String categoryId) {
        String sql = "SELECT DISTINCT b.id, b.name, b.limitAmount, b.balance, b.startDate, b.endDate " +
                     "FROM Budget b " +
                     "INNER JOIN Budget_Category bc ON b.id = bc.budgetID " +
                     "WHERE bc.categoryID = ? " +
                     "ORDER BY b.name";
        List<Budget> budgets = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, categoryId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Budget budget = new Budget();
                    budget.setId(rs.getString("id"));
                    budget.setName(rs.getString("name"));
                    budget.setLimitAmount(rs.getDouble("limitAmount"));
                    budget.setBalance(rs.getDouble("balance"));
                    budget.setStartDate(rs.getString("startDate"));
                    budget.setEndDate(rs.getString("endDate"));
                    budgets.add(budget);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting budgets for category: " + e.getMessage());
        }
        return budgets;
    }

    /**
     * Check if a category is linked to a budget
     * Returns true if the relationship exists in Budget_Category junction table
     */
    public boolean isCategoryInBudget(String budgetId, String categoryId) {
        String sql = "SELECT COUNT(*) FROM Budget_Category " +
                     "WHERE budgetID = ? AND categoryID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, budgetId);
            pstmt.setString(2, categoryId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking category in budget: " + e.getMessage());
        }
        return false;
    }

    /**
     * Add a category to a budget (INSERT into Budget_Category junction table)
     * Handles duplicate key errors gracefully
     */
    public void addCategoryToBudget(String budgetId, String categoryId) {
        String sql = "INSERT INTO Budget_Category (budgetID, categoryID) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, budgetId);
            pstmt.setString(2, categoryId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE") || e.getMessage().contains("PRIMARY KEY")) {
                System.out.println("Category already linked to budget.");
            } else {
                System.err.println("Error adding category to budget: " + e.getMessage());
            }
        }
    }

    /**
     * Remove a category from a budget (DELETE from Budget_Category junction table)
     */
    public void removeCategoryFromBudget(String budgetId, String categoryId) {
        String sql = "DELETE FROM Budget_Category WHERE budgetID = ? AND categoryID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, budgetId);
            pstmt.setString(2, categoryId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing category from budget: " + e.getMessage());
        }
    }

    /**
     * Replace all categories for a budget
     * Removes all existing category links and adds new ones (transactional)
     */
    public void setCategoriesForBudget(String budgetId, List<String> categoryIds) {
        try {
            connection.setAutoCommit(false);
            
            // Step 1: Delete all existing category links
            String deleteSql = "DELETE FROM Budget_Category WHERE budgetID = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, budgetId);
                deleteStmt.executeUpdate();
            }
            
            // Step 2: Insert new category links
            String insertSql = "INSERT INTO Budget_Category (budgetID, categoryID) VALUES (?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                for (String categoryId : categoryIds) {
                    insertStmt.setString(1, budgetId);
                    insertStmt.setString(2, categoryId);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }
            
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Error setting categories for budget: " + e.getMessage());
        }
    }

    /**
     * Remove all category links for a budget
     * Useful when deleting or resetting a budget's categories
     */
    public void removeAllCategoriesFromBudget(String budgetId) {
        String sql = "DELETE FROM Budget_Category WHERE budgetID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, budgetId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing all categories from budget: " + e.getMessage());
        }
    }
}
                