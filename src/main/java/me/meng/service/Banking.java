package me.meng.service;

import me.meng.exception.AccountNotFoundException;
import me.meng.exception.DailyLimitExceededException;
import me.meng.exception.InsufficientFundsException;
import me.meng.exception.InvalidAmountException;
import me.meng.model.Account;
import me.meng.model.CheckingAccount;
import me.meng.model.SavingAccount;
import me.meng.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;

public class Banking {

    ArrayList<Account> accounts = new ArrayList<Account>();
    int accountCounter = 1;
    String historyFile = "History.txt";

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
        return acc;
    }

    public void addAccount(Account acc) {
        accounts.add(acc);
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts);
    }

    public Account findAccount(String accountNumber) throws AccountNotFoundException{
        for(int i = 0; i < accounts.size(); i++){
            if(accounts.get(i).getAccountNumber().equals(accountNumber)) {
                return accounts.get(i);
            }
        }
        throw new AccountNotFoundException("Account is not found!");
    }
    void saveToHistory(Account acc, Transaction transfer){
        try {
            FileWriter fw = new FileWriter(historyFile, true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(acc.getAccountNumber() + " | " + acc.getName() + " | " + transfer.toString());
            bw.newLine();
            bw.close();
        }catch (IOException e){
            System.out.println("Could not save to history file: " + e.getMessage());
        }
    }
    public void showHistory(String accountNumber) throws AccountNotFoundException{
        Account acc = findAccount(accountNumber);
        try{

            FileReader fr = new FileReader(historyFile);
            BufferedReader br = new BufferedReader((fr));

            System.out.println("History for " + acc.getAccountNumber() + " | Owner name: " + acc.getName() + " :");
            String line = br.readLine();
            boolean foundAny = false;
            while (line != null){
                if(line.startsWith(accountNumber + " | ")){
                    System.out.println(line);
                    foundAny = true;
                }
                line = br.readLine();
            }
            if (!foundAny){
                System.out.println("No history yet for this account.");
            }
            br.close();
        }catch (IOException e){
            System.out.println("No history file yet make a transaction first.");
        }
    }
    public void deposit(String accountNumber, double amount) throws AccountNotFoundException, InvalidAmountException{
        Account acc = findAccount(accountNumber);
        Transaction transfer = acc.deposit(amount);
        saveToHistory(acc, transfer);
    }
    public void withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException,InsufficientFundsException, DailyLimitExceededException{
        Account acc = findAccount(accountNumber);
        Transaction transfer = acc.withdraw(amount);
        saveToHistory(acc, transfer);
    }
    public void transfered(String fromId, String toId, double amount)
            throws AccountNotFoundException, InsufficientFundsException, InvalidAmountException, DailyLimitExceededException{
        Account from = findAccount(fromId);
        Account to = findAccount(toId);
        from.withdraw(amount);
        to.deposit(amount);

        saveToHistory(from, new Transaction("Transfer out to " + toId, amount, from.getBalance()));
        saveToHistory(from, new Transaction("Transfer in from " + fromId, amount, to.getBalance()));
    }
}

