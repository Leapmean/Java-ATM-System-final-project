package org.example.model;

public class Card {
    private String cardNumber;
    private String accountNumber;
    private String pin;
    private int failedAttempts;
    private boolean locked;

    public Card(String cardNumber, String accountNumber, String pin){
        this.cardNumber = cardNumber;
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.failedAttempts = 0;
        this.locked = false;
    }

    public boolean checkPin(String enteredPin){
        return pin.equals(enteredPin);
    }
    public void setPin (String newPin){
        this.pin = newPin;
    }
    private void addFailedAttempts(){
        failedAttempts++;
        if (failedAttempts>=3){
            locked = true;
        }
    }
    public void resetFailedAttempts(){
        failedAttempts = 0;
    }
    public void unlock(){
        locked = false;
        failedAttempts = 0;
    }
    public String getCardNumber(){
        return cardNumber;
    }
    public String getAccountNumber(){
        return accountNumber;
    }
    public int getFailedAttempts(){
        return failedAttempts;
    }
    public boolean isLocked(){
        return locked;
    }
}
