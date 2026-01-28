package gitgud.pfm.services;

import java.util.List;

import gitgud.pfm.Models.*;

public class AccountDataLoader {

    public static class DataHolder {
        private List<Budget> budgets;
        private List<Goal> goals;
        private List<Transaction> transactions;
        private gitgud.pfm.Models.Account account;

        public List<Budget> getBudgets() {
            return budgets;
        }

        public void setBudgets(List<Budget> budgets) {
            this.budgets = budgets;
        }

        public List<Goal> getGoals() {
            return goals;
        }

        public void setGoals(List<Goal> goals) {
            this.goals = goals;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<Transaction> transactions) {
            this.transactions = transactions;
        }

        public gitgud.pfm.Models.Account getAccount() {
            return account;
        }

        public void setAccount(gitgud.pfm.Models.Account account) {
            this.account = account;
        }

    }

    public static DataHolder loadAccountData(String accountID) {
        DataHolder data = new DataHolder();

        // Budgets: read all budgets (public data)
        try {
            BudgetService budgetService = new BudgetService();
            data.budgets = budgetService.readAll();
        } catch (Exception e) {
            System.err.println("Warning: failed to read budgets: " + e.getMessage());
            data.budgets = new java.util.ArrayList<>();
        }

        // Transactions: read all transactions where AccountID = accountID
        try {
            TransactionService txService = new TransactionService();
            data.transactions = txService.readByAccount(accountID);
        } catch (Exception e) {
            System.err.println("Warning: failed to read transactions: " + e.getMessage());
            data.transactions = new java.util.ArrayList<>();
        }

        // Account: read Account by primary key 'AccountID'
        try {
            AccountService accountService = new AccountService();
            gitgud.pfm.Models.Account account = accountService.read(accountID);
            data.setAccount(account);
        } catch (Exception e) {
            System.err.println("Warning: failed to read Account with AccountID = " + accountID + ": " + e.getMessage());
            data.setAccount(null);
        }

        // Goals: read all goals (public data)
        try {
            GoalService goalService = new GoalService();
            data.goals = goalService.readAll();
        } catch (Exception e) {
            System.err.println("Warning: failed to read goals: " + e.getMessage());
            data.goals = new java.util.ArrayList<>();
        }

        return data;
    }
}
