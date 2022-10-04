package winsome_DB;

/**
 * This class contains all the exceptions that can be thrown by the Winsome_DB class.
 * The exceptions are:
 * 1. Wrong password = the password is wrong.
 * 2. User already exists = the user already exists in the database.
 * 3. User not found = the user was not found in the database.
 * 4. ...
 * Each exception has a code and a default message.
 * <p></p>
 * These exceptions are used to communicate with the server.
 */
public class Winsome_DB_Exception {
	public static class WrongPassword extends Exception {
		@Override
		public String getMessage() {
			return "WinSome 01 : Wrong Password";
		}
	}

	public static class UsernameAlreadyExists extends Exception {
		@Override
		public String getMessage() {
			return "WinSome 02 : Username already exists";
		}
	}

	public static class UsernameNotFound extends Exception {
		@Override
		public String getMessage() {
			return "WinSome 03 : Username not found";
		}
	}

	public static class UsernameAlreadyFollows extends Exception {
		private final String username;
		private final String username_to_follow;

		public UsernameAlreadyFollows(String username, String username_to_follow) {
			this.username = username;
			this.username_to_follow = username_to_follow;
		}

		@Override
		public String getMessage() {
			return "WinSome 04 : " + username + " already follows " + username_to_follow;
		}
	}

	public static class UsernameNotFollowing extends Exception {
		private final String username;
		private final String username_to_unfollow;

		public UsernameNotFollowing(String username, String username_to_unfollow) {
			this.username = username;
			this.username_to_unfollow = username_to_unfollow;
		}

		@Override
		public String getMessage() {
			return "WinSome 05 : " + username + " is not following " + username_to_unfollow;
		}
	}

	public static class PostInvalidTitle extends Exception {
		private final String title;

		public PostInvalidTitle(String title) {
			this.title = title;
		}

		@Override
		public String getMessage() {
			return "WinSome 06 : " + title + " is not a valid title";
		}
	}

	public static class PostInvalidContent extends Exception {
		private final String content;

		public PostInvalidContent(String content) {
			this.content = content;
		}

		@Override
		public String getMessage() {
			return "WinSome 07 : " + content + " is not a valid content";
		}
	}

	public static class PostNotFound extends Exception {
		private final String postId;

		public PostNotFound(String postId) {
			this.postId = postId;
		}

		@Override
		public String getMessage() {
			return "WinSome 08 : " + postId + " was not found";
		}
	}

	public static class PostNotOwned extends Exception {
		private final String postId;
		private final String username;

		public PostNotOwned(String postId, String username) {
			this.postId = postId;
			this.username = username;
		}

		@Override
		public String getMessage() {
			return "WinSome 09 : " + postId + " is not owned by " + username;
		}
	}

	public static class PostAlreadyVoted extends Exception {
		private final String postId;
		private final String username;

		public PostAlreadyVoted(String postId, String username) {
			this.postId = postId;
			this.username = username;
		}

		@Override
		public String getMessage() {
			return "WinSome 10 : " + username + " already voted on " + postId;
		}
	}

	public static class PostCommentedByAuthor extends Exception {
		private final String postId;
		private final String username;

		public PostCommentedByAuthor(String postId, String username) {
			this.postId = postId;
			this.username = username;
		}

		@Override
		public String getMessage() {
			return "WinSome 11 : " + username + " cannot comment his own post (" + postId + ")";
		}
	}
}
