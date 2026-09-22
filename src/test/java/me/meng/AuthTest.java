package me.meng;

import me.meng.exception.*;
import me.meng.model.Card;
import me.meng.service.AuthService;

public class AuthTest {
  public static void main(String[] args) {
    AuthService auth = new AuthService();
    auth.addCard(new Card("1111222233334444", "ACC-001", "1234"));

    String[] pins = {"0000", "1111", "2222", "1234"};

    for (String pin : pins) {
      try {
        Card c = auth.login("1111222233334444", pin);
        System.out.println("Login OK: " + c.getAccountNumber());
      } catch (InvalidPinException | AccountLockedException | AccountNotFoundException e) {
        System.out.println(e.getMessage());
      }
    }
  }
}
