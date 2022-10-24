package winsome_communication;

public class WinsomeExceptionGeneric extends WinsomeException {
	final String message;

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
