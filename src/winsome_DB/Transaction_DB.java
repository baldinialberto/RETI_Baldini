package winsome_DB;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import winsome_comunication.Transition_representation;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * This class represents a transaction in the database.
 * It contains:
 * 1. The amount of the transaction.
 * 2. The time the transaction was created.
 * <p></p>
 * This class is available only to the Winsome_Database
 */
public class Transaction_DB implements JSON_Serializable {
	// Member variables
	Timestamp time_created;
	double value;

	// Constructors

	// Default constructor
	public Transaction_DB(double value) {
		/*
		 * This constructor is used when we want to create a new transaction.
		 *
		 * 1. Create a new timestamp for this transaction.
		 * 2. Set the value of this transaction.
		 */

		// 1. Create a new timestamp for this transaction.
		this.time_created = new Timestamp(System.currentTimeMillis());

		// 2. Set the value of this transaction.
		this.value = value;

	}

	// Jackson constructor
	public Transaction_DB() {
	}

	// JSON Methods
	public static Transaction_DB JSON_read(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new File(filePath), Transaction_DB.class);
	}
	@Override
	public void JSON_write(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(filePath), this);
	}

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

	// Representation
	public Transition_representation representation() {
		/*
		 * This method is used to convert a transaction to a simple transaction.
		 *
		 * 1. Create a new simple transaction.
		 * 2. Return the simple transaction.
		 */

		// 1. Create a new simple transaction.
		Transition_representation transition_simple = new Transition_representation();

		// 2. Set the time created of the simple transaction.
		transition_simple.setTime_created(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(this.time_created));

		// 3. Set the value of the simple transaction.
		transition_simple.setValue(this.value);

		// 4. Return the simple transaction.
		return transition_simple;
	}
}
