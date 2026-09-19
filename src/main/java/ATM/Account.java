package ATM;

public abstract class Account {
    private String accountId;
    private String name;
    private String password;
    private double balance;
    protected double dailyLimit;
    private double todayWithdraw = 0;

    Account(String accountId, String name, String password, double balance, double dailyLimit){
        this.accountId = accountId;
        this.name = name;
        this.password = password;
        this.balance = balance;
        this.dailyLimit = dailyLimit;
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

    protected void reduceBalance(double amount){
        balance = balance - amount;
    }

    protected  void checkBasicWithdrawRule(double amount) throws InvalidAmountException, DailyLimitExceededException{
        if (amount <= 0){
            throw new InvalidAmountException("Amount must be larger than $0.");
        }
        if(todayWithdraw + amount > dailyLimit){
            throw new DailyLimitExceededException("This is out of your Daily Withdraw Limit.");
        }
    }
    public abstract Transaction withdraw(double amount)
            throws InvalidAmountException, InsufficientFundsException, DailyLimitExceededException;

    public abstract String getAccountType();
}
