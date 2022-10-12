package winsome_comunication;

public class Transaction_representation implements Win_Serializable {
	// member variables
	private double value;
	private String time_created;

	// constructor
	public Transaction_representation(double value, String time_created) {
		this.value = value;
		this.time_created = time_created;
	}

	// empty constructor
	public Transaction_representation() {

	}

	// getters
	public double getValue() {
		return this.value;
	}

	// setters
	public void setValue(double value) {
		this.value = value;
	}

	public String getTime_created() {
		return this.time_created;
	}

	public void setTime_created(String time_created) {
		this.time_created = time_created;
	}

	@Override
	public String toString() {
		return "Wallet_Transition_simple [value=" + value + ", time_created=" + time_created + "]";
	}

	@Override
	public String serialize() {
		/*
		 * This method is used to serialize this object.
		 * the format is:
		 * value|time_created
		 *
		 * 1. Return the serialized string.
		 */

		// 1. Return the serialized string.
		return this.value + "|" + this.time_created;
	}

	@Override
	public void deserialize(String string) {
		/*
		 * This method is used to deserialize a string.
		 * The format is:
		 * value|time_created
		 *
		 * 1. Split the string.
		 * 2. Set the value.
		 * 3. Set the time created.
		 */

		// 1. Split the string.
		String[] split = string.split("\\|");

		// 2. Set the value.
		try {
			this.value = Double.parseDouble(split[0]);
		} catch (Exception e) {
			this.value = 0;
			this.time_created = "invalid";
			return;
		}

		// 3. Set the time created.
		this.time_created = split[1];
	}
}
