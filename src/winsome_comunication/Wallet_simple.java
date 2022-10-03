package winsome_comunication;

import java.util.ArrayList;
import java.util.List;

public class Wallet_simple implements Win_Serializable {
	// member variables
	double balance;
	List<Wallet_Transition_simple> transitions = new ArrayList<>();

	// constructor
	public Wallet_simple(double balance, List<Wallet_Transition_simple> transitions) {
		this.balance = balance;
		this.transitions = transitions;
	}
	// empty constructor
	public Wallet_simple() {

	}

	// getters
	public double getBalance() {
		return this.balance;
	}

	public List<Wallet_Transition_simple> getTransitions() {
		return this.transitions;
	}

	// setters
	public void setBalance(double balance) {
		this.balance = balance;
	}

	public void setTransitions(List<Wallet_Transition_simple> transitions) {
		this.transitions = transitions;
	}

	// add transition
	public void add_transition(Wallet_Transition_simple transition) {
		this.transitions.add(transition);
	}

	@Override
	public String toString() {
		return "Wallet_simple [balance=" + balance + ", transitions=" + transitions + "]";
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
		for (Wallet_Transition_simple transition : this.transitions) {
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
		try{
			this.balance = Double.parseDouble(split[0]);
		} catch (Exception e) {
			this.balance = 0;
			return;
		}

		// 3. For each transition, deserialize it and add it to the list.
		for (int i = 1; i < split.length; i++) {
			Wallet_Transition_simple transition = new Wallet_Transition_simple();
			transition.deserialize(split[i]);
			this.transitions.add(transition);
		}
	}
}
