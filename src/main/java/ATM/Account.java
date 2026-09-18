package ATM;

import java.util.Scanner;

public class Account {
    private String accountId;
    private String name;
    private String password;
    private double balance;
    protected double dailyLimit;
    private double todayWithdraw;

    Account(String accountId, String name, String password, double balance, double todayWithdraw){
        this.accountId = accountId;
        this.name = name;
        this.password = password;
        this.balance = balance;
        this.todayWithdraw = todayWithdraw;
    }

    public String getAccountId(){
        return accountId;
    }
    public String getName(){
        return name;
    }
    public double getBalance(){
        return balance;
    }
    public Transaction deposit(double amount) throws InvalidAmountException{
        if (amount <= 0){
            throw new InvalidAmountException("Deposit should be more than 0$.");
        }
        balance = balance + amount;
        return new Transaction("Deposit : $" , amount, balance);
    }
    protected void addToWithdrawToday(double amount){
        todayWithdraw = todayWithdraw + amount;
    }


}
