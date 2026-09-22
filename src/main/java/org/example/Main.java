package org.example;

import org.example.exception.AccountLockedException;
import org.example.exception.AccountNotFoundException;
import org.example.exception.DailyLimitExceededException;
import org.example.exception.InsufficientFundsException;
import org.example.exception.InvalidAmountException;
import org.example.exception.InvalidPinException;
import org.example.model.Account;
import org.example.model.Card;
import org.example.service.AuthService;
import org.example.service.Banking;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Banking bank = new Banking();
        AuthService auth = new AuthService();
        Scanner input = new Scanner(System.in);

        Account alice = bank.createAccount("Somaneth", 500, "sreyLngong123", "savings");
        Account bob = bank.createAccount("Bob", 200, "bongslo1maneth", "checking");
        auth.addCard(new Card("1111222233334444", alice.getAccountNumber(), "1234"));
        auth.addCard(new Card("5555666677778888", bob.getAccountNumber(), "4321"));
        System.out.println("Test cards ready: 1111222233334444 (PIN 1234, " + alice.getAccountType()
                + " " + alice.getAccountNumber() + "), 5555666677778888 (PIN 4321, "
                + bob.getAccountType() + " " + bob.getAccountNumber() + ")");

        Card session = null;
        while (session == null) {
            System.out.print("Card number: ");
            String cardNumber = input.nextLine();
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
            System.out.println("7. Exit");
            System.out.print("Pick an option: ");
            int choice = Integer.parseInt(input.nextLine());
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
                        System.out.println("Goodbye! see you again.");
                        break;
                    default:
                        System.out.println("Please enter number from 1-7.");
                }
            } catch (AccountNotFoundException e) {
                System.out.println("Error : " + e.getMessage());
            } catch (InvalidAmountException | InsufficientFundsException | DailyLimitExceededException
                    | InvalidPinException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        input.close();
    }
}
