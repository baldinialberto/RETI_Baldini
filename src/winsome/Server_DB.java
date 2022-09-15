package winsome;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Server_DB extends HashMap<String, User> implements JSON_Serializable {
	public Server_DB() {
		super();
	}

	public int add_user(User user) {
		if (this.containsKey(user.username))
			return -1;
		this.put(user.username, user);
		return 0;
	}

	public int remove_user(String username) {
		if (!this.containsKey(username))
			return -1;
		this.remove(username);
		return 0;
	}

	public User get_user(String username) {
		return this.get(username);
	}

	public int login(String username, String password, Server.ServerAuthorization sa) {
		if (!this.containsKey(username))
			return -1;
		User u = this.get(username);
		if (u.check_psw(password, sa))
		{
			// TODO: set login status
			return 0;
		}
		return -1;
	}

	public int logout(String username, Server.ServerAuthorization sa) {
		if (!this.containsKey(username))
			return -1;
		User u = this.get(username);
		// TODO: set logout status
		return 0;
	}

	@Override
	public void JSON_write(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(filePath), this);
	}

	@Override
	public JSON_Serializable JSON_read(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new File(filePath), Server_DB.class);
	}
}
