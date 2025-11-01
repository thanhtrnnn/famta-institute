package com.famta.database;

/**
 * Utility entry point to initialize the SQL Server database using the bundled SQL scripts.
 */
public final class DatabaseBootstrapper {

    private DatabaseBootstrapper() {
    }

    public static void main(String[] args) {
        DatabaseManager manager = DatabaseManager.getInstance();
        try {
            manager.initializeSchemaIfNeeded();
            manager.seedSampleData();
            System.out.println("Database schema ensured and sample data applied successfully.");
        } finally {
            manager.closeConnection();
        }
    }
}
