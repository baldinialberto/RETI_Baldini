package winsome;

public class Pair<A, B> {
    private A _a;
    private B _b;

    public Pair(A a, B b) {
        _a = a;
        _b = b;
    }

    public A get_a() {
        return _a;
    }

    public void set_a(A a) {
        _a = a;
    }

    public B get_b() {
        return _b;
    }

    public void set_b(B b) {
        _b = b;
    }
}
