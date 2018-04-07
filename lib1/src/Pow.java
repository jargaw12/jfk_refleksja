@Description(description = "Metoda podnosi liczbę do podanej potęgi")
public class Pow implements ICallable{
    @Override
    public String Call(int a, int b) {
        return String.valueOf(Math.pow(a,b));
    }
}