package winsome;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import winsome.WinsomeExceptions.WrongPassword;

public class ClientGUI {
    private interface ClientGUI_authorized {

    }

    public static boolean RIGHT_TO_LEFT = false;
    private JFrame _window_frame;
    private final WinsomeGUI_LoginScreen _login_screen;
    private final WinsomeGUI_RegisterScreen _register_screen;
    private final WinsomeGUI_MainScreen _main_screen;
    private final Client _client;
    private User _user = null;

    public ClientGUI(Client client) {

        _client = client;
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);

        _window_frame = new JFrame("WinsomeClient");
        _window_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _window_frame.setMinimumSize(new Dimension(600, 400));
        _window_frame.setResizable(false);

        _login_screen = new WinsomeGUI_LoginScreen(this._window_frame, this);
        _register_screen = new WinsomeGUI_RegisterScreen(this._window_frame, this);
        _main_screen = new WinsomeGUI_MainScreen(this._window_frame, this);
    }

    public User user(ClientGUI_authorized a) {
        return (a != null) ? _user : null;
    }

    public Client client(ClientGUI_authorized a) {
        return (a != null) ? _client : null;
    }

    public void run() {
        _window_frame.setVisible(true);
        _login_screen.show();
    }

    private abstract class ClientGUI_page extends JPanel
            implements ActionListener, ClientGUI_authorized {
        protected final ClientGUI _gui;
        protected final JFrame _window_frame;

        protected ClientGUI_page(JFrame window_frame, ClientGUI gui) {
            super(new BorderLayout());
            this._gui = gui;
            this._window_frame = window_frame;
        }

        public void show() {
            reset();
            this._window_frame.setContentPane(this);
            this._window_frame.pack();
        }

        public abstract void reset();
    }

    private abstract class ClientGUI_widget extends JPanel
            implements ActionListener, ClientGUI_authorized {
        protected final ClientGUI _gui;

        protected ClientGUI_widget(ClientGUI gui) {
            super(new BorderLayout());
            this._gui = gui;
        }

    }

    private class WinsomeGUI_post extends ClientGUI_widget {
        private JPanel _username_cp;
        private JPanel _text_cp;
        private JPanel _buttons_cp;
        private JPanel _votes_cp;
        private JLabel _username_label;
        private JLabel _votes_label;
        private JTextArea _text_ta;
        private JButton _vote_up_button;
        private JButton _vote_down_button;
        private JButton _comment_button;
        private JButton _rewin_button;
        private Post _post;

        public WinsomeGUI_post(ClientGUI gui, Post post) {
            super(gui);
            _post = post;

            // panels
            _username_cp = new JPanel(new BorderLayout());
            _text_cp = new JPanel(new BorderLayout());
            _buttons_cp = new JPanel(new BorderLayout());
            _votes_cp = new JPanel(new BorderLayout());
            this.add(_username_cp, BorderLayout.PAGE_START);
            this.add(_text_cp, BorderLayout.CENTER);
            this.add(_buttons_cp, BorderLayout.PAGE_END);
            _buttons_cp.add(_votes_cp, BorderLayout.WEST);

            // labels
            _username_label = new JLabel(String.format("@%s : %s", _post.username(), _post.title()));
            Pair<Integer, Integer> votes = Vote.get_votes_count(_post.votes());
            _votes_label = new JLabel(String.format(
                    "%d up / %d down", votes.get_a(), votes.get_b()));
            _username_cp.add(_username_label, BorderLayout.WEST);
            _username_cp.add(_votes_label, BorderLayout.EAST);

            // textAreas
            _text_ta = new JTextArea();
            _text_ta.setText(_post.content());
            _text_cp.add(_text_ta, BorderLayout.CENTER);

            // buttons
            _vote_up_button = new JButton("UpVote");
            _vote_up_button.setActionCommand("upvote_pressed");
            _vote_up_button.addActionListener(this);
            _vote_down_button = new JButton("DownVote");
            _vote_down_button.setActionCommand("downvote_pressed");
            _vote_down_button.addActionListener(this);
            _comment_button = new JButton(String.format("Comments (%d)", _post.comments().size()));
            _comment_button.setActionCommand("comment_pressed");
            _comment_button.addActionListener(this);
            _rewin_button = new JButton("Rewin");
            _rewin_button.setActionCommand("rewin_pressed");
            _rewin_button.addActionListener(this);
            _votes_cp.add(_vote_up_button, BorderLayout.WEST);
            _votes_cp.add(_vote_down_button, BorderLayout.EAST);
            _buttons_cp.add(_comment_button, BorderLayout.CENTER);
            _buttons_cp.add(_rewin_button, BorderLayout.EAST);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub

        }
    }

    private class WinsomeGUI_MainScreen extends ClientGUI_page {
        private JPanel _leftSide_cp;
        private JPanel _rightSide_cp;
        private JPanel _winsome_logo_cp;
        private JPanel _buttons_cp;
        private JPanel _userLogged_cp;
        private JPanel _newPost_cp;
        private JPanel _newPost_title_cp;
        private JPanel _newPost_content_cp;
        private JPanel _blogArea_cp;

        private JScrollPane _blog_sp;

        private JLabel _winsome_label;
        private JLabel _username_logged_label;
        private JLabel _username_newPost_label;

        private JButton _logout_button;
        private JButton _newPost_post_button;

        private JTextField _newPost_title_tf;
        private JTextArea _newPost_content_ta;

        public WinsomeGUI_MainScreen(JFrame window_frame, ClientGUI gui) {
            super(window_frame, gui);

            _leftSide_cp = new JPanel(new BorderLayout());
            _rightSide_cp = new JPanel(new BorderLayout());
            _winsome_logo_cp = new JPanel(new BorderLayout());
            _buttons_cp = new JPanel(new BorderLayout());
            _userLogged_cp = new JPanel(new BorderLayout());
            _newPost_cp = new JPanel(new BorderLayout());
            _newPost_title_cp = new JPanel(new BorderLayout());
            _newPost_content_cp = new JPanel(new BorderLayout());
            _blogArea_cp = new JPanel(new FlowLayout());
            _blog_sp = new JScrollPane(_blogArea_cp);
            _blogArea_cp.setAutoscrolls(true);
            this.add(_leftSide_cp, BorderLayout.WEST);
            this.add(_rightSide_cp, BorderLayout.EAST);
            _leftSide_cp.add(_winsome_logo_cp, BorderLayout.PAGE_START);
            _leftSide_cp.add(_buttons_cp, BorderLayout.CENTER);
            _leftSide_cp.add(_userLogged_cp, BorderLayout.PAGE_END);
            _rightSide_cp.add(_newPost_cp, BorderLayout.PAGE_START);
            _rightSide_cp.add(_blogArea_cp, BorderLayout.PAGE_END);
            _rightSide_cp.setPreferredSize(new Dimension(450, 350));
            _newPost_cp.add(_newPost_title_cp, BorderLayout.PAGE_START);
            _newPost_cp.add(_newPost_content_cp, BorderLayout.CENTER);

            _winsome_label = new JLabel("WINSOME");
            _winsome_label.setPreferredSize(new Dimension(150, 100));
            _username_logged_label = new JLabel(String.format(
                    "logged as : @%s", (_gui.user(this) != null) ? _gui.user(this).username() : "not_logged"));
            _username_logged_label.setPreferredSize(new Dimension(150, 50));
            _username_newPost_label = new JLabel(String.format(
                    "@%s :", (_gui.user(this) != null) ? _gui.user(this).username() : "not_logged"));
            _username_newPost_label.setPreferredSize(new Dimension(150, 25));
            _winsome_logo_cp.add(_winsome_label, BorderLayout.CENTER);
            _userLogged_cp.add(_username_logged_label, BorderLayout.PAGE_START);
            _newPost_title_cp.add(_username_newPost_label, BorderLayout.WEST);

            _logout_button = new JButton("logout");
            _logout_button.setPreferredSize(new Dimension(150, 50));
            _logout_button.setActionCommand("logout_pressed");
            _logout_button.addActionListener(this);
            _newPost_post_button = new JButton("share");
            _newPost_post_button.setPreferredSize(new Dimension(150, 25));
            _newPost_post_button.setActionCommand("newpost_pressed");
            _newPost_post_button.addActionListener(this);
            _userLogged_cp.add(_logout_button, BorderLayout.PAGE_END);
            _newPost_cp.add(_newPost_post_button, BorderLayout.PAGE_END);

            _newPost_title_tf = new JTextField("newTitle");
            _newPost_title_tf.setPreferredSize(new Dimension(300, 25));
            _newPost_content_ta = new JTextArea("newPost");
            _newPost_content_ta.setPreferredSize(new Dimension(450, 100));
            _newPost_title_cp.add(_newPost_title_tf, BorderLayout.EAST);
            _newPost_content_cp.add(_newPost_content_ta, BorderLayout.CENTER);

            this.setPreferredSize(new Dimension(600, 500));

            update_blog(null);
        }

        public void update_blog(Blog blog) {
            // STUB
            Blog _blog = new Blog();
            _blog.add_post(new Post(new User(null, "pluto", ""), "plutoTitle", "plutoContent"));
            _blog.add_post(new Post(new User(null, "pippo", ""), "pippoTitle", "pippoContent"));
            _blog.add_post(new Post(new User(null, "paperino", ""), "paperinoTitle", "paperinoContent"));
            _blog.add_post(new Post(new User(null, "topolino", ""), "topolinoTitle", "topolinoContent"));
            _blog.add_post(new Post(new User(null, "minnie", ""), "minnieTitle", "minnieContent"));
            _blog.add_post(new Post(new User(null, "paperone", ""), "paperoneTitle", "paperoneContent"));
            _blog.add_post(new Post(new User(null, "paperina", ""), "paperinaTitle", "paperinaContent"));
            _blog.add_post(new Post(new User(null, "gastone", ""), "gastoneTitle", "gastoneContent"));

            // _blog_sp.removeAll();
            _blogArea_cp.removeAll();
            for (Post p : _blog.posts()) {
                _blogArea_cp.add(new WinsomeGUI_post(_gui, p));
            }

            // END STUB
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void reset() {
            // TODO Auto-generated method stub

        }

    }

    private class WinsomeGUI_RegisterScreen extends ClientGUI_page {
        private List<String> _tag_list;
        private JPanel _textfields_cp;
        private JPanel _username_cp;
        private JPanel _password_cp;
        private JPanel _tags_cp;
        private JPanel _add_tag_cp;
        private JPanel _buttons_cp;
        private JLabel _username_label;
        private JLabel _password_label;
        private JTextField _username_tf;
        private JPasswordField _password_pf;
        private JTextField _insert_tag_tf;
        private JTextArea _show_tags_tf;
        private JButton _add_tag_button;
        private JButton _clear_tags_button;
        private JButton _register_button;
        private JButton _back_to_login_button;

        public WinsomeGUI_RegisterScreen(JFrame window_frame, ClientGUI gui) {
            super(window_frame, gui);

            _tag_list = new ArrayList<>();

            // panels
            _textfields_cp = new JPanel(new BorderLayout());
            _username_cp = new JPanel(new BorderLayout());
            _password_cp = new JPanel(new BorderLayout());
            _tags_cp = new JPanel(new BorderLayout());
            _add_tag_cp = new JPanel(new BorderLayout());
            _buttons_cp = new JPanel(new BorderLayout());

            _textfields_cp.add(_username_cp, BorderLayout.PAGE_START);
            _textfields_cp.add(_password_cp, BorderLayout.PAGE_END);
            _tags_cp.add(_add_tag_cp, BorderLayout.PAGE_START);

            this.add(_textfields_cp, BorderLayout.PAGE_START);
            this.add(_tags_cp, BorderLayout.CENTER);
            this.add(_buttons_cp, BorderLayout.PAGE_END);

            // labels
            _username_label = new JLabel("desired username");
            _username_cp.add(_username_label, BorderLayout.WEST);
            _password_label = new JLabel("desired password");
            _password_cp.add(_password_label, BorderLayout.WEST);

            // textfields
            _username_tf = new JTextField("username");
            _username_cp.add(_username_tf, BorderLayout.EAST);
            _password_pf = new JPasswordField("password");
            _password_cp.add(_password_pf, BorderLayout.EAST);
            _insert_tag_tf = new JTextField("");
            _add_tag_cp.add(_insert_tag_tf/* , BorderLayout.WEST */);
            _show_tags_tf = new JTextArea("");
            _tags_cp.add(_show_tags_tf, BorderLayout.PAGE_END);

            // buttons
            _add_tag_button = new JButton("add tag");
            _add_tag_button.setActionCommand("add_tag_pressed");
            _add_tag_button.addActionListener(this);
            _add_tag_cp.add(_add_tag_button, BorderLayout.EAST);
            _clear_tags_button = new JButton("clear tags");
            _clear_tags_button.setActionCommand("clear_tags_pressed");
            _clear_tags_button.addActionListener(this);
            _add_tag_cp.add(_clear_tags_button, BorderLayout.PAGE_END);
            _register_button = new JButton("register");
            _register_button.setActionCommand("register_pressed");
            _register_button.addActionListener(this);
            _buttons_cp.add(_register_button, BorderLayout.EAST);
            _back_to_login_button = new JButton("back to login");
            _back_to_login_button.setActionCommand("login_pressed");
            _back_to_login_button.addActionListener(this);
            _buttons_cp.add(_back_to_login_button, BorderLayout.WEST);

        }

        public List<String> get_tags() {
            return new ArrayList<>(_tag_list);
        }

        private void add_tag() {
            String new_tag = _insert_tag_tf.getText();
            if (!new_tag.equals("") && _tag_list.size() > 4) {
                _insert_tag_tf.setText("reached maximum amount of tags");
            } else if (!new_tag.equals("")) {
                _tag_list.add(new_tag);
                System.out.printf("added new tag [%s]\n", new_tag);
            }

            update_tagList();
        }

        private void update_tagList() {
            StringBuilder sb = new StringBuilder();

            int i = 0;
            _tag_list.sort(Comparator.comparing(String::toString));
            for (String s : _tag_list) {
                i++;
                sb.append(String.format("-%d : %s\n", i, s));
            }
            _show_tags_tf.setText(sb.toString());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ("add_tag_pressed".equals(e.getActionCommand())) {
                add_tag();
            }
            if ("clear_tags_pressed".equals(e.getActionCommand())) {
                _tag_list.clear();
                update_tagList();
            }
            if ("login_pressed".equals(e.getActionCommand())) {
                _gui._login_screen.show();
            }

        }

        @Override
        public void reset() {
            _username_tf.setText("username");
            _password_pf.setText("password");
            _insert_tag_tf.setText("");
            _tag_list.clear();
            update_tagList();
        }
    }

    private class WinsomeGUI_LoginScreen extends ClientGUI_page {
        private JPanel _textfields_cp;
        private JPanel _buttons_cp;
        private JTextField _username_tf;
        private JPasswordField _password_pf;
        private JButton _login_button;
        private JButton _register_button;

        public WinsomeGUI_LoginScreen(JFrame window_frame, ClientGUI gui) {
            super(window_frame, gui);

            _textfields_cp = new JPanel(new BorderLayout());
            this.add(_textfields_cp, BorderLayout.PAGE_START);
            _buttons_cp = new JPanel(new BorderLayout());
            this.add(_buttons_cp, BorderLayout.PAGE_END);

            _username_tf = new JTextField("username");
            _textfields_cp.add(_username_tf, BorderLayout.PAGE_START);
            _password_pf = new JPasswordField("password");
            _textfields_cp.add(_password_pf, BorderLayout.PAGE_END);

            _login_button = new JButton("Login");
            _login_button.setActionCommand("loginPress");
            _login_button.addActionListener(this);
            _buttons_cp.add(_login_button, BorderLayout.PAGE_START);

            _register_button = new JButton("Register");
            _register_button.setActionCommand("registerPress");
            _register_button.addActionListener(this);
            _buttons_cp.add(_register_button, BorderLayout.PAGE_END);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if ("loginPress".equals(e.getActionCommand())) {
                // System.out.println("LOGIN_PRESSED");
                // System.out.printf("username = %s, password = %s\n", _username_tf.getText(),
                // new String(_password_pf.getPassword()));
                // try {
                // _gui.client(this).login(_username_tf.getText(), new
                // String(_password_pf.getPassword()));
                // } catch (WrongPassword e1) {
                // // TODO Auto-generated catch block
                // e1.printStackTrace();
                // _username_tf.setText("username o password errati");
                // _password_pf.setText("password");
                // }
                _gui._main_screen.show();
            }
            if ("registerPress".equals(e.getActionCommand())) {
                System.out.println("REGISTER_PRESSED");
                _gui._register_screen.show();
            }
        }

        @Override
        public void reset() {
            _username_tf.setText("username");
            _password_pf.setText("password");
        }

    }
}
