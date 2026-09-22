package me.meng.model;

import me.meng.exception.DailyLimitExceededException;
import me.meng.exception.InsufficientFundsException;
import me.meng.exception.InvalidAmountException;

public class SavingAccount extends Account {
  private double minimumBalance = 0;

  public SavingAccount(String accountNumber, String name, String password, double balance) {
    super(accountNumber, name, password, balance, 10000);
  }

  @Override
  public Transaction withdraw(double amount)
      throws InvalidAmountException, InsufficientFundsException, DailyLimitExceededException {

    checkBasicWithdrawRule(amount);
    if (getBalance() - amount < minimumBalance) {
      throw new InsufficientFundsException("Saving account must keep atleast $" + minimumBalance);
    }
    reduceBalance(amount);
    addToWithdrawToday(amount);
    return new Transaction("Withdraw", amount, getBalance());
  }

  @Override
  public String getAccountType() {
    return "Saving Account.";
  }
}
