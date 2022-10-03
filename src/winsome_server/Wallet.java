package winsome_server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import winsome_comunication.Wallet_simple;

public class Wallet implements JSON_Serializable {
    // Member variables
    private String username;
    private double balance;
    private List<Transaction> transactions;

    // Constructors

    // Default constructor
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
        this.transactions = new ArrayList<>();
    }

    // Jackson constructor
    public Wallet() {
        /*
         * This constructor is used by Jackson when it reads a JSON file.
         */
    }

    // Methods

    // Getters

    public String getUsername() {
        return this.username;
    }

    public double getBalance() {
        return this.balance;
    }

    public List<Transaction> getTransactions() {
        return this.transactions;
    }

    // Setters

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // Other methods
    public Wallet_simple to_wallet_simple() {
        /*
         * This method is used to convert a Wallet object to a Wallet_simple object.
         *
         * 1. Create a List of Wallet_Transition_simple objects from the list of transactions.
         * 2. Return a new Wallet_simple object.
         */

        // 1. Create a List of Wallet_Transition_simple objects from the list of transactions.
        List<winsome_comunication.Wallet_Transition_simple> transactions_simple = new ArrayList<>();
        for (Transaction transaction : this.transactions) {
            transactions_simple.add(transaction.to_transition_simple());
        }

        // 2. Return a new Wallet_simple object.
        return new winsome_comunication.Wallet_simple(this.balance, transactions_simple);
    }

    @Override
    public void JSON_write(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File(filePath), this);
    }

    public static Wallet JSON_read(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), Wallet.class);
    }
}
