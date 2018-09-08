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
@Target(ElementType.ANNOTATION_TYPE)
public @interface ListenerClass {
    String targetType();


    String setter();

    String remover() default "";
    //监听的完全限定类型
    String type();
    //声明枚举的监听回调
    Class<? extends Enum<?>> callbacks() default NONE.class;
    //单个方法的监听回调
    ListenerMethod[] method() default { };
    enum NONE { }
}
