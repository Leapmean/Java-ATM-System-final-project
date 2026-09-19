package ATM;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;

public class Banking {

    ArrayList<Account> accounts = new ArrayList<Account>();
    int accountCounter = 1;
    String historyFile = "History.txt";

    Account createAccount(String name, double startingMoney, String password, String type) {
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

    void addAccount(Account acc) {
        accounts.add(acc);
    }

    Account findAccount(String accountId) throws AccountNotFoundException{
        for(int i = 0; i < accounts.size(); i++){
            if(accounts.get(i).getAccountId().equals(accountId)) {
                return accounts.get(i);
            }
        }
        throw new AccountNotFoundException("Account is not found!");
    }
    void saveToHistory(Account acc, Transaction transfer){
        try {
            FileWriter fw = new FileWriter(historyFile, true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(acc.getAccountId() + " | " + acc.getName() + " | " + transfer.toString());
            bw.newLine();
            bw.close();
        }catch (IOException e){
            System.out.println("Could not save to history file: " + e.getMessage());
        }
    }
    void showHistory(String accountId) throws AccountNotFoundException{
        Account acc = findAccount(accountId);
        try{
            FileReader fr = new FileReader(historyFile);
            BufferedReader br = new BufferedReader((fr));

            System.out.println("History for " + acc.getAccountId() + " | Owner name: " + acc.getName() + " :");
            String line = br.readLine();
            boolean foundAny = false;
            while (line != null){
                if(line.startsWith(accountId + " | ")){
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
    void deposit(String accountId, double amount) throws AccountNotFoundException, InvalidAmountException{
        Account acc = findAccount(accountId);
        Transaction transfer = acc.deposit(amount);
        saveToHistory(acc, transfer);
    }
    void withdraw(String accountId, double amount)
            throws AccountNotFoundException, InvalidAmountException,InsufficientFundsException, DailyLimitExceededException{
        Account acc = findAccount(accountId);
        Transaction transfer = acc.withdraw(amount);
        saveToHistory(acc, transfer);
    }
    void transfered(String fromId, String toId, double amount)
            throws AccountNotFoundException, InsufficientFundsException, InvalidAmountException, DailyLimitExceededException{
        Account from = findAccount(fromId);
        Account to = findAccount(toId);
        from.withdraw(amount);
        to.deposit(amount);

        saveToHistory(from, new Transaction("Transfer out to " + toId, amount, from.getBalance()));
        saveToHistory(from, new Transaction("Transfer in from " + fromId, amount, to.getBalance()));
    }
}

