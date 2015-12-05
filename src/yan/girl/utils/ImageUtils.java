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
 * ����ͼƬ�Ĺ�����.
 */
public class ImageUtils {
	
	//��̬��������ͼƬ�ϳ�ʱʹ�ã������ڵڶ���ͼƬ
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int TOP = 2;
    public static final int BOTTOM = 3;


    /**
     * 1.Drawable ת Bitmap
     * 
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        return bitmapDrawable.getBitmap();
    }

    /**
     * 2.Bitmap ת Drawable
     * 
     * @param bitmap
     * @return
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    /**
     * 3.byte[] ת bitmap
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
     * 4.byte[] �� Drawable
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
     * 5.bitmap ת byte[]
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
     * 6.ͼƬȥɫ,���ػҶ�ͼƬ
     * 
     * @param bmpOriginal
     *            �����ͼƬ
     * @return ȥɫ���ͼƬ
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
     * 7.ȥɫͬʱ��Բ��
     * 
     * @param bmpOriginal
     *            ԭͼ
     * @param pixels
     *            Բ�ǻ���
     * @return �޸ĺ��ͼƬ
     */
    public static Bitmap toRoundGrayscale(Bitmap bmpOriginal, int pixels) {
        return toRoundCorner(toGrayscale(bmpOriginal), pixels);
    }

   
    /**
     * 8.��ͼƬ���Բ��
     * 
     * @param bmpOriginal
     *            ��Ҫ�޸ĵ�ͼƬ
     * @param pixels
     *            Բ�ǵĻ���
     * @return Բ��ͼƬ
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
     * 9.ʹԲ�ǹ���֧��BitampDrawable
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
     * 10.��ȡ·���е�ͼƬ��Ȼ����ת��Ϊ���ź��bitmap
     * 
     * @param imgpath
     */
    public static Bitmap readImage(String imgpath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // ��ȡ���ͼƬ�Ŀ�͸�
        Bitmap bitmap = BitmapFactory.decodeFile(imgpath, options); // ��ʱ����bmΪ��
        options.inJustDecodeBounds = false;
        // �������ű�
        int be = (int) (options.outHeight / (float) 200);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be; // ͼƬ�������Сbe��֮һ
        // ���¶���ͼƬ��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ falseŶ
        bitmap = BitmapFactory.decodeFile(imgpath, options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        System.out.println(w + " " + h);
        return bitmap;
    }

    /**
     * 11.����ͼƬΪPNG
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
     * 12.����ͼƬΪJPEG
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
     * 13.��ˮӡ
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
        Bitmap newbmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);// ����һ���µĺ�SRC���ȿ��һ����λͼ
        Canvas cv = new Canvas(newbmp);
        // draw src into
        cv.drawBitmap(bmpOriginal, 0, 0, null);// �� 0��0���꿪ʼ����src
        // draw watermark into
        cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);// ��src�����½ǻ���ˮӡ
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// ����
        // store
        cv.restore();// �洢
        return newbmp;
    }

    /**
     * 14.����ͼƬ�ϳ�
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
     * 15.����ͼƬ�ϳ�
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
        if (direction == LEFT) {//���ڶ���ͼ���ڵ�һ��ͼƬ���
            newBitmap = Bitmap.createBitmap(fw + sw, fh > sh ? fh : sh,
                    Config.ARGB_8888);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(first, sw, 0, null);
            canvas.drawBitmap(second, 0, 0, null);
        } else if (direction == RIGHT) {//���ڶ���ͼ���ڵ�һ��ͼƬ�ұ�
            newBitmap = Bitmap.createBitmap(fw + sw, fh > sh ? fh : sh,
                    Config.ARGB_8888);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(first, 0, 0, null);
            canvas.drawBitmap(second, fw, 0, null);
        } else if (direction == TOP) {//���ڶ���ͼ���ڵ�һ��ͼƬ�ϱ�
            newBitmap = Bitmap.createBitmap(sw > fw ? sw : fw, fh + sh,
                    Config.ARGB_8888);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(first, 0, sh, null);
            canvas.drawBitmap(second, 0, 0, null);
        } else if (direction == BOTTOM) {//���ڶ���ͼ���ڵ�һ��ͼƬ�±�
            newBitmap = Bitmap.createBitmap(sw > fw ? sw : fw, fh + sh,
                    Config.ARGB_8888);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(first, 0, 0, null);
            canvas.drawBitmap(second, 0, fh, null);
        }
        return newBitmap;
    }

  
    /** 
     * 16.ͼƬ��ת 
     *  
     * @param bmpOriginal 
     *            Ҫ��ת��ͼƬ 
     * @param degree 
     *            ͼƬ��ת�ĽǶȣ���ֵΪ��ʱ����ת����ֵΪ˳ʱ����ת 
     * @return 
     */  
    public static Bitmap rotateBitmap(Bitmap bmpOriginal, float degree) {  
        Matrix matrix = new Matrix();  
        matrix.postRotate(degree);  
        return Bitmap.createBitmap(bmpOriginal, 0, 0, bmpOriginal.getWidth(), bmpOriginal.getHeight(), matrix, true);  
    }  
  
    /** 
     * 17.ͼƬ���� 
     *  
     * @param bmpOriginal 
     * @param scale 
     *            ֵС����Ϊ��С������Ϊ�Ŵ� 
     * @return 
     */  
    public static Bitmap resizeBitmap(Bitmap bmpOriginal, float scale) {  
        Matrix matrix = new Matrix();  
        matrix.postScale(scale, scale);  
        return Bitmap.createBitmap(bmpOriginal, 0, 0, bmpOriginal.getWidth(), bmpOriginal.getHeight(), matrix, true);  
    }  
  
    /** 
     * 18.ͼƬ���� 
     *  
     * @param bmpOriginal 
     * @param w 
     *            ��С��Ŵ�ɵĿ� 
     * @param h 
     *            ��С��Ŵ�ɵĸ� 
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
     * 19.ͼƬ��ת 
     *  
     * @param bm 
     * @param flag 
     *            0Ϊˮƽ��ת��1Ϊ��ֱ��ת 
     * @return 
     */  
    public static Bitmap reverseBitmap(Bitmap bmp, int flag) {  
        float[] floats = null;  
        switch (flag) {  
        case 0: // ˮƽ��ת  
            floats = new float[] { -1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };  
            break;  
        case 1: // ��ֱ��ת  
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
     * 20.����һ��ͼƬ������ת����ȡһ������ԭͼ�ϳɵ�Ӱͼ
     * @param bitmap
     * @return
     */
    public static Bitmap createReflectBitmap(Bitmap originBitmap){
    	
    	int reflectionGap = 2;
		//ȡԭͼ�ߴ�
		int width = originBitmap.getWidth();
		int height = originBitmap.getHeight();

		Matrix matrix = new Matrix();
		//���·�ת����
		matrix.preScale(1, -1);

		//ȡԭͼ�°벿�ֲ����з�ת
		Bitmap reflectionImage = Bitmap.createBitmap(originBitmap, 0,
				height / 2, width, height / 2, matrix, false);

		//��������Ӱ��ͼԭ��
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		//��ͼԭ�����뻭����ʹ�û�����ͼԭ�ͽ��в���
		Canvas canvas = new Canvas(bitmapWithReflection);

		//�ڻ����ϻ���ԭͼ
		canvas.drawBitmap(originBitmap, 0, 0, null);

		//����Ĭ�ϻ�ˢ���ߣ��ڻ����ϻ���һ������������
		Paint deafaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap,
				deafaultPaint);
		
		//�ڻ����ϻ�����ǰ��ͼ�ĵ�Ӱͼ
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		//������Ӱ��ˢ����
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, originBitmap
				.getHeight(), 0, bitmapWithReflection.getHeight()
				+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		//�ڻ�����ԭͼ�·����������������ӰЧ��
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);   	
		
		return bitmapWithReflection;
    }
}