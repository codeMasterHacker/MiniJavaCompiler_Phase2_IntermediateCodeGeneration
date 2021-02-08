package SymbolTable;
import VTable.*;
import java.util.*;

public class GoalEntry extends SymbolTableEntry
{
    public GoalEntry(String name, String type, SymbolTableEntry parent_tableEntry)
    {
        super(name, type, parent_tableEntry);
        classes = new LinkedHashMap<>();
    }

    //given a class's name, return its class entry object
    @Override
    public SymbolTableEntry get_tableEntry(String className) 
    { 
        return classes.get(className);
    }

    @Override
    public void insert_methodEntry(String methodName, MethodEntry methodEntry) { return; }

    @Override
    public void insert_variableEntry(String variableName, VariableEntry variableEntry) { return; }

    @Override
    public void print()
    {
        System.out.println("\n\n\nClasses:");
        classes.forEach( (key, value) -> 
        {
            if (value.parentClass_name.isEmpty())
                System.out.println("\t" + key);
            else
                System.out.println("\t" + key + " extends " + value.parentClass_name);
            value.print();
        });
    }

    public void build_classesMap(Map<String, ClassObject> map)
    {
        classes.forEach( (key, value) -> //value is just the class that contains the inherited data
        {
            ClassObject classObject = new ClassObject(key);

            if (value.fields.size() > 0) //the class has fields
            {
                for (Map.Entry<String, VariableEntry> variableEntry_element : value.fields.entrySet()) 
                { 
                    String variableName = variableEntry_element.getKey(); 
                    VariableEntry variableEntry = variableEntry_element.getValue();

                    classObject.insertField(new FieldMethod(variableName, variableEntry.originClass_name));
                }
            }

            if (value.methods.size() > 0) // the class has methods
            {
                for (Map.Entry<String, MethodEntry> methodEntry_element : value.methods.entrySet()) 
                { 
                    String methodName = methodEntry_element.getKey(); 
                    MethodEntry methodEntry = methodEntry_element.getValue();

                    classObject.get_classVTable().insertMethod(new FieldMethod(methodName, methodEntry.originClass_name));
                }
            }

            map.put(key, classObject);
        });
    }
}