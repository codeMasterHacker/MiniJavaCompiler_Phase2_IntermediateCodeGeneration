import SymbolTable.*;
import VTable.*;
import syntaxtree.*;
import visitor.*;

public class J2V 
{
    public static void main(String [] args) 
    {
        try 
        {
            MiniJavaParser mjp = new MiniJavaParser(System.in);

            GoalEntry goalEntry = new GoalEntry("", "", null);

            Goal root = mjp.Goal();
            root.accept(new SymbolTableVisitor(), goalEntry); //builds symbol table

            ClassesMapManager mapManager = new ClassesMapManager(); 

            goalEntry.build_classesMap(mapManager.classesMap); //use symbol table to build classesMap

            root.accept(new VaporVisitor(mapManager), goalEntry);

            //root.accept(new TypeCheckerVisitor(), goalEntry);
            //mapManager.printMap();
            //goalEntry.print();
        }
        catch (ParseException e) 
        {
            System.out.println(e.toString());
        }
    }
}