package me.meng.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Database implements AutoCloseable {
  private final HikariDataSource dataSource;

  public Database(String sqliteFilePath) {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:sqlite:" + sqliteFilePath);
    // SQLite only allows one writer at a time; a bigger pool just adds
    // lock-contention retries for no benefit.
    config.setMaximumPoolSize(1);
    this.dataSource = new HikariDataSource(config);
    initSchema();
  }

  public HikariDataSource getDataSource() {
    return dataSource;
  }

  private void initSchema() {
    String accounts =
        """
                CREATE TABLE IF NOT EXISTS accounts (
                    account_number TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    password TEXT NOT NULL,
                    type TEXT NOT NULL,
                    balance REAL NOT NULL,
                    daily_limit REAL NOT NULL
                )
                """;
    String cards =
        """
                CREATE TABLE IF NOT EXISTS cards (
                    card_number TEXT PRIMARY KEY,
                    account_number TEXT NOT NULL,
                    pin TEXT NOT NULL,
                    failed_attempts INTEGER NOT NULL,
                    locked INTEGER NOT NULL
                )
                """;
    String transactions =
        """
                CREATE TABLE IF NOT EXISTS transactions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    account_number TEXT NOT NULL,
                    owner_name TEXT NOT NULL,
                    type TEXT NOT NULL,
                    amount REAL NOT NULL,
                    new_balance REAL NOT NULL,
                    created_at TEXT NOT NULL DEFAULT (datetime('now'))
                )
                """;
    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(accounts);
      stmt.execute(cards);
      stmt.execute(transactions);
    } catch (SQLException e) {
      throw new RuntimeException("Failed to initialize database schema", e);
    }
  }

  @Override
  public void close() {
    dataSource.close();
  }
}
