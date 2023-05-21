package com.chams.myhope.color;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.chams.myhope.Utilities.GlobalVariables;

import java.io.ByteArrayOutputStream;

/**
 * Created by SamanthaDi on 8/27/2016.
 */
public class GeneralColor {
    String colorName="";
//    TextToSpeech textToSpeech;

//    public GeneralColor(Context mContext){
//        textToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if(status != TextToSpeech.ERROR) {
//                    textToSpeech.setLanguage(Locale.UK);
//                }
//            }
//        });
//    }

    public String getColor(int mRed, int mGreen, int mBlue){

//        if (mRed>=125 && mGreen<=125 & mBlue<=125) colorName = GlobalVariables.red;

        if (mRed>=150 && mRed<=255 && mGreen>150 && mGreen<=255 && mBlue>150 && mBlue<=255)
            colorName = GlobalVariables.white;
        else if (mRed>=40 && mRed<=150 && mGreen>40 && mGreen<=150 && mBlue>40 && mBlue<=150){
            if(mGreen > mRed && mGreen> mBlue)
                colorName = GlobalVariables.green;
            else if(mRed > mGreen && mRed> mBlue) {
                if (mRed > 80 && mGreen > 80 && mBlue > 80)
                    colorName = GlobalVariables.pink;
                else
                    colorName = GlobalVariables.red;
            } else if(mBlue > mRed && mBlue> mGreen)
                colorName = GlobalVariables.blue;
            else
                colorName = GlobalVariables.gray;
        }

        else if (mRed>=0 && mRed<=90 && mGreen>=0 && mGreen<=90 && mBlue>=0 & mBlue<=90) {
            if(mRed>=15 && mGreen>=15 && mBlue>=15){
                if(mRed>mGreen && mRed>mBlue)
                    colorName = GlobalVariables.maroon;
                else if(mGreen>mRed && mGreen>mBlue)
                    colorName = GlobalVariables.darkGreen;
                else if(mBlue>mGreen && mBlue>mRed)
                    colorName = GlobalVariables.darkBlue;
                else colorName = GlobalVariables.black;
            }
            else colorName = GlobalVariables.black;
        }

        else if (mRed>=70 && mRed<=255 && mGreen>=0 && mGreen<=70 && mBlue>=0 & mBlue<=70) {
            if (mRed > 100)
                colorName = GlobalVariables.red;
            else colorName = GlobalVariables.maroon;
        }
        else if (mRed>=0 && mRed<=100 && mGreen>=100 && mGreen<=255 && mBlue>=0 & mBlue<=239)
            colorName = GlobalVariables.green;
        else if (mRed>=120 && mRed<=255 && mGreen>=100 && mGreen<=220 && mBlue>=0 & mBlue<=150)
            colorName = GlobalVariables.yellow;
        else if (mRed>=0 && mRed<=25 && mGreen>=10 && mGreen<=100 && mBlue>=100 & mBlue<=255)
            colorName = GlobalVariables.blue;
        return colorName;
    }

    public void colorSpeeker(TextToSpeech mTextToSpeech, String mColor, Context mContext) {
        if (!mColor.equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                mTextToSpeech.speak(GlobalVariables.itIs + mColor, TextToSpeech.QUEUE_FLUSH, null);
            }
//            Toast.makeText(mContext, mColor, Toast.LENGTH_SHORT).show();
        }else {
//            mTextToSpeech.speak(GlobalVariables.colorNotClear, TextToSpeech.QUEUE_FLUSH, null);
//            Toast.makeText(mContext, GlobalVariables.colorNotClear, Toast.LENGTH_SHORT).show();
        }
    }

    public String getColorName(Bitmap bmp){

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // bmp.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
        Bitmap resizebitmap = Bitmap.createBitmap(bmp, bmp.getWidth() / 2, bmp.getHeight() / 2, 60, 60);
//        iv_image.setImageBitmap(rotateImage(resizebitmap, 90));
//                    iv_image.setImageBitmap(resizebitmap);

        int color = getAverageColor(resizebitmap);
        color = resizebitmap.getPixel(resizebitmap.getWidth()/2,resizebitmap.getHeight()/2);
        int[] colorArgb = averageARGB(resizebitmap);
        Log.i("Color Int", color + "");
        int red = Color.red(resizebitmap.getPixel(resizebitmap.getWidth()/2,resizebitmap.getHeight()/2));
        int green = Color.green(resizebitmap.getPixel(resizebitmap.getWidth()/2,resizebitmap.getHeight()/2));
        int blue = Color.blue(resizebitmap.getPixel(resizebitmap.getWidth()/2,resizebitmap.getHeight()/2));

        // int color =
        // resizebitmap.getPixel(resizebitmap.getWidth()/2,resizebitmap.getHeight()/2);

        String strColor = String.format("#%06X", 0xFFFFFF & color);

//                    tvRgbRed.setText(String.valueOf(colorArgb[1]));
//                    tvRgbGreen.setText(String.valueOf(colorArgb[2]));
//                    tvRgbBlue.setText(String.valueOf(colorArgb[3]));

        String colorName; // = sColorNameMap.get(strColor);

        colorName = getColor(colorArgb[1], colorArgb[2], colorArgb[3]);

       return colorName;
    }

    public int getAverageColor(Bitmap bitmap) {
        int redBucket = 0;
        int greenBucket = 0;
        int blueBucket = 0;
        int pixelCount = 0;

        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int c = bitmap.getPixel(x, y);

                pixelCount++;
                redBucket += Color.red(c);
                greenBucket += Color.green(c);
                blueBucket += Color.blue(c);
                // does alpha matter?
            }
        }

        int averageColor = Color.rgb(redBucket / pixelCount, greenBucket
                / pixelCount, blueBucket / pixelCount);
        return averageColor;
    }

    int[] averageARGB(Bitmap pic) {
        int A, R, G, B;
        A = R = G = B = 0;
        int pixelColor;
        int width = pic.getWidth();
        int height = pic.getHeight();
        int size = width * height;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixelColor = pic.getPixel(x, y);
                A += Color.alpha(pixelColor);
                R += Color.red(pixelColor);
                G += Color.green(pixelColor);
                B += Color.blue(pixelColor);
            }
        }

        A /= size;
        R /= size;
        G /= size;
        B /= size;

        int[] average = {A, R, G, B};
        return average;

    }
}
