package winsome_comunication;

public class Winsome_Exception_Generic extends Winsome_Exception {
	String message;

	public Winsome_Exception_Generic(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String niceMessage() {
		return message;
	}
}
