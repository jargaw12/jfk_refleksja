@Description(description = "Metoda odejmuje dwie liczby")
public class Sub implements ICallable {
    @Override
    public String Call(int a, int b) {
        return String.valueOf(a-b);
    }
}