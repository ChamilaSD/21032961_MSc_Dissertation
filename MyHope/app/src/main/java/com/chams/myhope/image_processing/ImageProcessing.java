package com.chams.myhope.image_processing;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.chams.myhope.R;
import com.chams.myhope.ocr.CharacterRecognision;
import com.google.android.material.color.utilities.Score;


import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by SamanthaDi on 1/16/2017.
 */

public class ImageProcessing {

    Bitmap bmp;
    int[] intSigns = new int[4];
    String[] strSigns = new String[4];
    int[] intCurrency = new int[8];
    String[] strCurrency = new String[8];


    public Bitmap captureBitmap(Mat rgba, CameraBridgeViewBase mOpenCvCameraView, Context mContext){
        bmp = Bitmap.createBitmap(mOpenCvCameraView.getWidth()/4,mOpenCvCameraView.getHeight()/4, Bitmap.Config.ARGB_8888);
        Bitmap rotatedBmp = null;
        try {
            bmp = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.ARGB_8888);

            Utils.matToBitmap(rgba, bmp);
//            mBitmap.setImageBitmap(bmp);
//            mBitmap.invalidate();

//            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.male_image);
//            save(bmp);
//            load();

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
             rotatedBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
//            saveImage(rotatedBmp);
//            mContext.sendBroadcast(new Intent(
//                    Intent.ACTION_MEDIA_MOUNTED,
//                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));


        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        return rotatedBmp;
    }


    public void saveImage(Bitmap finalBitmap, String strFileName, Context mContext){

        String root = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        }
        File myDir = new File(root + "/hope");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
//        n = generator.nextInt(n);
//        String fname = "currency-" + n + ".png";
        String fname = strFileName; // "object.png";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Image attached to media files..............
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            MediaScannerConnection.scanFile(mContext, new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        }
    }


    public String imageMapping(Bitmap bitmap, int match_method, Context mContext){

        intSigns[0] = R.drawable.pedestrial_crossing;
        intSigns[1] = R.drawable.warning_men_at_work;
//        intSigns[2] = R.drawable.men_at_work;
        intSigns[2] = R.drawable.toilets;

        strSigns[0] = "Pedestrian crossing";
        strSigns[1] = "Warning men at work";
//        strSigns[2] = "Men at work";
        strSigns[2] = "Toilets";

        CharacterRecognision cr = new CharacterRecognision();
        double l2_norm = 0;
        Mat img= new Mat();
        Mat templ= new Mat();
        String strResult = "";
        double x=0; double y = 0;
        for(int i=0; i< 3; i++) {
            try {
                Utils.bitmapToMat(bitmap, img);
//            img = new Mat (bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC3); //CvType.CV_8UC3
                templ = Utils.loadResource(mContext, intSigns[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Imgproc.resize(templ, templ, img.size());

            // / Create the result matrix
            int result_cols = img.cols() - templ.cols() + 1;
            int result_rows = img.rows() - templ.rows() + 1;
        try {
//                Mat result = new Mat(img.size(), CvType.CV_8UC1);
                Mat result = new Mat(result_rows, result_cols, CvType.CV_32F); // CvType.CV_32FC1);

//            Core.compare(img, templ, result, Core.CMP_LE);
                // / Do the Matching and Normalize
                Imgproc.matchTemplate(img, templ, result, Imgproc.TM_CCOEFF);
                Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
                // / Localizing the best match with minMaxLoc
                Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
                double score = Core.countNonZero(result);

                Point matchLoc;
                if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
                    matchLoc = mmr.minLoc;
                } else {
                    matchLoc = mmr.maxLoc;
                }

                // / Show the rectangle
    //        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()), new Scalar(0, 255, 0));
    //        Imgproc.rectangle(img, new Point(matchLoc.x, matchLoc.y), new Point(matchLoc.x + templ.width(), matchLoc.y + templ.height()),new Scalar(0, 255, 0));

                Log.d("MATCHLOC",matchLoc.x + " , " + templ.width() + " , " + matchLoc.y + " , " + templ.height() + "  - " + strSigns[i]);
                Log.d("MATCHLOC", "Similarity Score: " + score * 100 + "%");
                //            Log.d("MATCHLOC", l2_norm+"");
                // Save the visualized detection.
    //        System.out.println("Writing "+ "outFile.png");
    //        Toast.makeText(getApplicationContext(),"Object identified",Toast.LENGTH_SHORT).show();
    //        Imgcodecs.imwrite(outFile, img);
    //        boolean x = Imgcodecs.imwrite("tpresult.png", result);
    //        System.out.println(x);


                if (matchLoc.x > 150 && matchLoc.y > 150) {
//                    if(strResult.equals("")) {
                        if (x < matchLoc.x && y < matchLoc.y) {
                            strResult = cr.imageOCR(bitmap, mContext);
                            x = matchLoc.x;
                            y = matchLoc.y;
                            if(strResult.equals("") || strResult.length()<=5)
                                strResult = strSigns[i];
                        }
//                    }
                }

            } catch (Exception e){
                System.out.println(e.getMessage());
                strResult = "";
            }
        }
        if ( strResult.equals(""))
            strResult = "No signs here";
        else {
            if(strResult.toLowerCase().contains("men at work"))
                strResult = strResult + " sign is here. Be careful";
            else
                strResult = strResult + " sign is here.";
        }
        return strResult;
    }


}
