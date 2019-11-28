package com.app.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.jbarcode.encode.Code128Encoder;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void test(View view) {
        ImageView ivCode = findViewById(R.id.ivCode);
        Bitmap bitmap = getBarCodeWithoutPadding("a25");
        ivCode.setImageBitmap(bitmap);
    }

    private static int getBarCodeNoPaddingWidth(int expectWidth,String contents,int maxWidth){
        boolean[] code = new Code128Writer(). encode(contents);
        int inputWidth = code.length;
        double outputWidth = (double) Math.max(expectWidth, inputWidth);
        double multiple = outputWidth / inputWidth;
        //优先取大的
        int returnVal;
        int ceil = (int) Math.ceil(multiple);
        if(inputWidth * ceil <= maxWidth){
            returnVal =  inputWidth * ceil;
        }else {
            int floor = (int) Math.floor(multiple);
            returnVal =  inputWidth * floor;
        }
        return returnVal;
    }

    /** @param context 尽量用activity,以防使用过屏幕适配工具类后application context 和activity里的desplaymetric里的dpidensity不一致
     @param expectWidth 期望的宽度
     @param maxWidth 最大允许宽度
      * @param contents 生成条形码的内容
     * @param height
     * @return
     */
    public Bitmap getBarCodeWithoutPadding(String contents){
        int width = 100;
        int widthMax = 150;
        int heightExpect = 50;
        int realWidth = getBarCodeNoPaddingWidth(width,contents,widthMax);
        return syncEncodeBarcode(contents, realWidth, heightExpect,0);
    }

    /**
     * 同步创建条形码图片
     *
     * @param content  要生成条形码包含的内容
     * @param width    条形码的宽度，单位px
     * @param height   条形码的高度，单位px
     * @param textSize 字体大小，单位px，如果等于0则不在底部绘制文字
     * @return 返回生成条形的位图
     *
     * 白边问题:
     *   https://blog.csdn.net/sunshinwong/article/details/50156017
     *已知高度,计算宽度:
     *
     */
    private static Bitmap syncEncodeBarcode(String content, int width, int height, int textSize) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 0);

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, width, height, hints);
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//            if (textSize > 0) {
//                bitmap = showContent(bitmap, content, textSize);
//            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
