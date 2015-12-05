package yan.girl.utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * 处理图片的工具类.
 */
public class ImageUtils {
	
	//静态变量用于图片合成时使用，作用于第二张图片
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int TOP = 2;
    public static final int BOTTOM = 3;


    /**
     * 1.Drawable 转 Bitmap
     * 
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        return bitmapDrawable.getBitmap();
    }

    /**
     * 2.Bitmap 转 Drawable
     * 
     * @param bitmap
     * @return
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    /**
     * 3.byte[] 转 bitmap
     * 
     * @param b
     * @return
     */
    public static Bitmap bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }
    
    /**
     * 4.byte[] → Drawable
     * 
     * @param b
     * @return
     */
	public static Drawable bytes2Drawable(byte[] b){
        ByteArrayInputStream inputStream = new ByteArrayInputStream(b);
        Drawable draw= Drawable.createFromStream(inputStream, null);
        return draw;
    }
    /**
     * 5.bitmap 转 byte[]
     * 
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
  
    
    /**
     * 6.图片去色,返回灰度图片
     * 
     * @param bmpOriginal
     *            传入的图片
     * @return 去色后的图片
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    
    /**
     * 7.去色同时加圆角
     * 
     * @param bmpOriginal
     *            原图
     * @param pixels
     *            圆角弧度
     * @return 修改后的图片
     */
    public static Bitmap toRoundGrayscale(Bitmap bmpOriginal, int pixels) {
        return toRoundCorner(toGrayscale(bmpOriginal), pixels);
    }

   
    /**
     * 8.把图片变成圆角
     * 
     * @param bmpOriginal
     *            需要修改的图片
     * @param pixels
     *            圆角的弧度
     * @return 圆角图片
     */
    public static Bitmap toRoundCorner(Bitmap bmpOriginal, int pixels) {
        Bitmap output = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bmpOriginal.getWidth(), bmpOriginal.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bmpOriginal, rect, rect, paint);
        return output;
    }

    
    /**
     * 9.使圆角功能支持BitampDrawable
     * 
     * @param bitmapDrawable
     * @param pixels
     * @return
     */
    public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable,
            int pixels) {
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
        return bitmapDrawable;
    }

    /**
     * 10.读取路径中的图片，然后将其转化为缩放后的bitmap
     * 
     * @param imgpath
     */
    public static Bitmap readImage(String imgpath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        Bitmap bitmap = BitmapFactory.decodeFile(imgpath, options); // 此时返回bm为空
        options.inJustDecodeBounds = false;
        // 计算缩放比
        int be = (int) (options.outHeight / (float) 200);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be; // 图片长宽各缩小be分之一
        // 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
        bitmap = BitmapFactory.decodeFile(imgpath, options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        System.out.println(w + " " + h);
        return bitmap;
    }

    /**
     * 11.保存图片为PNG
     * 
     * @param bmpOriginal
     * @param name
     */
    public static void savePNG(Bitmap bmpOriginal, String name) {
        File file = new File(name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bmpOriginal.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 12.保存图片为JPEG
     * 
     * @param bmpOriginal
     * @param name
     */
    public static void saveJPGE(Bitmap bmpOriginal, String name) {
        File file = new File(name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bmpOriginal.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 13.打水印
     * 
     * @param bitmap
     * @return
     */
    public static Bitmap createBmpWithWatermark(Bitmap bmpOriginal, Bitmap watermark) {
        if (bmpOriginal == null) {
            return null;
        }
        int w = bmpOriginal.getWidth();
        int h = bmpOriginal.getHeight();
        int ww = watermark.getWidth();
        int wh = watermark.getHeight();
        // create the new blank bitmap
        Bitmap newbmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newbmp);
        // draw src into
        cv.drawBitmap(bmpOriginal, 0, 0, null);// 在 0，0坐标开始画入src
        // draw watermark into
        cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);// 在src的右下角画入水印
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储
        return newbmp;
    }

    /**
     * 14.多张图片合成
     * 
     * @return
     */
    public static Bitmap mixBitmaps(int direction, Bitmap... bitmaps) {
        if (bitmaps.length <= 0) {
            return null;
        }
        if (bitmaps.length == 1) {
            return bitmaps[0];
        }
        Bitmap newBitmap = bitmaps[0];
        for (int i = 1; i < bitmaps.length; i++) {
            newBitmap = mix2Bitmap(newBitmap, bitmaps[i], direction);
        }
        return newBitmap;
    }
    /**
     * 15.两张图片合成
     * 
     * @return
     */
    private static Bitmap mix2Bitmap(Bitmap first, Bitmap second,
            int direction) {
        if (first == null) {
            return null;
        }
        if (second == null) {
            return first;
        }
        int fw = first.getWidth();
        int fh = first.getHeight();
        int sw = second.getWidth();
        int sh = second.getHeight();
        Bitmap newBitmap = null;
        if (direction == LEFT) {//将第二张图置于第一张图片左边
            newBitmap = Bitmap.createBitmap(fw + sw, fh > sh ? fh : sh,
                    Config.ARGB_8888);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(first, sw, 0, null);
            canvas.drawBitmap(second, 0, 0, null);
        } else if (direction == RIGHT) {//将第二张图置于第一张图片右边
            newBitmap = Bitmap.createBitmap(fw + sw, fh > sh ? fh : sh,
                    Config.ARGB_8888);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(first, 0, 0, null);
            canvas.drawBitmap(second, fw, 0, null);
        } else if (direction == TOP) {//将第二张图置于第一张图片上边
            newBitmap = Bitmap.createBitmap(sw > fw ? sw : fw, fh + sh,
                    Config.ARGB_8888);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(first, 0, sh, null);
            canvas.drawBitmap(second, 0, 0, null);
        } else if (direction == BOTTOM) {//将第二张图置于第一张图片下边
            newBitmap = Bitmap.createBitmap(sw > fw ? sw : fw, fh + sh,
                    Config.ARGB_8888);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(first, 0, 0, null);
            canvas.drawBitmap(second, 0, fh, null);
        }
        return newBitmap;
    }

  
    /** 
     * 16.图片旋转 
     *  
     * @param bmpOriginal 
     *            要旋转的图片 
     * @param degree 
     *            图片旋转的角度，负值为逆时针旋转，正值为顺时针旋转 
     * @return 
     */  
    public static Bitmap rotateBitmap(Bitmap bmpOriginal, float degree) {  
        Matrix matrix = new Matrix();  
        matrix.postRotate(degree);  
        return Bitmap.createBitmap(bmpOriginal, 0, 0, bmpOriginal.getWidth(), bmpOriginal.getHeight(), matrix, true);  
    }  
  
    /** 
     * 17.图片缩放 
     *  
     * @param bmpOriginal 
     * @param scale 
     *            值小于则为缩小，否则为放大 
     * @return 
     */  
    public static Bitmap resizeBitmap(Bitmap bmpOriginal, float scale) {  
        Matrix matrix = new Matrix();  
        matrix.postScale(scale, scale);  
        return Bitmap.createBitmap(bmpOriginal, 0, 0, bmpOriginal.getWidth(), bmpOriginal.getHeight(), matrix, true);  
    }  
  
    /** 
     * 18.图片缩放 
     *  
     * @param bmpOriginal 
     * @param w 
     *            缩小或放大成的宽 
     * @param h 
     *            缩小或放大成的高 
     * @return 
     */  
    public static Bitmap resizeBitmap(Bitmap bmpOriginal, int w, int h) {  

        int width = bmpOriginal.getWidth();  
        int height = bmpOriginal.getHeight();  
  
        float scaleWidth = ((float) w) / width;  
        float scaleHeight = ((float) h) / height;  
  
        Matrix matrix = new Matrix();  
        matrix.postScale(scaleWidth, scaleHeight);  
        return Bitmap.createBitmap(bmpOriginal, 0, 0, width, height, matrix, true);  
    }  
  
    /** 
     * 19.图片反转 
     *  
     * @param bm 
     * @param flag 
     *            0为水平反转，1为垂直反转 
     * @return 
     */  
    public static Bitmap reverseBitmap(Bitmap bmp, int flag) {  
        float[] floats = null;  
        switch (flag) {  
        case 0: // 水平反转  
            floats = new float[] { -1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };  
            break;  
        case 1: // 垂直反转  
            floats = new float[] { 1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f };  
            break;  
        }  
  
        if (floats != null) {  
            Matrix matrix = new Matrix();  
            matrix.setValues(floats);  
            return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);  
        }  
  
        return null;  
    }  

    /**
     * 20.传入一张图片，经倒转后，再取一半再与原图合成倒影图
     * @param bitmap
     * @return
     */
    public static Bitmap createReflectBitmap(Bitmap originBitmap){
    	
    	int reflectionGap = 2;
		//取原图尺寸
		int width = originBitmap.getWidth();
		int height = originBitmap.getHeight();

		Matrix matrix = new Matrix();
		//上下翻转设置
		matrix.preScale(1, -1);

		//取原图下半部分并进行翻转
		Bitmap reflectionImage = Bitmap.createBitmap(originBitmap, 0,
				height / 2, width, height / 2, matrix, false);

		//创建带阴影的图原型
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		//将图原型载入画布，使用画布对图原型进行操作
		Canvas canvas = new Canvas(bitmapWithReflection);

		//在画布上画入原图
		canvas.drawBitmap(originBitmap, 0, 0, null);

		//生成默认画刷工具，在画布上画入一个正方形区域
		Paint deafaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap,
				deafaultPaint);
		
		//在画布上画入先前截图的倒影图
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		//产生阴影画刷工具
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, originBitmap
				.getHeight(), 0, bitmapWithReflection.getHeight()
				+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		//在画布的原图下方整个区域画入这个阴影效果
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);   	
		
		return bitmapWithReflection;
    }
}