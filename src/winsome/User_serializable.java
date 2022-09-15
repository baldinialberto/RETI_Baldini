package winsome;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class User_serializable extends User implements JSON_Serializable{
    public User_serializable(String username, String password, String[] tags) {
        super(username, password, tags);

    }

//    public String[] getFollows() {
//        String[] res = new String[follows.size()];
//        int i = 0;
//        for (String s : this.follows)
//            res[i++] = s;
//        return res;
//    }
//
//    public void setFollows(String[] follows) {
//        this.follows.clear();
//        this.follows.addAll(Arrays.asList(follows));
//    }
//
//    public String[] getFollowers() {
//        String[] res = new String[followers.size()];
//        int i = 0;
//        for (String s : this.followers)
//            res[i++] = s;
//        return res;
//    }
//
//    public void setFollowers(String[] followers) {
//        this.followers.clear();
//        this.followers.addAll(Arrays.asList(followers));
//    }
//
//    public String[] getTags() {
//        return tags;
//    }
//
//    public void setTags(String[] tags) {
//        this.tags = tags;
//    }
//
//    public Blog getBlog() {
//        return blog;
//    }
//
//    public void setBlog(Blog blog) {
//        this.blog = blog;
//    }
//
//    public Wallet getWallet() {
//        return wallet;
//    }
//
//    public void setWallet(Wallet wallet) {
//        this.wallet = wallet;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }

    @Override
    public void JSON_write(String filePath) throws IOException {
        File file = new File(filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(file, this);
    }

    @Override
    public User_serializable JSON_read(String filePath) throws IOException {
        File file = new File(filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(file, User_serializable.class);
    }
}
