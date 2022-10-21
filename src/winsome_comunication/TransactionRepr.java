package winsome_comunication;

public class TransactionRepr extends WinSerializable {
	// member variables
	private double value;
	private String time_created;

	// constructor
	public TransactionRepr(double value, String time_created) {
		this.value = value;
		this.time_created = time_created;
	}

	// empty constructor
	public TransactionRepr() {

	}

	// deserialization constructor
	public TransactionRepr(String string) {
		deserialize(string);
	}

	// getters
	public double getValue() {
		return this.value;
	}
	public String getTime_created() {
		return this.time_created;
	}

	// setters
	public void setValue(double value) {
		this.value = value;
	}
	public void setTime_created(String time_created) {
		this.time_created = time_created;
	}

	@Override
	public String toString() {
		return "Wallet_Transition_simple [value=" + value + ", time_created=" + time_created + "]";
	}
}
