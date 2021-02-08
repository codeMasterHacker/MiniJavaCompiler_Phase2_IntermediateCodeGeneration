package VTable;
import java.util.*;

public class ClassesMap 
{
    //this will represent figure 14.3 in the book
    public Map<String, ClassObject> classesMap;
    
    public ClassesMap()
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
}
