package winsome_server;

import java.sql.Timestamp;

public abstract class User_interaction implements JSON_Serializable {
	protected String username;
	protected Timestamp time_created;
}
