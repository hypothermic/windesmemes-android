package nl.hypothermic.windesmemes.android.util;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageViewUtil {

    public static void shrink(ImageView imageView) {
        shrink(imageView, 0);
    }

    public static void shrink(ImageView imageView, int heightPadding) {
        final Drawable drawable = imageView.getDrawable();
        final ViewGroup.LayoutParams layout = imageView.getLayoutParams();
        final int height = drawable.getIntrinsicHeight();

        if (height > 0 && height <= layout.height) {
            layout.height = height + heightPadding;
            imageView.setLayoutParams(layout);
        }
    }

    private ImageViewUtil() {
        throw new AssertionError("Not instantiable");
    }
}
