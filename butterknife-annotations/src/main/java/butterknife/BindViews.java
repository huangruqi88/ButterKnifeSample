package butterknife;

import android.support.annotation.IdRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author:黄汝琪 on 2018/9/7.
 * email:huangruqi88@163.com
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface BindViews {
    @IdRes
    int[] value() default 0;
}
