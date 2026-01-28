package gitgud.pfm.services;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * Database Initializer - Ensures required tables exist
 * ═══════════════════════════════════════════════════════════════════════════════
 * 
 * Creates all required tables if they don't already exist. This is called
 * automatically when the Database singleton is initialized.
 * 
 * Tables created:
 * - Budget: Financial budget tracking with category limits
 * - Goal: Savings/financial goals with deadlines
 * - Wallets: Account/wallet management (renamed from Accounts conceptually)
 * - transaction_records: Individual transaction records
 * 
 * ═══════════════════════════════════════════════════════════════════════════════
 */
public class DatabaseInitializer {

    /**
     * Initialize the database by creating all required tables if they don't exist.
     * Called automatically on first Database connection.
     *
     * @param connection The active database connection
     * @throws SQLException If table creation fails
     */
    public static void initializeDatabase(Connection connection) throws SQLException {
        if (connection == null) {
            throw new SQLException("Cannot initialize database: connection is null");
        }

        try (Statement statement = connection.createStatement()) {
            // Create Budget table
            if (!tableExists(connection, "Budget")) {
                String createBudgetSQL = """
                    CREATE TABLE "Budget" (
                        "id"  TEXT NOT NULL,
                        "name"  TEXT,
                        "limits"  NUMERIC,
                        "balance"  NUMERIC,
                        "start_date"  TEXT,
                        "end_date"  TEXT,
                        "trackedCategories"  TEXT,
                        PRIMARY KEY("id")
                    )
                    """;
                statement.execute(createBudgetSQL);
                System.out.println("✓ Created table: Budget");
            }

            // Create Goal table
            if (!tableExists(connection, "Goal")) {
                String createGoalSQL = """
                    CREATE TABLE "Goal" (
                        "id"  TEXT NOT NULL,
                        "name"  TEXT,
                        "target"  NUMERIC,
                        "balance"  NUMERIC,
                        "deadline"  TEXT,
                        "priority"  NUMERIC,
                        "createAt"  TEXT,
                        PRIMARY KEY("id")
                    )
                    """;
                statement.execute(createGoalSQL);
                System.out.println("✓ Created table: Goal");
            }

            // Create Account table
            if (!tableExists(connection, "Account")) {
                String createAccountSQL = """
                    CREATE TABLE "Account" (
                        "accountID"  TEXT NOT NULL,
                        "name"  TEXT,
                        "balance"  NUMERIC,
                        "color"  TEXT,
                        PRIMARY KEY("accountID")
                    )
                    """;
                statement.execute(createAccountSQL);
                System.out.println("✓ Created table: Account");
            }

            // Create transaction_records table
            if (!tableExists(connection, "transaction_records")) {
                String createTransactionSQL = """
                    CREATE TABLE "transaction_records" (
                        "ID"  TEXT NOT NULL,
                        "Categories"  TEXT,
                        "Amount"  NUMERIC,
                        "Name"  TEXT,
                        "Income"  NUMERIC,
                        "AccountID"  TEXT,
                        "Create_time"  TEXT,
                        PRIMARY KEY("ID"),
                        FOREIGN KEY("AccountID") REFERENCES "Account"("accountID")
                    )
                    """;
                statement.execute(createTransactionSQL);
                System.out.println("✓ Created table: transaction_records");
            }

            // Create Category table (referenced by transaction Categories field)
            if (!tableExists(connection, "Category")) {
                String createCategorySQL = """
                    CREATE TABLE "Category" (
                        "id"  TEXT NOT NULL,
                        "name"  TEXT NOT NULL,
                        "description"  TEXT,
                        "color"  TEXT,
                        PRIMARY KEY("id")
                    )
                    """;
                statement.execute(createCategorySQL);
                System.out.println("✓ Created table: Category");
            }

            System.out.println("Database initialization complete.");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Check if a table exists in the database.
     *
     * @param connection The database connection
     * @param tableName  The name of the table to check
     * @return true if the table exists, false otherwise
     * @throws SQLException If the check fails
     */
    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return tables.next();
        }
    }

    /**
     * Drop all tables in the database (useful for testing/reset).
     * WARNING: This is destructive and will remove all data.
     *
     * @param connection The database connection
     * @throws SQLException If the operation fails
     */
    public static void dropAllTables(Connection connection) throws SQLException {
        if (connection == null) {
            throw new SQLException("Cannot drop tables: connection is null");
        }

        String[] tableNames = {"transaction_records", "Budget", "Goal", "Account", "Category"};

        try (Statement statement = connection.createStatement()) {
            for (String tableName : tableNames) {
                if (tableExists(connection, tableName)) {
                    statement.execute("DROP TABLE \"" + tableName + "\"");
                    System.out.println("✓ Dropped table: " + tableName);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error dropping tables: " + e.getMessage());
            throw e;
        }
    }
}
