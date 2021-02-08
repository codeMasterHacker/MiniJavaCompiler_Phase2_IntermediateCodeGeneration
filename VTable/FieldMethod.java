package VTable;

public class FieldMethod 
{
    public String fieldMethod_name;
    public String className;

    public FieldMethod(String fieldMethod_name, String className)
    {
        this.fieldMethod_name = fieldMethod_name;
        this.className = className;
    }

    public String get_fieldMethod_name() { return fieldMethod_name; }

    public String get_className() { return className; }
}
