package winsome_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class Transaction implements JSON_Serializable {
	// Member variables
	Timestamp time_created;
	double value;

	// Constructor
	public Transaction(List<Comment> comments, List<Vote> votes)
	{
		/*
		 * This constructor is used when we want to create a new transaction.
		 *
		 * 1. Create a new timestamp for this transaction.
		 * 2. Set the value of this transaction.
		 */

		// 1. Create a new timestamp for this transaction.
		this.time_created = new Timestamp(System.currentTimeMillis());

		// 2. Set the value of this transaction.
		this.value = 0;

	}

	// Methods

	// Getters
	public Timestamp getTime_created() {
		return this.time_created;
	}

	public double getValue() {
		return this.value;
	}

	public double getValue_in_BTC(double BTC_price) {
		return this.value / BTC_price;
	}

	// Setters

	public void setTime_created(Timestamp time_created) {
		this.time_created = time_created;
	}

	public void setValue(double value) {
		this.value = value;
	}

	// Other methods
	@Override
	public void JSON_write(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(filePath), this);
	}

	public static Transaction JSON_read(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new File(filePath), Transaction.class);
	}
}
