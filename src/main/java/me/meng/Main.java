package me.meng;

import java.util.List;
import java.util.Scanner;
import me.meng.db.Database;
import me.meng.exception.AccountLockedException;
import me.meng.exception.AccountNotFoundException;
import me.meng.exception.DailyLimitExceededException;
import me.meng.exception.InsufficientFundsException;
import me.meng.exception.InvalidAccountDetailsException;
import me.meng.exception.InvalidAmountException;
import me.meng.exception.InvalidPinException;
import me.meng.model.Account;
import me.meng.model.Card;
import me.meng.service.AdminService;
import me.meng.service.AuthService;
import me.meng.service.Banking;

public class Main {
  private static final String ADMIN_PASSWORD = "admin123";

  public static void main(String[] args) throws InvalidAccountDetailsException {
    try (Database db = new Database("atm.db")) {
      Banking bank = new Banking(db.getDataSource());
      AuthService auth = new AuthService(db.getDataSource());
      AdminService admin = new AdminService(bank, auth);
      Scanner input = new Scanner(System.in);

      if (bank.getAllAccounts().isEmpty()) {
        Account alice = bank.createAccount("Somaneth", 500, "sreyLngong123", "savings");
        Account bob = bank.createAccount("Bob", 200, "bongslo1maneth", "checking");
        auth.addCard(new Card("1111222233334444", alice.getAccountNumber(), "1234"));
        auth.addCard(new Card("5555666677778888", bob.getAccountNumber(), "4321"));
        System.out.println(
            "Test cards ready: 1111222233334444 (PIN 1234, "
                + alice.getAccountType()
                + " "
                + alice.getAccountNumber()
                + "), 5555666677778888 (PIN 4321, "
                + bob.getAccountType()
                + " "
                + bob.getAccountNumber()
                + ")");
      } else {
        System.out.println("Loaded " + bank.getAllAccounts().size() + " account(s) from atm.db.");
      }
      System.out.println("Admin password: " + ADMIN_PASSWORD);

      runMenu(bank, auth, admin, input);
    }
  }

  private static void runMenu(Banking bank, AuthService auth, AdminService admin, Scanner input) {
    boolean running = true;
    while (running) {
      System.out.println("=========ATM SYSTEM=========");
      System.out.println("1. User Login");
      System.out.println("2. Admin Login");
      System.out.println("3. Exit");
      System.out.print("Pick an option: ");
      String choice = input.nextLine();
      switch (choice) {
        case "1":
          userSession(bank, auth, input);
          break;
        case "2":
          System.out.print("Admin password: ");
          String password = input.nextLine();
          if (password.equals(ADMIN_PASSWORD)) {
            adminSession(admin, input);
          } else {
            System.out.println("Incorrect admin password.");
          }
          break;
        case "3":
          running = false;
          System.out.println("Goodbye!");
          break;
        default:
          System.out.println("Please enter number from 1-3.");
      }
    }
    input.close();
  }

  private static void userSession(Banking bank, AuthService auth, Scanner input) {
    Card session = null;
    while (session == null) {
      System.out.print("Card number (or 0 to cancel): ");
      String cardNumber = input.nextLine();
      if (cardNumber.equals("0")) {
        return;
      }
      System.out.print("PIN: ");
      String pin = input.nextLine();
      try {
        session = auth.login(cardNumber, pin);
      } catch (AccountNotFoundException | AccountLockedException | InvalidPinException e) {
        System.out.println("Error: " + e.getMessage());
      }
    }

    boolean bankingFunction = true;
    while (bankingFunction) {
      System.out.println("===========ATM==========");
      System.out.println("1. Check balance");
      System.out.println("2. Deposit");
      System.out.println("3. Withdraw");
      System.out.println("4. Transfer");
      System.out.println("5. Show transaction history");
      System.out.println("6. Change PIN");
      System.out.println("7. Log out");
      System.out.print("Pick an option: ");
      int choice;
      try {
        choice = Integer.parseInt(input.nextLine());
      } catch (NumberFormatException e) {
        System.out.println("Please enter a number.");
        continue;
      }
      try {
        switch (choice) {
          case 1:
            Account acc = bank.findAccount(session.getAccountNumber());
            System.out.println(acc.getAccountType() + " account balance: $" + acc.getBalance());
            break;
          case 2:
            System.out.print("Enter amount to deposit: ");
            double depositAmount = Double.parseDouble(input.nextLine());
            bank.deposit(session.getAccountNumber(), depositAmount);
            System.out.println("Deposit done");
            break;
          case 3:
            System.out.print("Amount to withdraw: ");
            double withdrawAmount = Double.parseDouble(input.nextLine());
            bank.withdraw(session.getAccountNumber(), withdrawAmount);
            System.out.println("Withdraw done");
            break;
          case 4:
            System.out.print("To account: ");
            String toId = input.nextLine();
            System.out.print("Amount to Transfer: ");
            double amountToTransfer = Double.parseDouble(input.nextLine());
            bank.transfered(session.getAccountNumber(), toId, amountToTransfer);
            System.out.println("Transfer done");
            break;
          case 5:
            bank.showHistory(session.getAccountNumber());
            break;
          case 6:
            System.out.print("Old PIN: ");
            String oldPin = input.nextLine();
            System.out.print("New PIN: ");
            String newPin = input.nextLine();
            auth.changePin(session, oldPin, newPin);
            System.out.println("PIN changed");
            break;
          case 7:
            bankingFunction = false;
            System.out.println("Logged out.");
            break;
          default:
            System.out.println("Please enter number from 1-7.");
        }
      } catch (AccountNotFoundException e) {
        System.out.println("Error : " + e.getMessage());
      } catch (InvalidAmountException
          | InsufficientFundsException
          | DailyLimitExceededException
          | InvalidPinException
          | NumberFormatException e) {
        System.out.println("Error: " + e.getMessage());
      }
    }
  }

  private static void adminSession(AdminService admin, Scanner input) {
    boolean inAdmin = true;
    while (inAdmin) {
      System.out.println("=========ADMIN MENU=========");
      System.out.println("1. Create new account");
      System.out.println("2. View all accounts");
      System.out.println("3. Lock an account");
      System.out.println("4. Unlock a locked account");
      System.out.println("5. Log out");
      System.out.print("Pick an option: ");
      int choice;
      try {
        choice = Integer.parseInt(input.nextLine());
      } catch (NumberFormatException e) {
        System.out.println("Please enter a number.");
        continue;
      }
      try {
        switch (choice) {
          case 1:
            System.out.print("Owner name: ");
            String name = input.nextLine();
            System.out.print("Account type (savings/checking): ");
            String type = input.nextLine();
            System.out.print("Starting balance: ");
            double startingBalance = Double.parseDouble(input.nextLine());
            System.out.print("New card number: ");
            String cardNumber = input.nextLine();
            System.out.print("New 4-digit PIN: ");
            String pin = input.nextLine();
            Account created = admin.createAccount(name, startingBalance, type, cardNumber, pin);
            System.out.println(
                "Created "
                    + created.getAccountType()
                    + " account "
                    + created.getAccountNumber()
                    + " for "
                    + created.getName()
                    + " with card "
                    + cardNumber);
            break;
          case 2:
            List<Account> accounts = admin.viewAllAccounts();
            if (accounts.isEmpty()) {
              System.out.println("No accounts yet.");
            } else {
              for (Account acc : accounts) {
                System.out.println(admin.describeAccount(acc));
              }
            }
            break;
          case 3:
            System.out.print("Account number to lock: ");
            String accountToLock = input.nextLine();
            admin.lockAccount(accountToLock);
            System.out.println("Account locked.");
            break;
          case 4:
            System.out.print("Account number to unlock: ");
            String accountNumber = input.nextLine();
            admin.unlockAccount(accountNumber);
            System.out.println("Account unlocked.");
            break;
          case 5:
            inAdmin = false;
            System.out.println("Logging out of admin mode.");
            break;
          default:
            System.out.println("Please enter number from 1-5.");
        }
      } catch (InvalidAmountException
          | InvalidPinException
          | InvalidAccountDetailsException
          | AccountNotFoundException
          | NumberFormatException e) {
        System.out.println("Error: " + e.getMessage());
      }
    }
  }
}
