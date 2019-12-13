package a.miracle.lib_zxing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by A Miracle on 2016/3/14.
 */
public class BitmapUtils {

    private static final Canvas sCanvas = new Canvas();

    /** 缩放图片 */
    public static Bitmap zoomBitmapSize(Bitmap bitmap, float width, float height) {
        if (width <= 0 || height <= 0) {
            return bitmap;
        }
        if(bitmap == null){
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(width / bitmap.getWidth(), height / bitmap.getHeight());
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /** 缩放图片 */
    public static Bitmap zoomBitmapScale(Bitmap bitmap, float sx, float sy) {
        if (sx <= 0 || sy <= 0) {
            return bitmap;
        }
        if(bitmap == null){
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sx);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /** 读取图片(原图) */
    public static Bitmap readFile2Bitmap(File file) {
        if (file.exists()) {// 若该文件存在
            FileDescriptor fd;
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                fd = fis.getFD();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inDither = false;
                options.inPurgeable = true;
                options.inInputShareable = true;
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(fis);
            }
        }
        return null;
    }

    /** 读取图片(图片已经过合理缩放) */
    public static Bitmap readFile2BitmapZoom(Context context, File file) {
        if (file.exists()) {// 若该文件存在
            FileDescriptor fd;
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                fd = fis.getFD();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(fd, null, options);
                int imgHeight = options.outHeight;
                int imgWidth = options.outWidth;

                DisplayMetrics metric = context.getResources().getDisplayMetrics();
                int windowWidth = metric.widthPixels;
                int windowHeitht = metric.heightPixels;
                if(imgWidth > imgHeight){
                    int tmp = imgWidth;
                    imgWidth = imgHeight;
                    imgHeight = tmp;
                }
                int scaleX = imgWidth/windowWidth;
                int scaleY = imgHeight/windowHeitht;
                int scale = 1;
                if (scaleX >= scaleY && scaleX > 1) {
                    scale = scaleX - (scaleX - scaleY) / 4;
                } else if (scaleY > scaleX && scaleY > 1) {
                    scale = scaleY - (scaleY - scaleX) / 4;
                }

                options.inSampleSize = scale;

                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPurgeable = true;
                options.inInputShareable = true;
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);

                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(fis);
            }
        }
        return null;
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree 旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     * @param angle 角度
     * @param bitmap
     * @return
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 从视图创建位图
     * @param view
     * @return
     */
    public static Bitmap createBitmapFromView(View view) {
        if (view instanceof ImageView) {
            Drawable drawable = ((ImageView) view).getDrawable();
            if (drawable != null && drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
        }
        view.clearFocus();
        Bitmap bitmap = createBitmapSafely(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888, 1);
        if (bitmap != null) {
            synchronized (sCanvas) {
                Canvas canvas = sCanvas;
                canvas.setBitmap(bitmap);
                view.draw(canvas);
                canvas.setBitmap(null);
            }
        }
        return bitmap;
    }

    /**
     * 一个安全的Bitmap.createBitmap(width, height, config)
     * @param width
     * @param height
     * @param config
     * @param retryCount 重试次数
     * @return
     */
    public static Bitmap createBitmapSafely(int width, int height, Bitmap.Config config, int retryCount) {
        try {
            return Bitmap.createBitmap(width, height, config);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (retryCount > 0) {
                System.gc();
                return createBitmapSafely(width, height, config, retryCount - 1);
            }
            return null;
        }
    }
}
