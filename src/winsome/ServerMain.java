package winsome;

public class ServerMain {
    public static void main(String[] args) {
        Server s = new Server("config.txt");
        System.out.println(s.get_properties_toString());
    }
}
