package VTable;
import java.util.*;

public class ClassObject 
{
    public String className;
    public List<FieldMethod> classFields;
    public Vtable classVTable;

    public ClassObject(String className) 
    {
        this.className = className;
        classFields = new ArrayList<>(); 
        classVTable = new Vtable();
    }

    public void insertField(FieldMethod field) { classFields.add(field); }

    public Vtable get_classVTable() { return classVTable; }
}
