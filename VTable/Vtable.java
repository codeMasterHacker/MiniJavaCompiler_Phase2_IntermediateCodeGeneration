package VTable;
import java.util.*;

public class Vtable 
{
    public List<FieldMethod> classMethods;

    public Vtable() { classMethods = new ArrayList<>(); }

    public void insertMethod(FieldMethod method) { classMethods.add(method); }
}
