package SymbolTable;
import java.util.*;

public class ClassEntry extends SymbolTableEntry 
{
    public boolean is_mainClass;
    public String parentClass_name; //in case the class extends another class, inheritence

    private SymbolTableEntry parentClass_entry; //points to the parent class, the class its extending from

    public ClassEntry(String className, String type, SymbolTableEntry parent_tableEntry, boolean is_mainClass, String parent_className)
    {
        super(className, type, parent_tableEntry);

        this.is_mainClass = is_mainClass;
        this.parentClass_name = parent_className;

        if (!this.parentClass_name.isEmpty())
            parentClass_entry = parent_tableEntry.get_tableEntry(this.parentClass_name);
        
        methods = new LinkedHashMap<>();
        fields = new LinkedHashMap<>();
    }

    @Override
    public boolean equalsType(String type)
    {
        if (this.type.equals(type))
            return true;
        else if (parentClass_entry != null)
            return parentClass_entry.equalsType(type);
        else
            return false;
    }

    //given a name, return the appropriate type of symbol table entry
    @Override
    public SymbolTableEntry get_tableEntry(String name)
    {
        VariableEntry variableEntry = fields.get(name);
        MethodEntry methodEntry;
    
        if (variableEntry != null) 
            return variableEntry; 
        else 
	    { 
            methodEntry = methods.get(name);
        
            if (methodEntry != null) 
                return methodEntry; 
            else 
                return parent_tableEntry.get_tableEntry(name); //return a class entry object
	    }
    }

    @Override
    public void insert_classEntry(String className, ClassEntry classEntry) { return; }

    @Override
    public void insert_methodEntry(String methodName, MethodEntry methodEntry)
    {
        Set<String> methodNames = this.methods.keySet();

        if (methodNames.contains(methodName))
        {
            this.methods.remove(methodName); //remove the parent's function, since the child is overriding
            super.insert_methodEntry(methodName, methodEntry); //now insert thr child overriden method
        }
        else
            super.insert_methodEntry(methodName, methodEntry);
    }

    @Override
    public void insert_variableEntry(String fieldName, VariableEntry variableEntry)
    {
        fields.put(fieldName, variableEntry);
    }

    public void insert_FieldsMethods(ClassEntry parentClass_entry)
    {
        parentClass_entry.fields.forEach( (key, value) -> { insert_variableEntry(key, value); });

        parentClass_entry.methods.forEach( (key, value) -> { insert_methodEntry(key, value); });
    }

    @Override
    public void print()
    {
        System.out.println("\t\tFields:");
        fields.forEach( (key, value) -> 
        {
            System.out.println("\t\t\t" + value.type + " " + key);
            value.print();
        });

        System.out.println("\t\tMethods:");
        methods.forEach( (key, value) -> 
        {
            System.out.println("\t\t\t" + value.type + " " + key);
            value.print();
        });
    }
}