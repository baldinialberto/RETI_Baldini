package winsome_DB;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import winsome_comunication.TransactionRepr;
import winsome_comunication.WalletRepr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a wallet in the database.
 * It contains:
 * 1. The username of the wallet.
 * 2. The balance of the wallet.
 * 3. The list of transactions of the wallet.
 * <p></p>
 * This class is available only to the Winsome_Database
 */
public class WalletDB implements JSON_Serializable {
	// Member variables
	private String username;
	private double balance;
	private List<TransactionDB> transactions;

	// Constructors

	// Default constructor
	public WalletDB(String username) {
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
	public WalletDB() {
		/*
		 * This constructor is used by Jackson when it reads a JSON file.
		 */
	}

	// JSON Methods
	public static WalletDB JSON_read(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new File(filePath), WalletDB.class);
	}
	@Override
	public void JSON_write(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(filePath), this);
	}

	// Getters
	public String getUsername() {
		return this.username;
	}
	public double getBalance() {
		return this.balance;
	}
	public List<TransactionDB> getTransactions() {
		return this.transactions;
	}

	// Setters
	public void setUsername(String username) {
		this.username = username;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public void setTransactions(List<TransactionDB> transactions) {
		this.transactions = transactions;
	}

	// Adders
	public void add_transaction(TransactionDB transaction) {
		/*
		 * This method is used to add a transaction to the list of transactions.
		 *
		 * 1. Add the transaction to the list of transactions.
		 * 2. Update the balance of this wallet.
		 */

		// 1. Add the transaction to the list of transactions.
		this.transactions.add(transaction);

		// 2. Update the balance of this wallet.
		this.balance += transaction.getValue();
	}

	// Representation
	public WalletRepr representation() {
		/*
		 * This method is used to convert a Wallet object to a Wallet_simple object.
		 *
		 * 1. Create a List of Wallet_Transition_simple objects from the list of transactions.
		 * 2. Return a new Wallet_simple object.
		 */

		// 1. Create a List of Wallet_Transition_simple objects from the list of transactions.
		List<TransactionRepr> transactions_simple = new ArrayList<>();
		for (TransactionDB transaction : this.transactions) {
			transactions_simple.add(transaction.representation());
		}

		// 2. Return a new Wallet_simple object.
		return new WalletRepr(this.balance, transactions_simple);
	}
}
