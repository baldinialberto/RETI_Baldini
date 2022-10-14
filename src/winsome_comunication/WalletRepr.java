package winsome_comunication;

import java.util.ArrayList;
import java.util.List;

public class WalletRepr implements WinSerializable {
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

	// setters
	public void setBalance(double balance) {
		this.balance = balance;
	}

	public List<TransactionRepr> getTransaction() {
		return this.transaction;
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

	@Override
	public String serialize() {
		/*
		 * This method is used to serialize this object.
		 * The format is:
		 * balance||transitions (each transition is separated by a ||)
		 *
		 * 1. Create a string builder.
		 * 2. Append the balance.
		 * 3. Append the transitions.
		 * 4. Return the serialized string.
		 */

		// 1. Create a string builder.
		StringBuilder sb = new StringBuilder();

		// 2. Append the balance.
		sb.append(this.balance);

		// 3. Append the transitions.
		for (TransactionRepr transition : this.transaction) {
			sb.append("||");
			sb.append(transition.serialize());
		}

		// 4. Return the serialized string.
		return sb.toString();
	}

	@Override
	public void deserialize(String string) {
		/*
		 * This method is used to deserialize a string.
		 * The format is:
		 * balance||transitions (each transition is separated by a ||)
		 *
		 * 1. Split the string.
		 * 2. Set the balance.
		 * 3. For each transition, deserialize it and add it to the list.
		 */

		// 1. Split the string.
		String[] split = string.split("\\|\\|");

		// 2. Set the balance.
		try {
			this.balance = Double.parseDouble(split[0]);
		} catch (Exception e) {
			this.balance = 0;
			return;
		}

		// 3. For each transition, deserialize it and add it to the list.
		for (int i = 1; i < split.length; i++) {
			TransactionRepr transition = new TransactionRepr();
			transition.deserialize(split[i]);
			this.transaction.add(transition);
		}
	}
}
