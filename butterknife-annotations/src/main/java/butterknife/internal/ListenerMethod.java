package butterknife.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author:黄汝琪 on 2018/9/7.
 * email:huangruqi88@163.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ListenerMethod {
    //监听的名字
    String name();
    //方法参数列表。如果类型不是原语，则必须完全限定。
    String[] parameters() default { };
    //监听方法的原句的返回类型，默认为void
    String returnType() default "void";
    //如果{@link #returnType()}不是{@code void}，则在不存在绑定时返回该值。
    String defaultReturn() default "null";
}
