package winsome_communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public abstract class WinSerializable {
	// methods
	public String serialize(){
		/*
		 * Serialize the object to a String using Jackson
		 */
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return "";
		}
	}

	public void deserialize(String string) {
		/*
		 * Deserialize the object from a String using Jackson
		 */
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.readerForUpdating(this).readValue(string);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
