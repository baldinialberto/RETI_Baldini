package winsome;

import java.util.List;
import java.util.ArrayList;

public class Blog {
    private final List<Post> _posts;

    public Blog() {
        _posts = new ArrayList<>();
    }

    public void add_post(Post p) {
        _posts.add(p);
    }

    public List<Post> posts() {
        return this._posts;
    }
}
