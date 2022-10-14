package winsome_comunication;

public class WinsomeExceptionGeneric extends WinsomeException {
	String message;

	public WinsomeExceptionGeneric(String message) {
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
