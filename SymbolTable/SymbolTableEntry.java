package SymbolTable;
import java.util.*;

public class SymbolTableEntry 
{
    public String name;
    public String type;

    public SymbolTableEntry parent_tableEntry;

    protected Map<String, ClassEntry> classes; //collection of (className -> ClassEntry)
    protected Map<String, MethodEntry> methods; //collection of (methodName -> MethodEntry)
    protected Map<String, VariableEntry> fields; //collection of (fieldName -> type)
    protected Map<String, VariableEntry> parameters; //collections of (variableName -> type)
    protected Map<String, VariableEntry> localVariables; //collections of (variableName -> type)

    public SymbolTableEntry(String name, String type, SymbolTableEntry parent_tableEntry)
    {
        this.name = name;
        this.type = type;
        this.parent_tableEntry = parent_tableEntry;
    }

    public boolean equalsType(String type)
    {
        return true;
    }

    public SymbolTableEntry get_tableEntry(String key)
    {
        return this;
    }

    public void insert_classEntry(String className, ClassEntry classEntry)
    {
        classes.put(className, classEntry);
    }

    public void insert_methodEntry(String methodName, MethodEntry methodEntry)
    {
        methods.put(methodName, methodEntry);
    }

    public void insert_variableEntry(String variableName, VariableEntry variableEntry)
    {
        return;
    }

    public void print()
    {
        return;
    }
}