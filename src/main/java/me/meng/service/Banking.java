package me.meng.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import me.meng.exception.AccountNotFoundException;
import me.meng.exception.DailyLimitExceededException;
import me.meng.exception.InsufficientFundsException;
import me.meng.exception.InvalidAmountException;
import me.meng.model.Account;
import me.meng.model.CheckingAccount;
import me.meng.model.SavingAccount;
import me.meng.model.Transaction;

public class Banking {

  private final DataSource dataSource;
  ArrayList<Account> accounts = new ArrayList<Account>();
  int accountCounter = 1;

  public Banking(DataSource dataSource) {
    this.dataSource = dataSource;
    loadAccounts();
  }

  private void loadAccounts() {
    String sql = "SELECT account_number, name, password, type, balance FROM accounts";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        String id = rs.getString("account_number");
        String name = rs.getString("name");
        String password = rs.getString("password");
        String type = rs.getString("type");
        double balance = rs.getDouble("balance");

        Account acc =
            type.equalsIgnoreCase("savings")
                ? new SavingAccount(id, name, password, balance)
                : new CheckingAccount(id, name, password, balance);
        accounts.add(acc);

        int suffix = Integer.parseInt(id.substring("A00".length()));
        if (suffix >= accountCounter) {
          accountCounter = suffix + 1;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to load accounts from database", e);
    }
  }

  public Account createAccount(String name, double startingMoney, String password, String type) {
    String id = "A00" + accountCounter;
    accountCounter++;

    Account acc;
    if (type.equalsIgnoreCase("savings")) {
      acc = new SavingAccount(id, name, password, startingMoney);
    } else {
      acc = new CheckingAccount(id, name, password, startingMoney);
    }

    accounts.add(acc);
    insertAccount(acc, type);
    return acc;
  }

  public void addAccount(Account acc) {
    accounts.add(acc);
  }

  public List<Account> getAllAccounts() {
    return new ArrayList<>(accounts);
  }

  public Account findAccount(String accountNumber) throws AccountNotFoundException {
    for (int i = 0; i < accounts.size(); i++) {
      if (accounts.get(i).getAccountNumber().equals(accountNumber)) {
        return accounts.get(i);
      }
    }
    throw new AccountNotFoundException("Account is not found!");
  }

  private void insertAccount(Account acc, String type) {
    String sql =
        "INSERT INTO accounts (account_number, name, password, type, balance, daily_limit) "
            + "VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, acc.getAccountNumber());
      stmt.setString(2, acc.getName());
      stmt.setString(3, "");
      stmt.setString(4, type);
      stmt.setDouble(5, acc.getBalance());
      stmt.setDouble(6, 10000);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to save account to database", e);
    }
  }

  private void updateBalance(Account acc) {
    String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setDouble(1, acc.getBalance());
      stmt.setString(2, acc.getAccountNumber());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to update account balance in database", e);
    }
  }

  void saveToHistory(Account acc, Transaction transfer) {
    String sql =
        "INSERT INTO transactions (account_number, owner_name, type, amount, new_balance) "
            + "VALUES (?, ?, ?, ?, ?)";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, acc.getAccountNumber());
      stmt.setString(2, acc.getName());
      stmt.setString(3, transfer.getType());
      stmt.setDouble(4, transfer.getAmount());
      stmt.setDouble(5, transfer.getNewBalance());
      stmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Could not save to transaction history: " + e.getMessage());
    }
  }

  public void showHistory(String accountNumber) throws AccountNotFoundException {
    Account acc = findAccount(accountNumber);
    String sql =
        "SELECT type, amount, new_balance FROM transactions "
            + "WHERE account_number = ? ORDER BY id";
    System.out.println(
        "History for " + acc.getAccountNumber() + " | Owner name: " + acc.getName() + " :");
    boolean foundAny = false;
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, accountNumber);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          System.out.println(
              accountNumber
                  + " | "
                  + acc.getName()
                  + " | "
                  + rs.getString("type")
                  + " | $"
                  + rs.getDouble("amount")
                  + "| Balance: $"
                  + rs.getDouble("new_balance"));
          foundAny = true;
        }
      }
    } catch (SQLException e) {
      System.out.println("Could not read transaction history: " + e.getMessage());
      return;
    }
    if (!foundAny) {
      System.out.println("No history yet for this account.");
    }
  }

  public void deposit(String accountNumber, double amount)
      throws AccountNotFoundException, InvalidAmountException {
    Account acc = findAccount(accountNumber);
    Transaction transfer = acc.deposit(amount);
    updateBalance(acc);
    saveToHistory(acc, transfer);
  }

  public void withdraw(String accountNumber, double amount)
      throws AccountNotFoundException,
          InvalidAmountException,
          InsufficientFundsException,
          DailyLimitExceededException {
    Account acc = findAccount(accountNumber);
    Transaction transfer = acc.withdraw(amount);
    updateBalance(acc);
    saveToHistory(acc, transfer);
  }

  public void transfered(String fromId, String toId, double amount)
      throws AccountNotFoundException,
          InsufficientFundsException,
          InvalidAmountException,
          DailyLimitExceededException {
    Account from = findAccount(fromId);
    Account to = findAccount(toId);
    from.withdraw(amount);
    to.deposit(amount);
    updateBalance(from);
    updateBalance(to);

    saveToHistory(from, new Transaction("Transfer out to " + toId, amount, from.getBalance()));
    saveToHistory(to, new Transaction("Transfer in from " + fromId, amount, to.getBalance()));
  }
}
