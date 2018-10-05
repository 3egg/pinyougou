package interview;

import java.util.LinkedList;
import java.util.List;

public class StaticNestedClass {

    public static void main(String[] args) {
        /*A.b b = new A.b();
        System.out.println(b);*/
        C c = new C();
        List list = new LinkedList();//集合
        String a = "a";
        a.length();
        Integer[] bb = {1,2,3};//数组
        int length = bb.length;

    }

}
class A{
    static class b{
        public b() {
            System.out.println("A中静态的内部类");
        }
    }
}

class C{
    public C() {
        new D();
    }

    class D{
        public D() {
            System.out.println("C的内部类");
        }
    }
}
class Singleton {
    private Singleton(){}
    private static Singleton instance = new Singleton();
    public static Singleton getInstance() {
        return instance;
    }
}