package me.meng.service;
import me.meng.exception.AccountLockedException;
import me.meng.exception.AccountNotFoundException;
import me.meng.exception.InvalidPinException;
import me.meng.model.Card;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private Map<String, Card> cards = new HashMap<>();
    public void addCard(Card card){
        cards.put(card.getCardNumber(), card);
    }
    public Card login(String cardNumber, String pin) throws AccountNotFoundException, AccountLockedException,InvalidPinException{
        Card card= cards.get(cardNumber);
        if (card == null){
            throw new AccountNotFoundException("This Card Number not found.");
        }
        if (card.isLocked()){
            throw new AccountLockedException("Your Card is Locked.");
        }
        if (!card.checkPin(pin)){
            card.addFailedAttempts();
            if (card.isLocked()){
                throw new AccountLockedException("Too many wrong attempts. Your card is Locked.");
            }
            throw new InvalidPinException("Wrong PIN. Please try again");
        }
        card.resetFailedAttempts();
        return card;
    }
    public void changePin(Card card, String oldPin, String newPin)throws InvalidPinException{
        if (!card.checkPin(oldPin)){
            throw new InvalidPinException("Old PIN is incorrect.");
        }
        if (newPin.length() !=4){
            throw new InvalidPinException("New PIN must be 4 digits.");
        }
        card.setPin(newPin);
    }
    public void unlockCard(String cardNumber) throws AccountNotFoundException{
        Card card = cards.get(cardNumber);
        if (card == null){
            throw new AccountNotFoundException("Card number not found.");
        }
        card.unlock();
    }
}
