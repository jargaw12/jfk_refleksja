@Description(description = "Metoda dodaje dwie liczby")
public class Add implements ICallable {

    @Override
    public String Call(int a, int b) {
        return String.valueOf(a+b);
    }
}
