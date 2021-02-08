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

            ClassesMap map = new ClassesMap(); //wrapper class to keep main function clean

            goalEntry.build_classesMap(map.classesMap); //use symbol table to build classesMap
            map.printMap();

            //root.accept(new TypeCheckerVisitor(), goalEntry);
            //goalEntry.print();
        }
        catch (ParseException e) 
        {
            System.out.println(e.toString());
        }
    }
}