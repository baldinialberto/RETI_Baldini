package winsome_DB;

import winsome_comunication.Winsome_Exception;

/**
 * This class contains all the exceptions that can be thrown by the Winsome_DB class.
 * The exceptions are:
 * 1. Wrong password = the password is wrong.
 * 2. User already exists = the user already exists in the database.
 * 3. User not found = the user was not found in the database.
 * 4. ...
 * Each Winsome_Exception has a code and a default message.
 * <p></p>
 * These exceptions are used to communicate with the server.
 */
public class Winsome_DB_Exception {
	public static class WrongPassword extends Winsome_Exception {
		@Override
		public String getMessage() {
			return "WinSome 01 : Wrong Password";
		}
		public String niceMessage() {
			return "Wrong Password";
		}
	}

	public static class UsernameAlreadyExists extends Winsome_Exception {
		@Override
		public String getMessage() {
			return "WinSome 02 : Username already exists";
		}
		public String niceMessage() {
			return "Username already exists";
		}
	}

	public static class UsernameNotFound extends Winsome_Exception {
		private final String username;

		public UsernameNotFound(String username) {
			this.username = username;
		}
		@Override
		public String getMessage() {
			return "WinSome 03 : Username " + username + " not found";
		}
		public String niceMessage() {
			return "Username " + username + " not found";
		}
	}

	public static class UsernameAlreadyFollows extends Winsome_Exception {
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
		public String niceMessage() {
			return "You already follow " + username_to_follow;
		}
	}

	public static class UsernameNotFollowing extends Winsome_Exception {
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
		public String niceMessage() {
			return "You are not following " + username_to_unfollow;
		}
	}

	public static class PostInvalidTitle extends Winsome_Exception {
		private final String title;

		public PostInvalidTitle(String title) {
			this.title = title;
		}

		@Override
		public String getMessage() {
			return "WinSome 06 : " + title + " is not a valid title";
		}
		public String niceMessage() {
			return "Invalid title, must be between 1 and " + Post_DB.TITLE_MAX_LENGTH + " characters";
		}
	}

	public static class PostInvalidContent extends Winsome_Exception {
		private final String content;

		public PostInvalidContent(String content) {
			this.content = content;
		}

		@Override
		public String getMessage() {
			return "WinSome 07 : " + content + " is not a valid content";
		}
		public String niceMessage() {
			return "Invalid content, must be between 1 and " + Post_DB.CONTENT_MAX_LENGTH + " characters";
		}
	}

	public static class PostNotFound extends Winsome_Exception {
		private final String postId;

		public PostNotFound(String postId) {
			this.postId = postId;
		}

		@Override
		public String getMessage() {
			return "WinSome 08 : " + postId + " was not found";
		}
		public String niceMessage() {
			return "Post not found";
		}
	}

	public static class PostNotInBlog extends Winsome_Exception {
		private final String postId;
		private final String username;

		public PostNotInBlog(String postId, String username) {
			this.postId = postId;
			this.username = username;
		}

		@Override
		public String getMessage() {
			return "WinSome 09 : " + postId + " is not in the blog of " + username;
		}
		public String niceMessage() {
			return "You have not posted this post";
		}
	}

	public static class PostAlreadyRated extends Winsome_Exception {
		private final String postId;
		private final String username;

		public PostAlreadyRated(String postId, String username) {
			this.postId = postId;
			this.username = username;
		}

		@Override
		public String getMessage() {
			return "WinSome 10 : " + username + " already voted on " + postId;
		}
		public String niceMessage() {
			return "You already voted on this post";
		}
	}

	public static class PostAlreadyRewined extends Winsome_Exception {
		private final String postId;
		private final String username;

		public PostAlreadyRewined(String postId, String username) {
			this.postId = postId;
			this.username = username;
		}

		@Override
		public String getMessage() {
			return "WinSome 11 : " + username + " already rewined " + postId;
		}
		public String niceMessage() {
			return "You already rewined this post";
		}
	}

	public static class PostCommentedByAuthor extends Winsome_Exception {
		private final String postId;
		private final String username;

		public PostCommentedByAuthor(String postId, String username) {
			this.postId = postId;
			this.username = username;
		}

		@Override
		public String getMessage() {
			return "WinSome 12 : " + username + " cannot comment his own post (" + postId + ")";
		}
		public String niceMessage() {
			return "You cannot comment your own post";
		}
	}

	public static class UsersDatabaseNotFound extends Winsome_Exception {
		@Override
		public String getMessage() {
			return "WinSome 13 : Users database not found";
		}
		public String niceMessage() {
			return "Problem with the database, please try again later";
		}
	}

	public static class PostsDatabaseNotFound extends Winsome_Exception {
		@Override
		public String getMessage() {
			return "WinSome 14 : Posts database not found";
		}
		public String niceMessage() {
			return "Problem with the database, please try again later";
		}
	}
	
	public static class DatabaseNotInitialized extends Winsome_Exception {
		@Override
		public String getMessage() {
			return "WinSome 15 : Database not initialized";
		}
		public String niceMessage() {
			return "Problem with the database, please try again later";
		}
	}

	public static class DatabaseNotSaved extends Winsome_Exception {
		@Override
		public String getMessage() {
			return "WinSome 16 : Database not saved";
		}
		public String niceMessage() {
			return "Problem with the database, please try again later";
		}
	}

	public static class GenericException extends Winsome_Exception {
		private final String message;

		public GenericException(String message) {
			this.message = message;
		}

		@Override
		public String getMessage() {
			return "WinSome : " + message;
		}
		public String niceMessage() {
			return message;
		}
	}


}
