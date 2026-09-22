package me.meng.model;

public class Transaction {
  private String type; // to define what type of action "withdraw deposit or transfer"
  private double amount;
  private double newBalance;

  public Transaction(String type, double amount, double newBalance) {
    this.type = type;
    this.amount = amount;
    this.newBalance = newBalance;
  }

  public String getType() {
    return type;
  }

  public double getAmount() {
    return amount;
  }

  public double getNewBalance() {
    return newBalance;
  }

  public String toString() {
    return type + " | $" + amount + "| Balance: $" + newBalance;
  }
}
