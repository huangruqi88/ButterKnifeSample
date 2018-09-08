package butterknife;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * File description.
 *
 * @author 黄汝琪
 * @date 2018/9/6
 */
public final class ButterKnife {
    @VisibleForTesting
    static final Map<Class<?>, Constructor<? extends Unbinder>> BINDINGS = new LinkedHashMap<>();

    /**
     * 绑定activity的根布局
     * @param target
     * @return
     */
    @NonNull @UiThread
    public static Unbinder bind(@NonNull Activity target) {
        View sourceView = target.getWindow().getDecorView();
        return createBinding(target, sourceView);
    }

    private static Unbinder createBinding(@NonNull Object target, @NonNull View source) {
        Class<?> targetClass = target.getClass();
        Constructor<? extends Unbinder> constructor = findBindingConstructorForClass(targetClass);

        if (constructor == null) {
            return Unbinder.EMPTY;
        }
        try {
            //反射返回构造的实例（如果是Activity指的就是当前绑定的Activity，如果是Fragment就指当前绑定的Fragment）
            return constructor.newInstance(target, source);
        } catch (Exception e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        }
    }

    @Nullable
    @CheckResult
    @UiThread
    /**
     * 根据Class找到绑定的构造函数
     */
    private static Constructor<? extends Unbinder> findBindingConstructorForClass(Class<?> cls) {
        Constructor<? extends Unbinder> bindingCtor = BINDINGS.get(cls);
        if (bindingCtor != null) {
            return bindingCtor;
        }
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            return null;
        }
        try {
            Class<?> bindingClass = cls.getClassLoader().loadClass(clsName + "_ViewBinding");
            bindingCtor = (Constructor<? extends Unbinder>) bindingClass.getConstructor(cls, View.class);
        } catch (ClassNotFoundException e) {
            bindingCtor = findBindingConstructorForClass(cls.getSuperclass());
        } catch (NoSuchMethodException e) {
        }
        BINDINGS.put(cls, bindingCtor);
        return bindingCtor;
    }
}
