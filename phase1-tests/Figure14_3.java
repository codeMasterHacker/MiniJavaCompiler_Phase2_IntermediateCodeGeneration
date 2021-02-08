class Figure14_3
{
    public static void main(String[] a)
    {
	    System.out.println(new A().f());
    }
}

class A 
{
    int x ;

    public int f() { return x ; }
}

class B extends A
{
    public int g() { return x ; }
}

class C extends B
{
    public int g() { return x ; }
}

class D extends C
{
    int y ;

    public int f() { return y ; }
}
