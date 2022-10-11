package winsome_server;

public class Winsome_Reward {
	public double value;
	public String username;

	public Winsome_Reward(double value, String username) {
		this.value = value;
		this.username = username;
	}

	@Override
	public String toString()
	{
		return "Reward: " + value + " to " + username;
	}
}
