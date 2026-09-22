package me.meng.model;

import me.meng.exception.DailyLimitExceededException;
import me.meng.exception.InsufficientFundsException;
import me.meng.exception.InvalidAmountException;

public abstract class Account {
  private String accountNumber;
  private String name;
  private String password;
  private double balance;
  protected double dailyLimit;
  private double todayWithdraw = 0;

  Account(String accountNumber, String name, String password, double balance, double dailyLimit) {
    this.accountNumber = accountNumber;
    this.name = name;
    this.password = password;
    this.balance = balance;
    this.dailyLimit = dailyLimit;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public String getName() {
    return name;
  }

  public double getBalance() {
    return balance;
  }

  public Transaction deposit(double amount) throws InvalidAmountException {
    if (amount <= 0) {
      throw new InvalidAmountException("Deposit should be more than 0$.");
    }
    balance = balance + amount;
    return new Transaction("Deposit : $", amount, balance);
  }

  protected void addToWithdrawToday(double amount) {
    todayWithdraw = todayWithdraw + amount;
  }

  protected void reduceBalance(double amount) {
    balance = balance - amount;
  }

  protected void checkBasicWithdrawRule(double amount)
      throws InvalidAmountException, DailyLimitExceededException {
    if (amount <= 0) {
      throw new InvalidAmountException("Amount must be larger than $0.");
    }
    if (todayWithdraw + amount > dailyLimit) {
      throw new DailyLimitExceededException("This is out of your Daily Withdraw Limit.");
    }
  }

  public abstract Transaction withdraw(double amount)
      throws InvalidAmountException, InsufficientFundsException, DailyLimitExceededException;

  public abstract String getAccountType();
}
