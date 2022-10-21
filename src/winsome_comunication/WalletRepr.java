package winsome_comunication;

import java.util.ArrayList;
import java.util.List;

public class WalletRepr extends WinSerializable {
	// member variables
	double balance;
	List<TransactionRepr> transaction = new ArrayList<>();

	// constructor
	public WalletRepr(double balance, List<TransactionRepr> transactions) {
		this.balance = balance;
		this.transaction = transactions;
	}

	// empty constructor
	public WalletRepr() {

	}

	// Deserialization Constructor
	/**
	 * This method is used to create a new wallet representation based on a serialized wallet representation.
	 * @param string the serialized wallet representation.
	 */
	public WalletRepr(String string) {
		deserialize(string);
	}

	// getters
	public double getBalance() {
		return this.balance;
	}
	public List<TransactionRepr> getTransaction() {
		return this.transaction;
	}

	// setters
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public void setTransaction(List<TransactionRepr> transaction) {
		this.transaction = transaction;
	}

	// add transition
	public void add_transition(TransactionRepr transition) {
		this.transaction.add(transition);
	}

	@Override
	public String toString() {
		/*
		 * This method is used to convert the wallet representation to a string.
		 * The string will be shown in the console.
		 * The format is as follows:
		 * Wallet : balance = <balance(xx.xx)>, <number of transitions>:
		 * <transition 1> (<amount> at <time>)
		 * <transition 2> (<amount> at <time>)
		 * ...
		 * <transition n> (<amount> at <time>)
		 */
		StringBuilder s = new StringBuilder();
		s.append("Wallet : balance = ").append(String.format("%.2f", this.balance));
		s.append(", #transactions = ").append(this.transaction.size());

		if (this.transaction.size() == 0) s.append("\n");

		for (TransactionRepr transition : this.transaction) {
			s.append("\n").append(String.format("\t%.4f", transition.getValue()));
			s.append(" at ").append(transition.getTime_created());
		}

		return s.toString();
	}
}
