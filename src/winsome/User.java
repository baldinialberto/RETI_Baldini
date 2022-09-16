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

    public ArrayList<String> getFollows() {
        return follows;
    }

    public void setFollows(ArrayList<String> follows) {
        this.follows = follows;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



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

    public static User JSON_read(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), User.class);
    }
}
