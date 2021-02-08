package VTable;
import java.util.*;

public class ClassesMapManager 
{
    //this will represent figure 14.3 in the book
    public Map<String, ClassObject> classesMap;
    
    public ClassesMapManager()
    {
        classesMap = new LinkedHashMap<>();
    }

    public void printMap()
    {
        System.out.println("Classes:");
        classesMap.forEach( (key, value) -> //iterate thru classes
        {
            System.out.println("\t" + key);

            System.out.println("\t\tFields:");
            for (FieldMethod field: value.classFields) //iterate thru fields
            {
                System.out.println("\t\t\t" + field.className + "_" + field.fieldMethod_name);
            }

            System.out.println("\t\tMethods:");
            for (FieldMethod method: value.classVTable.classMethods) //iterate thru methods
            {
                System.out.println("\t\t\t" + method.className + "_" + method.fieldMethod_name);
            }
        });
    }

    public StringBuilder getVTable()
    {
        StringBuilder vTable = new StringBuilder();

        for (Map.Entry<String, ClassObject> classElement : classesMap.entrySet()) 
        { 
            String className = classElement.getKey(); 
            ClassObject classObject = classElement.getValue();
            
            if (classObject.classVTable.classMethods.isEmpty())
            {
                vTable.append("\nconst empty_" + className + "\n\n");
                continue;
            }

            vTable.append("\nconst vmt_" + className + "\n");

            for (FieldMethod method: classObject.classVTable.classMethods) //iterate thru methods
                vTable.append("  :" + method.className + "." + method.fieldMethod_name + "\n");
        }

        vTable.append("\n\n");

        return vTable;
    }
}
