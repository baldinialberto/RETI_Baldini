package winsome_DB;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class WinsomeDB_Users extends HashMap<String, UserDB> implements JSON_Serializable {
	// Member variables
	// Constructors

	// Default constructor // Jackson constructor
	public WinsomeDB_Users() {
		/*
		 * This constructor is used when we want to create a new user collection.
		 *
		 * 1. Create a new user collection.
		 */

		// 1. Create a new user collection.
		super();
	}

	// JSON Methods
	public static WinsomeDB_Users JSON_read(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new File(filePath), WinsomeDB_Users.class);
	}
	@Override
	public void JSON_write(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(filePath), this);
	}

	// Getters // None

	// Setters // None

	// Adders // None

	// Removers // None
}

