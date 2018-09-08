package butterknife;

import android.support.annotation.IdRes;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


import butterknife.internal.Constants;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;

/**
 * author:黄汝琪 on 2018/9/7.
 * email:huangruqi88@163.com
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@ListenerClass(
        targetType = "android.view.View",
        setter = "setOnClickListener",
        type = "butterknife.internal.DebouncingOnClickListener",
        method = @ListenerMethod(
                name = "doClick",
                parameters = "android.view.View"
        )
)
public @interface OnClick {
    @IdRes
    int[] value() default {Constants.NO_RES_ID};
}
