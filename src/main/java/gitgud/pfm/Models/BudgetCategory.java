package gitgud.pfm.Models;

/**
 * BudgetCategory - Junction table entity for Budget-Category many-to-many relationship
 * Represents both the database entity and calculated spending information
 */
public class BudgetCategory {
    // Database fields
    private String budgetId;
    private String categoryId;
    private Double categoryLimit; // Optional: specific limit for this category within the budget
    
    // Calculated/display fields (not persisted)
    private String categoryName;
    private double spentAmount;
    private double remainingAmount;
    private double percentageUsed;
    
    public BudgetCategory() {
    }
    
    public BudgetCategory(String budgetId, String categoryId) {
        this.budgetId = budgetId;
        this.categoryId = categoryId;
        this.categoryLimit = null;
    }
    
    public BudgetCategory(String budgetId, String categoryId, Double categoryLimit) {
        this.budgetId = budgetId;
        this.categoryId = categoryId;
        this.categoryLimit = categoryLimit;
    }
    
    /**
     * Constructor with spending calculation
     */
    public BudgetCategory(String budgetId, String categoryId, String categoryName, 
                         Double categoryLimit, double spentAmount) {
        this.budgetId = budgetId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryLimit = categoryLimit;
        this.spentAmount = spentAmount;
        calculateDerivedValues();
    }
    
    /**
     * Calculate remaining amount and percentage based on spent and limit
     */
    public void calculateDerivedValues() {
        if (categoryLimit != null && categoryLimit > 0) {
            this.remainingAmount = categoryLimit - spentAmount;
            this.percentageUsed = (spentAmount / categoryLimit) * 100.0;
        } else {
            this.remainingAmount = 0;
            this.percentageUsed = 0;
        }
    }
    
    // Getters and Setters
    public String getBudgetId() {
        return budgetId;
    }
    
    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }
    
    public String getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public Double getCategoryLimit() {
        return categoryLimit;
    }
    
    public void setCategoryLimit(Double categoryLimit) {
        this.categoryLimit = categoryLimit;
    }
    
    public double getSpentAmount() {
        return spentAmount;
    }
    
    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
        calculateDerivedValues();
    }
    
    public double getRemainingAmount() {
        return remainingAmount;
    }
    
    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
    
    public double getPercentageUsed() {
        return percentageUsed;
    }
    
    public void setPercentageUsed(double percentageUsed) {
        this.percentageUsed = percentageUsed;
    }
    
    public boolean isOverBudget() {
        return categoryLimit != null && spentAmount > categoryLimit;
    }
    
    @Override
    public String toString() {
        if (categoryName != null) {
            // Display format with spending info
            return "BudgetCategory{" +
                    "budgetId='" + budgetId + '\'' +
                    ", categoryId='" + categoryId + '\'' +
                    ", categoryName='" + categoryName + '\'' +
                    ", categoryLimit=" + categoryLimit +
                    ", spentAmount=" + spentAmount +
                    ", remainingAmount=" + remainingAmount +
                    ", percentageUsed=" + String.format("%.2f", percentageUsed) + "%" +
                    ", overBudget=" + isOverBudget() +
                    '}';
        } else {
            // Simple format for database entity
            return "BudgetCategory{" +
                    "budgetId='" + budgetId + '\'' +
                    ", categoryId='" + categoryId + '\'' +
                    ", categoryLimit=" + categoryLimit +
                    '}';
        }
    }
}
