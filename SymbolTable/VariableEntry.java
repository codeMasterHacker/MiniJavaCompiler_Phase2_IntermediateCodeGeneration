package SymbolTable;

public class VariableEntry extends SymbolTableEntry 
{
    public boolean isParameter;

    //Phase 2
    String originClass_name;
    //Pahse 2

    public VariableEntry(String variableName, String type, SymbolTableEntry parent_tableEntry, boolean isParameter, String originClass_name)
    {
        super(variableName, type, parent_tableEntry);

        this.isParameter = isParameter;

        this.originClass_name = originClass_name;
    }

    @Override
    public SymbolTableEntry get_tableEntry(String name) 
    { 
        return parent_tableEntry.get_tableEntry(name); 
    }

    @Override
    public void insert_classEntry(String className, ClassEntry classEntry) { return; }

    @Override
    public void insert_methodEntry(String methodName, MethodEntry methodEntry) { return; }
}