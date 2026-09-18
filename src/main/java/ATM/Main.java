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
                        System.out.print("Account ID: ");
                        String id1 = input.nextLine();

                        Account acc = bank.findAccount(id1);
                        System.out.println(acc.getAccountType() + " account balance: $" + acc.getBalance());
                        break;
                    case 2:
                        System.out.print("Account ID: ");
                        String id2 = input.nextLine();

                        System.out.print("Enter amount to deposit: ");
                        double depositAmount = input.nextDouble();

                        bank.deposit(id2, depositAmount);
                        System.out.println("Deposit done");
                        break;
                    case 3:
                        System.out.print("Account ID: ");
                        String id3 = input.nextLine();

                        System.out.print("Amount to withdraw: ");
                        double withdrawAmount = input.nextDouble();

                        bank.withdraw(id3, withdrawAmount);
                        System.out.println("Withdraw done");
                        break;
                    case 4:
                        System.out.print("Form account: ");
                        String formId = input.nextLine();

                        System.out.print("To account: ");
                        String toId = input.nextLine();

                        System.out.print("Amount to Transfer: ");
                        double amountToTransfer = input.nextDouble();

                        bank.transfered(formId, toId, amountToTransfer);
                        System.out.println("Transfer done");
                        break;
                    case 5:
                        System.out.print("Account ID: ");
                        String id5 = input.nextLine();

                        bank.showHistory(id5);
                        break;
                    case 6:
                        bankingFunction = false;
                        System.out.println("Goodbye! see you again.");
                    default:
                        System.out.println("Please enter number from 1-6.");
                }
            } catch(AccountNotFoundException e){
                System.out.println("Error : " + e.getMessage());
            }
            catch(InvalidAmountException e){
                System.out.println("Error: " + e.getMessage());
            }
            catch(InsufficientFundsException e){
                System.out.println("Error: " + e.getMessage());
            }
            catch (DailyLimitExceededException e){
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
