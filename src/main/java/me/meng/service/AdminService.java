package me.meng.service;

import java.util.List;
import me.meng.exception.AccountNotFoundException;
import me.meng.exception.InvalidAccountDetailsException;
import me.meng.exception.InvalidAmountException;
import me.meng.exception.InvalidPinException;
import me.meng.model.Account;
import me.meng.model.Card;

public class AdminService {
  private final Banking banking;
  private final AuthService auth;

  public AdminService(Banking banking, AuthService auth) {
    this.banking = banking;
    this.auth = auth;
  }

  public Account createAccount(
      String name, double startingBalance, String type, String cardNumber, String pin)
      throws InvalidAmountException, InvalidPinException, InvalidAccountDetailsException {
    if (startingBalance < 0) {
      throw new InvalidAmountException("Starting balance cannot be negative.");
    }
    if (!pin.matches("\\d{4}")) {
      throw new InvalidPinException("PIN must be exactly 4 digits.");
    }
    Account account = banking.createAccount(name, startingBalance, pin, type);
    auth.addCard(new Card(cardNumber, account.getAccountNumber(), pin));
    return account;
  }

  public List<Account> viewAllAccounts() {
    return banking.getAllAccounts();
  }

  public String describeAccount(Account acc) {
    String status = "no card issued";
    try {
      Card card = auth.findCardByAccountNumber(acc.getAccountNumber());
      status = card.isLocked() ? "LOCKED" : "active";
    } catch (AccountNotFoundException e) {
      // leave default status
    }
    return acc.getAccountNumber()
        + " | "
        + acc.getName()
        + " | "
        + acc.getAccountType()
        + " | Balance: $"
        + acc.getBalance()
        + " | "
        + status;
  }

  public void lockAccount(String accountNumber) throws AccountNotFoundException {
    Card card = auth.findCardByAccountNumber(accountNumber);
    auth.lockCard(card.getCardNumber());
  }

  public void unlockAccount(String accountNumber) throws AccountNotFoundException {
    Card card = auth.findCardByAccountNumber(accountNumber);
    auth.unlockCard(card.getCardNumber());
  }
}
