package butterknife.internal;

import android.view.View;

/**
 * 根布局中所有事件的点击事件
 *
 * @author 黄汝琪
 * @date 2018/9/6
 */
public abstract class DebouncingOnClickListener implements View.OnClickListener {
    static boolean enabled = true;

    private static final Runnable ENABLE_AGAIN = new Runnable() {
        @Override public void run() {
            enabled = true;
        }
    };

    @Override
    public void onClick(View v) {
        if (enabled) {
            enabled = false;
            v.post(ENABLE_AGAIN);
            doClick(v);
        }
    }

    public abstract void doClick(View v);
}
