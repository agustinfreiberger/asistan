package ar.edu.unicen.isistan.asistan.views.utils;

import android.graphics.Bitmap;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

public class BitmapUtils {

    public static Bitmap replaceColor(@NotNull Bitmap src, int fromColor, int targetColor) {

        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];

        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int x = 0; x < pixels.length; x++)
            if (pixels[x] == fromColor)
                pixels[x] = targetColor;

        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        result.setPixels(pixels, 0, width, 0, 0, width, height);

        return result;
    }

}
