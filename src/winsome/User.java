package winsome;

import java.util.ArrayList;

public class User {
    protected ArrayList<String> follows;
    protected ArrayList<String> followers;
    protected Tag[] tags;
    protected Blog blog;
    protected Wallet wallet;
    protected String username;
    protected String password;

    protected String id;

    public User(Tag[] tags, String username, String password) {
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
        if (sa == null)
            throw new NullPointerException("check_");
        return this.password.equals(password);
    }

    public String username() {
        return this.username;
    }

}
