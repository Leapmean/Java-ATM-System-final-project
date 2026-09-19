package ATM;

public class CheckingAccount extends Account {
    private double overDraftLimit = 50;
    public CheckingAccount(String accountId, String name, String password, double balance){
        super(accountId, name, password, balance, 10000);
    }
    @Override
    public Transaction withdraw(double amount)
            throws InvalidAmountException, InsufficientFundsException, DailyLimitExceededException{
        checkBasicWithdrawRule(amount);
        if(getBalance() - amount < -overDraftLimit){
            throw new InsufficientFundsException("That would go over your $ " + overDraftLimit + " overdaraft limit.");
        }
        reduceBalance(amount);
        addToWithdrawToday(amount);
        return new Transaction("Withdraw", amount, getBalance());
    }
    @Override
    public String getAccountType(){
        return "Checking";
    }
}
