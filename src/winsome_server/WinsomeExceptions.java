package winsome_server;

public class WinsomeExceptions {
    public static class WrongPassword extends Exception {
        @Override
        public String toString() {
            return "WinSome : Wrong Password";
        }
    }

    public static class UsernameAlreadyExists extends Exception {
        @Override
        public String toString() {
            return "WinSome : Username already exists";
        }
    }

    public static class UserNotLogged extends Exception {
        @Override
        public String toString() {
            return "WinSome : User not logged yet";
        }
    }

    public static class UsernameNotFound extends Exception {
        @Override
        public String toString() {
            return "WinSome : Username not found";
        }
    }

    public static class UnauthorizedAction extends Exception {
        @Override
        public String toString() {
            return "Winsome : action not authorized by server";
        }
    }
}
