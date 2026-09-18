package ATM;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;

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
    void saveToHistory(Account acc, Transaction transfer){
        try {
            FileWriter fw = new FileWriter(historyFile, true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(acc.getAccountId() + " | " + acc.getName() + " | " + acc.toString());
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
                    System.out.println("line");
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

}

