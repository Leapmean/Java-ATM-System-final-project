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
}
