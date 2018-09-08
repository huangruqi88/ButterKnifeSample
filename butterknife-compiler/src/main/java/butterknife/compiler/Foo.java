package butterknife.compiler;  // PackageElement

/**
 * author:黄汝琪 on 2018/9/8.
 * email:huangruqi88@163.com
 */
public class Foo {

    private int a;      // VariableElement
    private Foo other;  // VariableElement

    public Foo() {
    }    // ExecuteableElement

    public void setA(  // ExecuteableElement
                       int newA   // TypeElement
    ) {
    }
}
