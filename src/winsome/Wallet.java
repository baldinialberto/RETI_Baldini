package winsome;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import winsome.WinsomeExceptions.UnauthorizedAction;

public class Wallet {
    private float value;
    private ConcurrentLinkedQueue<Transaction> transactions;
    private final User _owner;

    public Wallet(User owner) {
        value = 0.0f;
        transactions = new ConcurrentLinkedQueue<>();
        this._owner = owner;
    }

    public Transaction addTransaction(float amount, Server.ServerAuthorization authorization)
            throws UnauthorizedAction {
        Transaction newTransaction = new Transaction(amount, authorization);
        this.transactions.offer(newTransaction);
        return newTransaction;
    }

    public class Transaction {
        public final float _amount;
        public final Date _time;

        public Transaction(float amount, Server.ServerAuthorization authorization)
                throws WinsomeExceptions.UnauthorizedAction {
            if (authorization == null) {
                throw new WinsomeExceptions.UnauthorizedAction();
            }
            this._amount = amount;
            this._time = new Date(System.currentTimeMillis());
        }

        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            return _amount + " : " + formatter.format(_time);
        }
    }
}
