package butterknife;

import android.support.annotation.UiThread;

/**
 * View解除绑定的回调
 *
 * @author 黄汝琪
 * @date 2018/9/6
 */
public interface Unbinder {
    @UiThread
    void unbind();
    Unbinder EMPTY = new Unbinder() {
        @Override
        public void unbind() {

        }
    };
}
