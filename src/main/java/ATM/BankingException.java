package ATM;

public class BankingException {
}
class InvalidAmountException extends Exception {
    InvalidAmountException(String message) {
        super(message);
    }
}

class InsufficientFundsException extends Exception {
    InsufficientFundsException(String message) {
        super(message);
    }
}

class DailyLimitExceededException extends Exception {
    DailyLimitExceededException(String message) {
        super(message);
    }
}

class AccountNotFoundException extends Exception {
    AccountNotFoundException(String message) {
        super(message);
    }
}