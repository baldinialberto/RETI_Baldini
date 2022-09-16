package winsome;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import winsome.WinsomeExceptions.UnauthorizedAction;

public class Wallet {
    // Member variables
    private String username;
    private double balance;
    private ConcurrentLinkedQueue<Transaction> transactions;

    // Constructor
    public Wallet(String username) {
        /*
         * This constructor is used when we want to create a new wallet.
         *
         * 1. Set the username of this wallet.
         * 2. Set the balance of this wallet to 0.
         * 3. Create a new list of transactions for this wallet.
         */

        // 1. Set the username of this wallet.
        this.username = username;

        // 2. Set the balance of this wallet to 0.
        this.balance = 0;

        // 3. Create a new list of transactions for this wallet.
        this.transactions = new ConcurrentLinkedQueue<>();
    }

    // Methods

}
