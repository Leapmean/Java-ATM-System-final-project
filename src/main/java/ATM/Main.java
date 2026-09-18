package ATM;

import java.util.Scanner;

public class Main {
    public static void main (String[] args) {

        Banking bank = new Banking();
        Scanner input = new Scanner(System.in);
        boolean bankingFunction = true;

        while (bankingFunction) {
            System.out.println("===========ATM==========");
            System.out.println("1. Check balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Show transaction history");
            System.out.println("6. Exit");
            System.out.println("Pick an option: ");
            int choice = Integer.parseInt(input.nextLine());
            try {
                switch (choice) {
                    case 1:
                        System.out.println("Account ID: ");
                        String id1 = input.nextLine();

                        Account acc = bank.findAccount(id1);
                        System.out.println(acc.getAccountType() + " account balance: $" + acc.getBalance());

                }
            } catch(AccountNotFoundException e){
                System.out.println("Error : " + e.getMessage());
            }
        }
    }
}
