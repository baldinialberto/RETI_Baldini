package winsome;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private final User _author;
    private final List<Vote> votes;
    private final List<Comment> comments;
    private Post.Content content;
    private Post.Title title;

    public Post(User author) {
        this._author = author;
        votes = new ArrayList<>();
        comments = new ArrayList<>();
    }

    public Post(User author, String title, String content) {
        this(author);
        this.title = new Title(title);
        this.content = new Content(content);
    }

    public String username() {
        return _author.username;
    }

    public List<Vote> votes() {
        return new ArrayList<Vote>(votes);
    }

    public List<Comment> comments() {
        return new ArrayList<Comment>(comments);
    }

    public String title() {
        return this.title.get();
    }

    public String content() {
        return this.content.get();
    }

    public Title title_obj() { return this.title; }

    public Content content_obj() { return this.content; }

    private abstract class PostContent {
        private String _content;
        private boolean valid;
        private int max_len = 0;

        protected PostContent(String content) {
            this._content = content;
        }

        public String get() {
            return isValid() ? _content : "Unknown";
        }

        public boolean isValid() {
            return _content.length() <= max_len;
        }
    }

    public class Title extends PostContent {
        public Title(String title) {
            super(title);
            super.max_len = 20;
        }
    }

    public class Content extends PostContent {
        public Content(String content) {
            super(content);
            super.max_len = 500;
        }
    }
}
