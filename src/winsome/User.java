package winsome;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class User implements JSON_Serializable {
    protected ArrayList<String> follows;
    protected ArrayList<String> followers;
    protected String[] tags;
    protected Blog blog;
    protected Wallet wallet;

    protected String username;
    protected String password;
    protected String id;
    
    protected Boolean login_status;

    public User(String username, String password, String[] tags) {
        assert tags != null && username != null && password != null;
        this.username = username;
        this.password = password;
        this.tags = tags;
        followers = new ArrayList<>();
        follows = new ArrayList<>();
        blog = new Blog();
        wallet = new Wallet(this);
        // id is set by the server
        id = null;
    }

    public boolean check_psw(String password, Server.ServerAuthorization sa) throws NullPointerException {
        Objects.requireNonNull(sa);
        return this.password.equals(password);
    }

    public String username() {
        return this.username;
    }
    
    public void set_login_status(Boolean status, Server.ServerAuthorization sa) {
        Objects.requireNonNull(sa);
    	this.login_status = status;
    }

    public Boolean get_login_status() {
    	return this.login_status;
    }

    @Override
    public void JSON_write(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File(filePath), this);
    }

    @Override
    public User JSON_read(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), User.class);
    }
}
