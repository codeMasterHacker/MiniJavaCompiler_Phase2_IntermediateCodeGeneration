package SymbolTable;
import java.util.*;

public class MethodEntry extends SymbolTableEntry 
{
    public boolean is_mainMethod;

    //Phase 2
    public String originClass_name;
    //Phase 2

    public MethodEntry(String methodName, String type, SymbolTableEntry parent_tableEntry, boolean is_mainMethod, String originClass_name)
    {
        super(methodName, type, parent_tableEntry);

        this.is_mainMethod = is_mainMethod;

        this.originClass_name = originClass_name;

        parameters = new LinkedHashMap<>(); //collections of (variableName -> type)
        localVariables = new LinkedHashMap<>(); //collections of (variableName -> type)
    }

    //given a name, return the appropriate type of symbol table entry
    @Override
    public SymbolTableEntry get_tableEntry(String name)
    {
        SymbolTableEntry tableEntry = localVariables.get(name);
        
        if (tableEntry != null) 
            return tableEntry; 
	    else 
	    {
            tableEntry = parameters.get(name);
            
            if (tableEntry != null) 
                return tableEntry; 
            else 
                return parent_tableEntry.get_tableEntry(name); 
                //if name belongs to a field, return a field entry object
                //else if name belongs to a method, return a method entry object
                //else return a class entry object
	    }
    }

    @Override
    public void insert_variableEntry(String variableName, VariableEntry variableEntry) 
    {
        if (variableEntry.isParameter)
            parameters.put(variableName, variableEntry);
        else
            localVariables.put(variableName, variableEntry);
    }

    @Override
    public void insert_methodEntry(String methodName, MethodEntry methodEntry) { return; }

    @Override
    public void insert_classEntry(String className, ClassEntry classEntry) { return; }

    public boolean checkParameters(String ts, SymbolTableEntry argu)
    {
        //boolean flag = true;
        int i = 0;

        ts = ts.trim();
        ArrayList<String> types = new ArrayList<>(Arrays.asList(ts.split(" ")));

        while (i < types.size() && !types.isEmpty())
        {
            if (types.get(i).isEmpty())
                types.remove(i);
            else
                i++;
        }

        // if (ts.isEmpty())
        //     System.out.println("Parameters Recieved is empty");

        Collection<VariableEntry> values = parameters.values();

        if (types.size() != values.size())
        {
            // System.out.println("Recieved Size: " + types.size());
            // System.out.println("Actual Size: " + values.size());

            return false;
        }

        // for (String s : types)
        // {
        //     System.out.println("Recieved: " + s);
        // }

        //String var;
        SymbolTableEntry entry;
        i = 0;

        for (VariableEntry parameter : values) //implement rule 7.7 (35)
        {
            // System.out.println("Recieved: " + types.get(i));
            // System.out.println("Conversion: " + var);
            // System.out.println("Actually: " + parameter.type);

            entry = argu.get_tableEntry(types.get(i));
            
            if (entry == null) //variable name doesn't exist, so must be a type
            {
                //var = types.get(i);

                if (!types.get(i).equals(parameter.type))
                {
                    System.out.println("\nType Checking Error at Expression List for Parameters");
                    System.out.println("The parameter is of type '" + types.get(i) + "'");
                    System.out.println("The expression must be of type '" + parameter.type + "'");

                    return false;
                    //break;
                }
            }
            else //variable name exists, so check its type
            {
                //types.get(i) (entry) must be subtype of parameter.type

                if (!entry.equalsType(parameter.type))
                {
                    System.out.println("\nType Checking Error at Expression List for Parameters");
                    System.out.println("The parameter is of type '" + entry.type + "'");
                    System.out.println("The expression must be of type '" + parameter.type + "'");

                    return false;
                    //break;
                }
            }

            i++;
        }

        return true;
    }

    @Override
    public void print()
    {
        System.out.println("\t\t\t\tParameters:");
        parameters.forEach( (key, value) -> 
        {
            System.out.println("\t\t\t\t\t" + value.type + " " + key);
            value.print();
        });

        System.out.println("\t\t\t\tLocal Variables:");
        localVariables.forEach( (key, value) -> 
        {
            System.out.println("\t\t\t\t\t" + value.type + " " + key);
            value.print();
        });
    }
}