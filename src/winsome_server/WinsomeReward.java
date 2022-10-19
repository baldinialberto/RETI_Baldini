package winsome_server;

public class WinsomeReward {
	public double value;
	public final String username;

	public WinsomeReward(double value, String username) {
		this.value = value;
		this.username = username;
	}

	@Override
	public String toString()
	{
		return "Reward: " + value + " to " + username;
	}
}
