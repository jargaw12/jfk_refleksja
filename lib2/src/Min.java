@Description(description = "Metoda zwraca minimalną liczbą z dwóch podanych")
public class Min implements ICallable {
    @Override
    public String Call(int a, int b) {
        if (a > b)
            return String.valueOf(b);
        else return String.valueOf(a);
    }
}
