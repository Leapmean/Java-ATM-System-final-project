package ATM;
import java.util.ArrayList;

public class Banking {

    ArrayList<Account> accounts = new ArrayList<Account>();
    int accountCount = 1;
    String historyFile = "History.txt";

    Account findAccount(String accountId) throws AccountNotFoundException{
        for(int i = 0; i < accounts.size(); i++){
            if(accounts.get(i).getAccountId().equals(accountId)) {
                return accounts.get(i);
            }
        }
        throw new AccountNotFoundException("Account is not found!");
    }
}
