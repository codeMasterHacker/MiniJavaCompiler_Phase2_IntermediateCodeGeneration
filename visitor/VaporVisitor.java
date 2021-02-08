package visitor;
import SymbolTable.*;
import syntaxtree.*;
import VTable.*;
import java.util.*;

public class VaporVisitor implements GJVisitor<String, SymbolTableEntry> 
{
    SymbolTableEntry currentClass;
    SymbolTableEntry currentMethod;

    ClassesMapManager classesMap_manager;
    StringBuilder vaporCode;

    int indentationCount = 0;
    int ifCount = 1;
    int whileCount = 1;

    public VaporVisitor(ClassesMapManager mM)
    {
        classesMap_manager = mM;
        vaporCode = classesMap_manager.getVTable();
    }

    /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
    @Override
    public String visit(Goal n, SymbolTableEntry goal)
    {
        n.f0.accept(this, goal); //* f0 -> MainClass()
        n.f1.accept(this, goal); //* f1 -> ( TypeDeclaration() )*

        System.out.println(vaporCode.toString()); //prints the trnslated vapor code

        return "";
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
    @Override
    public String visit(MainClass n, SymbolTableEntry goal)
    {
        String className = n.f1.accept(this, null); //* f1 -> Identifier(), will return class name

        //goal can only return class entry objects
        SymbolTableEntry classEntry = goal.get_tableEntry(className);
        SymbolTableEntry methodEntry = classEntry.get_tableEntry("main");
        //classEntry can only return method entry objects because this is a class node

        currentClass = classEntry;
        currentMethod = methodEntry;

        vaporCode.append("func Main()\n");
        indentationCount += 2;

        // n.f11.accept(this, classEntry); //* f11 -> Identifier()
        n.f14.accept(this, methodEntry); //* f14 -> ( VarDeclaration() )*
        n.f15.accept(this, methodEntry); //* f15 -> ( Statement() )*

        vaporCode.append("  ret\n\n");
        if (indentationCount > 1)
            indentationCount -= 2;

        return "";
    }

    /**
    * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
    @Override
    public String visit(TypeDeclaration n, SymbolTableEntry goal) 
    {
        // n.f0.accept(this, goal);

        // return "";
        return n.f0.accept(this, goal);
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
    @Override
    public String visit(ClassDeclaration n, SymbolTableEntry goal) 
    {
        String className = n.f1.accept(this, null); //* f1 -> Identifier(), will return class name

        //goal can only return class entry objects
        SymbolTableEntry classEntry = goal.get_tableEntry(className);

        currentClass = classEntry;

        n.f3.accept(this, classEntry); //* f3 -> ( VarDeclaration() )*
        n.f4.accept(this, classEntry); //* f4 -> ( MethodDeclaration() )*

        return "";
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
    @Override
    public String visit(ClassExtendsDeclaration n, SymbolTableEntry goal) 
    {
        String className = n.f1.accept(this, null); //* f1 -> Identifier(), will return class name
        //String parent_className = n.f3.accept(this, null); //* f3 -> Identifier(), will return parent class name

        //goal can only return class entry objects
        SymbolTableEntry classEntry = goal.get_tableEntry(className);

        currentClass = classEntry;

        n.f5.accept(this, classEntry); //* f5 -> ( VarDeclaration() )*
        n.f6.accept(this, classEntry); //* f6 -> ( MethodDeclaration() )*

        return "";
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    @Override
    public String visit(VarDeclaration n, SymbolTableEntry classMethod) 
    {
        n.f0.accept(this, null); //* f0 -> Type()
        n.f1.accept(this, null); //* f1 -> Identifier()

        return "";
    }

    /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
    @Override
    public String visit(MethodDeclaration n, SymbolTableEntry classEntry)
    {
        //String returnType = n.f1.accept(this, null); //* f1 -> Type()
        n.f1.accept(this, null); //* f1 -> Type()
        String methodName = n.f2.accept(this, null); //* f2 -> Identifier()

        //classEntry returns a method entry object because a method entry was inserted at this node
        SymbolTableEntry methodEntry = classEntry.get_tableEntry(methodName);

        currentMethod = methodEntry;

        String parameters = n.f4.accept(this, methodEntry); //* f4 -> ( FormalParameterList() )?

        vaporCode.append("func " + classEntry.name + "." + methodName + "(this " + parameters + ")\n");
        indentationCount += 2;

        n.f7.accept(this, methodEntry); //* f7 -> ( VarDeclaration() )*
        n.f8.accept(this, methodEntry); //* f8 -> ( Statement() )*

        String ret = n.f10.accept(this, methodEntry); //* f10 -> Expression()
        //will return something correct, since we're given a syntactically correct MiniJava program

        vaporCode.append("  ret " + ret + "\n\n");
        if (indentationCount > 1)
            indentationCount -= 2;

        return "";
    }

    /**
    * f0 -> FormalParameter()
    * f1 -> ( FormalParameterRest() )*
    */
    @Override
    public String visit(FormalParameterList n, SymbolTableEntry methodEntry)
    {
        String oneParameter = n.f0.accept(this, methodEntry); //* f0 -> FormalParameter()
        if (oneParameter.isEmpty())
            return "";

        String multipleParameters = n.f1.accept(this, methodEntry); //* f1 -> ( FormalParameterRest() )*

        if (multipleParameters.isEmpty())
            return oneParameter;

        return oneParameter + " " + multipleParameters;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
    @Override
    public String visit(FormalParameter n, SymbolTableEntry methodEntry) 
    {
        //n.f0.accept(this, null); //* f0 -> Type()
        return n.f1.accept(this, null); //* f1 -> Identifier()
    }

    /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
    @Override
    public String visit(FormalParameterRest n, SymbolTableEntry methodEntry) 
    {
        // n.f1.accept(this, methodEntry); //* f1 -> FormalParameter()

        // return "";
        return n.f1.accept(this, methodEntry); //* f1 -> FormalParameter()
    }

    /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
    @Override
    public String visit(Type n, SymbolTableEntry argu) 
    {
        return n.f0.accept(this, argu);
    }

    /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
    @Override
    public String visit(ArrayType n, SymbolTableEntry argu) 
    {
        return "int[]";
    }

    /**
    * f0 -> "boolean"
    */
    @Override
    public String visit(BooleanType n, SymbolTableEntry argu) 
    {
        return "boolean";
    }

    /**
    * f0 -> "int"
    */
    @Override
    public String visit(IntegerType n, SymbolTableEntry argu) 
    {
        return "int";
    }

    /**
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
    @Override
    public String visit(Statement n, SymbolTableEntry methodEntry) 
    {
        return n.f0.accept(this, methodEntry);
    }

    /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
    @Override
    public String visit(Block n, SymbolTableEntry methodEntry) 
    {
        indentationCount += 2;
        String ret = n.f1.accept(this, methodEntry); //f1 -> ( Statement() )*
        indentationCount -= 2;

        return ret;
    }

    /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    @Override
    public String visit(AssignmentStatement n, SymbolTableEntry methodEntry) //TODO--216.0000
    {
        String variableName = n.f0.accept(this, methodEntry); //* f0 -> Identifier()
        String equalsExpression = n.f2.accept(this, methodEntry); //* f2 -> Expression()

        SymbolTableEntry idEntry;
        SymbolTableEntry exEntry;
        // String varType = "";
        // String exType = "";

        idEntry = methodEntry.get_tableEntry(variableName);

        if (idEntry == null) //variableName doesn't exist
        {
            System.out.println("\nType Checking Error at Assignment Statement");
            System.out.println("The variable '" + variableName + "' doesn't exist");
        }
        else //variableName exists
        {
            exEntry = methodEntry.get_tableEntry(equalsExpression);

            if (exEntry == null) //equalsExpression doesn't exist, so must be a type
            {
                if (!idEntry.type.equals(equalsExpression))
                {
                    System.out.println("\nType Checking Error at Assignment Statement");
                    System.out.println("The variable '" + variableName + "' is of type '" + idEntry.type + "'");
                    System.out.println("The expression returns type '" + equalsExpression + "'");
                }
            }
            else //equalsExpression exists, so check its type
            {
                if (!exEntry.equalsType(idEntry.type))
                {
                    System.out.println("\nType Checking Error at Assignment Statement");
                    System.out.println("The variable '" + variableName + "' is of type '" + idEntry.type + "'");
                    System.out.println("The expression returns type '" + exEntry.type + "'");
                }
            }
        }
        // entry = methodEntry.get_tableEntry(equalsExpression);

        // if (entry == null) //equalsExpression doesn't exist, so must be a type
        //     exType = equalsExpression;
        // else //equalsExpression exists, so check its type
        //     exType = entry.type;

        // if (!varType.equals(exType))
        // {
        //     System.out.println("\nType Checking Error at Assignment Statement");
        //     System.out.println("The variable '" + variableName + "' is of type '" + varType + "'");
        //     System.out.println("The expression returns type '" + exType + "'");
        // }

        return "";
    }

    /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
    @Override
    public String visit(ArrayAssignmentStatement n, SymbolTableEntry methodEntry) //TODO--420.00
    {
        String arrayName = n.f0.accept(this, methodEntry); //* f0 -> Identifier()
        String indexExpression = n.f2.accept(this, methodEntry); //* f2 -> Expression()
        String equalsExpression = n.f5.accept(this, methodEntry); //* f5 -> Expression()
        //indexExpression and equalsExpression might be variables or int types

        //in case indexExpression or equalsExpression are variables
        SymbolTableEntry entry;
        String type;

        entry = methodEntry.get_tableEntry(arrayName);

        if (entry == null) //arrayName doesn't exist
        {
            System.out.println("\nType Checking Error at Array Assignment Statement");
            System.out.println("The array '" + arrayName + "' doesn't exist");
        }
        else //arrayName exists, so check its type is int[]
        {
            type = entry.type;

            if (!type.equals("int[]"))
            {
                System.out.println("\nType Checking Error at Array Assignment Statement");
                System.out.println("The array '" + arrayName + "' is of type '" + type + "'");
                System.out.println("The array must be of type 'int[]'");
            }
        }

        entry = methodEntry.get_tableEntry(indexExpression);

        if (entry == null) //indexExpression doesn't exist, so must be a type
            type = indexExpression;
        else //indexExpression exists, so check its type
            type = entry.type;

        if (!type.equals("int"))
        {
            System.out.println("\nType Checking Error at Array Assignment Statement");
            System.out.println("The index evaluates to type '" + type +"'");
            System.out.println("The index evaluate to type 'int'");
        }

        entry = methodEntry.get_tableEntry(equalsExpression);

        if (entry == null) //equalsExpression doesn't exist, so must be a type
            type = equalsExpression;
        else //equalsExpression exists, so check its type
            type = entry.type;

        if (!type.equals("int"))
        {
            System.out.println("\nType Checking Error at Array Assignment Statement");
            System.out.println("The array is set equal to type '" + type +"'");
            System.out.println("The array must be set equal to type 'int'");
        }

        return "";
    }

    /**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
    @Override
    public String visit(IfStatement n, SymbolTableEntry methodEntry) //TODO
    {
        String bool = n.f2.accept(this, methodEntry); //* f2 -> Expression()

        for (int i = 0; i < indentationCount; i++)
            vaporCode.append(" ");
        vaporCode.append("if0 " + bool + " goto :if" + ifCount + "_else\n");

        n.f4.accept(this, methodEntry); //* f4 -> Statement()



        n.f6.accept(this, methodEntry); //* f6 -> Statement()

        return "";
    }

    /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
    @Override
    public String visit(WhileStatement n, SymbolTableEntry methodEntry) //TODO
    {
        //bool can be a variable or a type
        String bool = n.f2.accept(this, methodEntry); //* f2 -> Expression()

        for (int i = 0; i < indentationCount; i ++)
            vaporCode.append(" ");
        vaporCode.append("while" + whileCount + "_top:\n");
        vaporCode.append("if0 " + bool + " goto :while" + whileCount + "_end:\n");
        indentationCount += 2;

        







        n.f4.accept(this, methodEntry); //* f4 -> Statement()

        return "";
    }

    /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
    @Override
    public String visit(PrintStatement n, SymbolTableEntry methodEntry) 
    {
        String integer = n.f2.accept(this, methodEntry); //* f2 -> Expression()
        //integer can be a variable or type

        for (int i = 0; i < indentationCount; i++)
            vaporCode.append(" ");
        vaporCode.append("PrintIntS(" + integer + ")\n");

        return integer;
    }

    /**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | PrimaryExpression()
    */
    @Override
    public String visit(Expression n, SymbolTableEntry argu) 
    {
        return n.f0.accept(this, argu);
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(AndExpression n, SymbolTableEntry argu) 
    {
        String bool1 = n.f0.accept(this, argu); //* f0 -> PrimaryExpression()
        String bool2 = n.f2.accept(this, argu); //* f2 -> PrimaryExpression()

        SymbolTableEntry entry;
        String type;

        entry = argu.get_tableEntry(bool1);

        if (entry == null) //the variable name doesn't exist, so must be a type
            type = bool1;
        else //the method name or variable name exists, so check its type
            type = entry.type;

        if (!type.equals("boolean"))
        {
            System.out.println("\nType Checking Error at And && Expression");
            System.out.println("The left hand side of the && expression evaulates to type '" + type + "'");
            System.out.println("The left hand side of the && expression must evaulate to type 'boolean'");
        }

        entry = argu.get_tableEntry(bool2);

        if (entry == null) //the variable name doesn't exist, so must be a type
            type = bool2;
        else //the method name or variable name exists, so check its type
            type = entry.type;

        if (!type.equals("boolean"))
        {
            System.out.println("\nType Checking Error at And && Expression");
            System.out.println("The right hand side of the && expression evaulates to type '" + type + "'");
            System.out.println("The right hand side of the && expression must evaulate to type 'boolean'");
        }

        //return "";
        return "boolean"; //because a logical and && is either true or false
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(CompareExpression n, SymbolTableEntry argu) 
    {
        String int1 = n.f0.accept(this, argu); //* f0 -> PrimaryExpression()
        String int2 = n.f2.accept(this, argu); //* f2 -> PrimaryExpression()

        SymbolTableEntry entry;
        String type;

        entry = argu.get_tableEntry(int1);

        if (entry == null) //the variable name doesn't exist, so must be a type
            type = int1;
        else //the method name or variable name exists, so check its type
            type = entry.type;

        if (!type.equals("int"))
        {
            System.out.println("\nType Checking Error at Compare Expression");
            System.out.println("The left hand side of the < expression evaulates to type '" + type + "'");
            System.out.println("The left hand side of the < expression must evaulate to type 'int'");
        }

        entry = argu.get_tableEntry(int2);

        if (entry == null) //the variable name doesn't exist, so must be a type
            type = int2;
        else //the method name or variable name exists, so check its type
            type = entry.type;

        if (!type.equals("int"))
        {
            System.out.println("\nType Checking Error at Compare Expression");
            System.out.println("The right hand side of the < expression evaulates to type '" + type + "'");
            System.out.println("The right hand side of the < expression must evaulate to type 'int'");
        }

        //return "";
        return "boolean"; //because a comparision is either true or false
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(PlusExpression n, SymbolTableEntry argu) 
    {
        String int1 = n.f0.accept(this, argu); //* f0 -> PrimaryExpression()
        String int2 = n.f2.accept(this, argu); //* f2 -> PrimaryExpression()

        SymbolTableEntry entry;
        String type;

        entry = argu.get_tableEntry(int1);

        if (entry == null) //the variable name doesn't exist, so must be a type
            type = int1;
        else //the method name or variable name exists, so check its type
            type = entry.type;

        if (!type.equals("int"))
        {
            System.out.println("\nType Checking Error at Plus Expression");
            System.out.println("The left hand side of the + expression evaulates to type '" + type + "'");
            System.out.println("The left hand side of the + expression must evaulate to type 'int'");
        }

        entry = argu.get_tableEntry(int2);

        if (entry == null) //the variable name doesn't exist, so must be a type
            type = int2;
        else //the method name or variable name exists, so check its type
            type = entry.type;

        if (!type.equals("int"))
        {
            System.out.println("\nType Checking Error at Plus Expression");
            System.out.println("The right hand side of the + expression evaulates to type '" + type + "'");
            System.out.println("The right hand side of the + expression must evaulate to type 'int'");
        }

        //return "";
        return "int"; //because we can only add integers
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(MinusExpression n, SymbolTableEntry argu) 
    {
        String int1 = n.f0.accept(this, argu); //* f0 -> PrimaryExpression()
        String int2 = n.f2.accept(this, argu); //* f2 -> PrimaryExpression()

        SymbolTableEntry entry;
        String type;

        entry = argu.get_tableEntry(int1);

        if (entry == null) //the variable name doesn't exist, so must be a type
            type = int1;
        else //the method name or variable name exists, so check its type
            type = entry.type;

        if (!type.equals("int"))
        {
            System.out.println("\nType Checking Error at Minus Expression");
            System.out.println("The left hand side of the - expression evaulates to type '" + type + "'");
            System.out.println("The left hand side of the - expression must evaulate to type 'int'");
        }

        entry = argu.get_tableEntry(int2);

        if (entry == null) //the variable name doesn't exist, so must be a type
            type = int2;
        else //the method name or variable name exists, so check its type
            type = entry.type;

        if (!type.equals("int"))
        {
            System.out.println("\nType Checking Error at Minus Expression");
            System.out.println("The right hand side of the - expression evaulates to type '" + type + "'");
            System.out.println("The right hand side of the - expression must evaulate to type 'int'");
        }

        //return "";
        return "int"; //because we can only subtract integers
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(TimesExpression n, SymbolTableEntry argu) 
    {
        //can a variable or an int type
        String int1 = n.f0.accept(this, argu); //* f0 -> PrimaryExpression()
        String int2 = n.f2.accept(this, argu); //* f2 -> PrimaryExpression()

        SymbolTableEntry entry;
        String type;

        entry = argu.get_tableEntry(int1);

        if (entry == null) //the variable name doesn't exist, so must be a type
            type = int1;
        else //the method name or variable name exists, so check its type
            type = entry.type;

        if (!type.equals("int"))
        {
            System.out.println("\nType Checking Error at Times Expression");
            System.out.println("The left hand side of the * expression evaulates to type '" + type + "'");
            System.out.println("The left hand side of the * expression must evaulate to type 'int'");
        }

        entry = argu.get_tableEntry(int2);

        if (entry == null) //the variable name doesn't exist, so must be a type
            type = int2;
        else //the method name or variable name exists, so check its type
            type = entry.type;

        if (!type.equals("int"))
        {
            System.out.println("\nType Checking Error at Times Expression");
            System.out.println("The right hand side of the * expression evaulates to type '" + type + "'");
            System.out.println("The right hand side of the * expression must evaulate to type 'int'");
        }

        //return "";
        return "int"; //because we can only mutliply integers
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    @Override
    public String visit(ArrayLookup n, SymbolTableEntry argu)
    {
        String arrayName = n.f0.accept(this, argu); //* f0 -> PrimaryExpression()
        String integer = n.f2.accept(this, argu); //* f2 -> PrimaryExpression()
        //integer can be a variable or a type

        SymbolTableEntry variableEntry;
        String arrayType;

        variableEntry = argu.get_tableEntry(arrayName);

        if (variableEntry == null) //the array doesn't exist
        {
            System.out.println("\nType Checking Error at Array Lookup");
            System.out.println("The array '" + arrayName + "' is not defined");
        }
        else //the array exists, so check it type
        {
            arrayType = variableEntry.type;

            if (!arrayType.equals("int[]"))
            {
                System.out.println("\nType Checking Error at Array Lookup");
                System.out.println("The array '" + arrayName + "' is of type '" + arrayType + "'");
                System.out.println("Arrays must be of type 'int[]'");
            }
        }

        variableEntry = argu.get_tableEntry(integer);

        if (variableEntry == null) //the variable name doesn't exist, so must be a type
            arrayType = integer;
        else //the method name or variable name exists, so check its type
            arrayType = variableEntry.type;

        if (!arrayType.equals("int"))
        {
            System.out.println("\nType Checking Error at Array Lookup");
            System.out.println("The [] expression evaulates to type '" + arrayType + "'");
            System.out.println("The [] expression doesn't evaulate to type 'int'");
        }

        //return "";
        return "int"; //because array[index] returns an integer
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
    @Override
    public String visit(ArrayLength n, SymbolTableEntry argu) 
    {
        String arrayName = n.f0.accept(this, argu); //* f0 -> PrimaryExpression()

        SymbolTableEntry variableEntry = argu.get_tableEntry(arrayName);
        String arrayType;

        if (variableEntry == null)
        {
            System.out.println("\nType Checking Error at Array Length");
            System.out.println("The array '" + arrayName + "' is not defined");
        }
        else
        {
            arrayType = variableEntry.type;

            if (!arrayType.equals("int[]"))
            {
                System.out.println("\nType Checking Error at Array Length");
                System.out.println("The array '" + arrayName + "' is of type '" + arrayType + "'");
                System.out.println("Arrays must be of type 'int[]'");
            }
        }

        //return arrayName; 
        return "int"; //since array.length return an integer
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
    @Override
    public String visit(MessageSend n, SymbolTableEntry argu) 
    {
        //either an object name or the word 'this', in which case this returns the class's name
        String objectName = n.f0.accept(this, argu); //* f0 -> PrimaryExpression()
        String methodName = n.f2.accept(this, argu); //* f2 -> Identifier()
        String parameters = n.f4.accept(this, argu); //* f4 -> ( ExpressionList() )?

        SymbolTableEntry varClass_Entry = argu.get_tableEntry(objectName);
        SymbolTableEntry classEntry;
        MethodEntry methodEntry;

        if (varClass_Entry == null) //the object doesn't exist
        {
            System.out.println("\nType Checking Error at Message Send");
            System.out.println("The object '" + objectName + "' doesn't exist");

            return "";
        }
        else //the object exists, so check if the method name exists in the object's class
        {
            classEntry = varClass_Entry.get_tableEntry(varClass_Entry.type);
            methodEntry = (MethodEntry)classEntry.get_tableEntry(methodName); 

            if (methodEntry == null) //the method doesn't belong to the class
            {
                System.out.println("\nType Checking Error at Message Send");
                System.out.println("The method '" + methodName + "' doesn't exist in the class '" + classEntry.name + "'");

                return "";
            }
            else//the method belongs to the class
            {
                if (methodEntry.checkParameters(parameters, argu)) //check parameters types and size
                    return methodEntry.type;
                else
                {
                    System.out.println("\nType Checking Error at Message Send");
                    System.out.println("Parameter Type/Size Mismatch for method '" + methodName + "'");

                    return "";
                }
            }
        }
    }

    /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
    @Override
    public String visit(ExpressionList n, SymbolTableEntry argu) 
    {
        String expressionList1 = n.f0.accept(this, argu); //* f0 -> Expression()
        //String expressionList2 = n.f1.accept(this, argu); //* f1 -> ( ExpressionRest() )*

        StringBuilder expressionList2 = new StringBuilder(expressionList1);

        //System.out.println("Parameter: " + expressionList2.toString());

        for (int i = 0; i < n.f1.size(); i++) 
        {
            expressionList2.append(" ");
            expressionList2.append(n.f1.elementAt(i).accept(this, argu));

            //System.out.println("Parameter: " + expressionList2.toString());
        }

        //System.out.println("All parameters: " + expressionList2.toString());
        return expressionList2.toString();
    }

    /**
    * f0 -> ","
    * f1 -> Expression()
    */
    @Override
    public String visit(ExpressionRest n, SymbolTableEntry argu) 
    {
        return n.f1.accept(this, argu); //* f1 -> Expression()
    }

    /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | NotExpression()
    *       | BracketExpression()
    */
    @Override
    public String visit(PrimaryExpression n, SymbolTableEntry argu) 
    {
        return n.f0.accept(this, argu);
    }

    /**
    * f0 -> <INTEGER_LITERAL>
    */
    @Override
    public String visit(IntegerLiteral n, SymbolTableEntry argu) 
    {
        return "int";
    }

    /**
    * f0 -> "true"
    */
    @Override
    public String visit(TrueLiteral n, SymbolTableEntry argu) 
    {
        return "boolean";
    }

    /**
    * f0 -> "false"
    */
    @Override
    public String visit(FalseLiteral n, SymbolTableEntry argu) 
    {
        return "boolean";
    }

    /**
    * f0 -> <IDENTIFIER>
    */
    @Override
    public String visit(Identifier n, SymbolTableEntry argu) 
    {
        return n.f0.toString();
    }

    /**
    * f0 -> "this"
    */
    @Override
    public String visit(ThisExpression n, SymbolTableEntry argu) 
    {
        //return "this";
        return currentClass.name;
    }

    /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    @Override
    public String visit(ArrayAllocationExpression n, SymbolTableEntry argu) 
    {
        //integer can be a method name or variable name or type
        String integer = n.f3.accept(this, argu); //* f3 -> Expression()

        SymbolTableEntry entry = argu.get_tableEntry(integer);
        String type;

        if (entry == null) //the method name or variable name doesn't exist, so must be a type
            type = integer;
        else //the method name or variable name exists, so check its type
            type = entry.type;

        if (!type.equals("int"))
        {
            System.out.println("\nType Checking Error at Array Allocation Expression");
            System.out.println("The expression is of type '" + type + "'");
            System.out.println("The expression must be of type 'int'");
        }

        return "int[]";
    }

    /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
    @Override
    public String visit(AllocationExpression n, SymbolTableEntry argu) 
    {
        String className = n.f1.accept(this, argu); //* f1 -> Identifier()

        if (argu.get_tableEntry(className) == null)
        {
            System.out.println("\nType Checking Error at Allocation Expression");
            System.out.println("The class " + className + " doesn't exist");
        }

        return className; //the class name is the type
    }

    /**
    * f0 -> "!"
    * f1 -> Expression()
    */
    @Override
    public String visit(NotExpression n, SymbolTableEntry argu) 
    {
        //can be a method name or variable name or type
        String bool = n.f1.accept(this, argu);//* f1 -> Expression()

        SymbolTableEntry entry = argu.get_tableEntry(bool);
        String type;

        if (entry == null) //the method name or variable name doesn't exist, so must be a type
            type = bool;
        else //the method name or variable name exists, so check its type
            type = entry.type;

        if (!type.equals("boolean"))
        {
            System.out.println("\nType Checking Error at Not Expression");
            System.out.println("The expression is of type '" + type + "'");
            System.out.println("The expression must be of type 'boolean'");
        }

        return bool;
    }

    /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    @Override
    public String visit(BracketExpression n, SymbolTableEntry argu) 
    {
        return n.f1.accept(this, argu);//* f1 -> Expression()
    }

    @Override
    public String visit(NodeList n, SymbolTableEntry argu) 
    {
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) 
            e.nextElement().accept(this,argu);
        
        return "";
    }

    @Override
    public String visit(NodeListOptional n, SymbolTableEntry argu) 
    {
        if ( n.present() ) 
        {
            for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) 
                e.nextElement().accept(this,argu);
        }
        
        return "";
    }

    @Override
    public String visit(NodeOptional n, SymbolTableEntry argu) 
    {
        if ( n.present() )
            return n.node.accept(this, argu);
        else
            return "";
    }

    @Override
    public String visit(NodeSequence n, SymbolTableEntry argu) 
    {
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) 
            e.nextElement().accept(this,argu);
            
        return "";
    }

    @Override
    public String visit(NodeToken n, SymbolTableEntry argu) 
    {
        return "";
    }
}