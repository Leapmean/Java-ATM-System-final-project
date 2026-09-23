package me.meng.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import me.meng.exception.AccountLockedException;
import me.meng.exception.AccountNotFoundException;
import me.meng.exception.InvalidAccountDetailsException;
import me.meng.exception.InvalidPinException;
import me.meng.model.Card;

public class AuthService {
  private final DataSource dataSource;
  private Map<String, Card> cards = new HashMap<>();

  public AuthService(DataSource dataSource) {
    this.dataSource = dataSource;
    loadCards();
  }

  private void loadCards() {
    String sql = "SELECT card_number, account_number, pin FROM cards";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        Card card =
            new Card(
                rs.getString("card_number"), rs.getString("account_number"), rs.getString("pin"));
        cards.put(card.getCardNumber(), card);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to load cards from database", e);
    }
  }

  public void addCard(Card card) throws InvalidAccountDetailsException {
    if (card.getCardNumber() == null || card.getCardNumber().isBlank()) {
      throw new InvalidAccountDetailsException("Card number is required.");
    }
    if (cards.containsKey(card.getCardNumber())) {
      throw new InvalidAccountDetailsException(
          "Card number " + card.getCardNumber() + " is already in use.");
    }
    if (!card.getCardNumber().matches("\\d+")) {
      throw new InvalidAccountDetailsException("Card number must contain only digits.");
    }
    cards.put(card.getCardNumber(), card);
    String sql =
        "INSERT INTO cards (card_number, account_number, pin, failed_attempts, locked) "
            + "VALUES (?, ?, ?, 0, 0)";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, card.getCardNumber());
      stmt.setString(2, card.getAccountNumber());
      stmt.setString(3, card.getPin());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to save card to database", e);
    }
  }

  public Collection<Card> getAllCards() {
    return cards.values();
  }

  public Card findCardByAccountNumber(String accountNumber) throws AccountNotFoundException {
    for (Card card : cards.values()) {
      if (card.getAccountNumber().equals(accountNumber)) {
        return card;
      }
    }
    throw new AccountNotFoundException("No card linked to account " + accountNumber);
  }

  private void persistCard(Card card) {
    String sql = "UPDATE cards SET pin = ?, failed_attempts = ?, locked = ? WHERE card_number = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, card.getPin());
      stmt.setInt(2, card.getFailedAttempts());
      stmt.setInt(3, card.isLocked() ? 1 : 0);
      stmt.setString(4, card.getCardNumber());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to update card in database", e);
    }
  }

  public Card login(String cardNumber, String pin)
      throws AccountNotFoundException, AccountLockedException, InvalidPinException {
    Card card = cards.get(cardNumber);
    if (card == null) {
      throw new AccountNotFoundException("This Card Number not found.");
    }
    if (card.isLocked()) {
      throw new AccountLockedException("Your Card is Locked.");
    }
    if (!card.checkPin(pin)) {
      card.addFailedAttempts();
      persistCard(card);
      if (card.isLocked()) {
        throw new AccountLockedException("Too many wrong attempts. Your card is Locked.");
      }
      throw new InvalidPinException("Wrong PIN. Please try again");
    }
    card.resetFailedAttempts();
    persistCard(card);
    return card;
  }

  public void changePin(Card card, String oldPin, String newPin) throws InvalidPinException {
    if (!card.checkPin(oldPin)) {
      throw new InvalidPinException("Old PIN is incorrect.");
    }
    if (!newPin.matches("\\d{4}")) {
      throw new InvalidPinException("New PIN must be exactly 4 digits.");
    }
    card.setPin(newPin);
    persistCard(card);
  }

  public void lockCard(String cardNumber) throws AccountNotFoundException {
    Card card = cards.get(cardNumber);
    if (card == null) {
      throw new AccountNotFoundException("Card number not found.");
    }
    card.lock();
    persistCard(card);
  }

  public void unlockCard(String cardNumber) throws AccountNotFoundException {
    Card card = cards.get(cardNumber);
    if (card == null) {
      throw new AccountNotFoundException("Card number not found.");
    }
    card.unlock();
    persistCard(card);
  }
}
