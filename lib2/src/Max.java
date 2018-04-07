@Description(description = "Metoda zwraca maksymalną liczbą z dwóch podanych")
public class Max implements ICallable {
    @Override
    public String Call(int a, int b) {
        if (a < b)
            return String.valueOf(b);
        else return String.valueOf(a);
    }
}
