package visitor;
import SymbolTable.*;
import VTable.*;
import syntaxtree.*;
import java.util.*;

public class SymbolTableVisitor implements GJVisitor<String, SymbolTableEntry> 
{
    /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
    @Override
    //public R visit(Goal n, A argu);
    public String visit(Goal n, SymbolTableEntry goal) 
    {
        n.f0.accept(this, goal); //* f0 -> MainClass()
        n.f1.accept(this, goal); //* f1 -> ( TypeDeclaration() )*

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

        ClassEntry classEntry = new ClassEntry(className, className, goal, true, "");

        // n.f11.accept(this, classEntry); //* f11 -> Identifier()
        MethodEntry methodEntry = new MethodEntry("main", "void", classEntry, true, className);

        n.f14.accept(this, methodEntry); //* f14 -> ( VarDeclaration() )*
        n.f15.accept(this, methodEntry); //* f15 -> ( Statement() )*

        classEntry.insert_methodEntry("main", methodEntry);

        goal.insert_classEntry(className, classEntry);

        return "";
    }

    /**
    * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
    @Override
    public String visit(TypeDeclaration n, SymbolTableEntry goal) 
    {
        n.f0.accept(this, goal);

        return "";
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

        ClassEntry classEntry = new ClassEntry(className, className, goal, false, "");

        n.f3.accept(this, classEntry); //* f3 -> ( VarDeclaration() )*
        n.f4.accept(this, classEntry); //* f4 -> ( MethodDeclaration() )*

        goal.insert_classEntry(className, classEntry);

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
        String parent_className = n.f3.accept(this, null); //* f3 -> Identifier(), will return parent class name

        ClassEntry classEntry = new ClassEntry(className, className, goal, false, parent_className);

        ClassEntry parent_classEntry = (ClassEntry)goal.get_tableEntry(parent_className);

        classEntry.insert_FieldsMethods(parent_classEntry);

        n.f5.accept(this, classEntry); //* f5 -> ( VarDeclaration() )*
        n.f6.accept(this, classEntry); //* f6 -> ( MethodDeclaration() )*

        goal.insert_classEntry(className, classEntry);

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
        String type = n.f0.accept(this, null); //* f0 -> Type()
        String variableName = n.f1.accept(this, null); //* f1 -> Identifier()

        VariableEntry variableEntry = (classMethod instanceof ClassEntry) ? 
        new VariableEntry(variableName, type, classMethod, false, classMethod.name) :
        new VariableEntry(variableName, type, classMethod, false, "");

        classMethod.insert_variableEntry(variableName, variableEntry);

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
        String returnType = n.f1.accept(this, null); //* f1 -> Type()
        String methodName = n.f2.accept(this, null); //* f2 -> Identifier()

        MethodEntry methodEntry = new MethodEntry(methodName, returnType, classEntry, false, classEntry.name);

        n.f4.accept(this, methodEntry); //* f4 -> ( FormalParameterList() )?
        n.f7.accept(this, methodEntry); //* f7 -> ( VarDeclaration() )*
        n.f8.accept(this, methodEntry); //* f8 -> ( Statement() )*
        n.f10.accept(this, methodEntry); //* f10 -> Expression()

        classEntry.insert_methodEntry(methodName, methodEntry);

        return "";
    }

    /**
    * f0 -> FormalParameter()
    * f1 -> ( FormalParameterRest() )*
    */
    @Override
    public String visit(FormalParameterList n, SymbolTableEntry methodEntry) 
    {
        n.f0.accept(this, methodEntry); //* f0 -> FormalParameter()
        n.f1.accept(this, methodEntry); //* f1 -> ( FormalParameterRest() )*

        return "";
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
    @Override
    public String visit(FormalParameter n, SymbolTableEntry methodEntry) 
    {
        String type = n.f0.accept(this, null); //* f0 -> Type()
        String variableName = n.f1.accept(this, null); //* f1 -> Identifier()

        VariableEntry variableEntry = new VariableEntry(variableName, type, methodEntry, true, "");

        methodEntry.insert_variableEntry(variableName, variableEntry);

        return "";
    }

    /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
    @Override
    public String visit(FormalParameterRest n, SymbolTableEntry methodEntry) 
    {
        n.f1.accept(this, methodEntry); //* f1 -> FormalParameter()

        return "";
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
        return n.f1.accept(this, methodEntry); //f1 -> ( Statement() )*
    }

    /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    @Override
    public String visit(AssignmentStatement n, SymbolTableEntry methodEntry) 
    {
        n.f0.accept(this, methodEntry); //* f0 -> Identifier()
        n.f2.accept(this, methodEntry); //* f2 -> Expression()

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
    public String visit(ArrayAssignmentStatement n, SymbolTableEntry methodEntry) 
    {
        n.f0.accept(this, methodEntry); //* f0 -> Identifier()
        n.f2.accept(this, methodEntry); //* f2 -> Expression()
        n.f5.accept(this, methodEntry); //* f5 -> Expression()

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
    public String visit(IfStatement n, SymbolTableEntry methodEntry) 
    {
        n.f2.accept(this, methodEntry); //* f2 -> Expression()
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
    public String visit(WhileStatement n, SymbolTableEntry methodEntry) 
    {
        n.f2.accept(this, methodEntry); //* f2 -> Expression()
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
        return n.f2.accept(this, methodEntry); //* f2 -> Expression()
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
        n.f0.accept(this, argu); //* f0 -> PrimaryExpression()
        n.f2.accept(this, argu); //* f2 -> PrimaryExpression()

        return "";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(CompareExpression n, SymbolTableEntry argu) 
    {
        n.f0.accept(this, argu); //* f0 -> PrimaryExpression()
        n.f2.accept(this, argu); //* f2 -> PrimaryExpression()

        return "";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(PlusExpression n, SymbolTableEntry argu) 
    {
        n.f0.accept(this, argu); //* f0 -> PrimaryExpression()
        n.f2.accept(this, argu); //* f2 -> PrimaryExpression()

        return "";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(MinusExpression n, SymbolTableEntry argu) 
    {
        n.f0.accept(this, argu); //* f0 -> PrimaryExpression()
        n.f2.accept(this, argu); //* f2 -> PrimaryExpression()

        return "";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(TimesExpression n, SymbolTableEntry argu) 
    {
        n.f0.accept(this, argu); //* f0 -> PrimaryExpression()
        n.f2.accept(this, argu); //* f2 -> PrimaryExpression()

        return "";
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
        n.f0.accept(this, argu); //* f0 -> PrimaryExpression()
        n.f2.accept(this, argu); //* f2 -> PrimaryExpression()

        return "";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
    @Override
    public String visit(ArrayLength n, SymbolTableEntry argu) 
    {
        return n.f0.accept(this, argu); //* f0 -> PrimaryExpression()
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
        n.f0.accept(this, argu); //* f0 -> PrimaryExpression()
        n.f2.accept(this, argu); //* f2 -> Identifier()
        n.f4.accept(this, argu); //* f4 -> ( ExpressionList() )?

        return "";
    }

    /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
    @Override
    public String visit(ExpressionList n, SymbolTableEntry argu) 
    {
        n.f0.accept(this, argu); //* f0 -> Expression()
        n.f1.accept(this, argu); //* f1 -> ( ExpressionRest() )*

        return "";
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
        return n.f0.toString();
    }

    /**
    * f0 -> "true"
    */
    @Override
    public String visit(TrueLiteral n, SymbolTableEntry argu) 
    {
        return "true";
    }

    /**
    * f0 -> "false"
    */
    @Override
    public String visit(FalseLiteral n, SymbolTableEntry argu) 
    {
        return "false";
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
        return "this";
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
        return n.f3.accept(this, argu); //* f3 -> Expression()
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
        return n.f1.accept(this, argu); //* f1 -> Identifier()
    }

    /**
    * f0 -> "!"
    * f1 -> Expression()
    */
    @Override
    public String visit(NotExpression n, SymbolTableEntry argu) 
    {
        return n.f1.accept(this, argu);//* f1 -> Expression()
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