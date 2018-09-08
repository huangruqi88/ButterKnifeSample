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
        targetType = "android.widget.CompoundButton",
        setter = "setOnCheckedChangeListener",
        type = "android.widget.CompoundButton.OnCheckedChangeListener",
        method = @ListenerMethod(
                name = "onCheckedChanged",
                parameters = {
                        "android.widget.CompoundButton",
                        "boolean"
                }
        )
)
public @interface OnCheckedChanged {
    @IdRes
    int[] value() default {Constants.NO_RES_ID};
}
