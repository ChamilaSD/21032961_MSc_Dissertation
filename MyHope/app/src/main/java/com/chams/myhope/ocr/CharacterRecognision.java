package com.chams.myhope.ocr;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;

import android.net.Uri;
//import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextRecognizer;

/**
 * Created by SamanthaDi on 1/17/2017.
 */

public class CharacterRecognision {

    private static final String LOG_TAG = "Text API";
    private static final int PHOTO_REQUEST = 10;
    private TextView scanResults;
    private Uri imageUri;
    private TextRecognizer detector;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";

    public String imageOCR(Bitmap bitmap, Context context){
        String ocrText = "";
        try {
            detector = new TextRecognizer.Builder(context).build();
//             bitmap = decodeBitmapUri(context, imageUri);
            if (detector.isOperational() && bitmap != null) {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<TextBlock> textBlocks = detector.detect(frame);
                String blocks = "";
                String lines = "";
                String words = "";
                for (int index = 0; index < textBlocks.size(); index++) {
                    //extract scanned text blocks here
                    TextBlock tBlock = textBlocks.valueAt(index);
                    blocks = blocks + tBlock.getValue() + "\n" + "\n";
                    for (Text line : tBlock.getComponents()) {
                        //extract scanned text lines here
                        lines = lines + line.getValue() + "\n";
                        for (Text element : line.getComponents()) {
                            //extract scanned text words here
                            words = words + element.getValue() + ", ";
                        }
                    }
                }
                if (textBlocks.size() == 0) {
                    System.out.println("Scan Failed: Found nothing to scan");
                } else {
                    ocrText = blocks;
                    Log.d("blocks", blocks);
//                    scanResults.setText(scanResults.getText() + "Blocks: " + "\n");
//                    scanResults.setText(scanResults.getText() + blocks + "\n");
//                    scanResults.setText(scanResults.getText() + "---------" + "\n");
//                    scanResults.setText(scanResults.getText() + "Lines: " + "\n");
//                    scanResults.setText(scanResults.getText() + lines + "\n");
//                    scanResults.setText(scanResults.getText() + "---------" + "\n");
//                    scanResults.setText(scanResults.getText() + "Words: " + "\n");
//                    scanResults.setText(scanResults.getText() + words + "\n");
//                    scanResults.setText(scanResults.getText() + "---------" + "\n");
                }
            } else {
                scanResults.setText("Could not set up the detector!");
            }
        } catch (Exception e) {
            Toast.makeText(context, "Failed to load Image", Toast.LENGTH_SHORT)
                    .show();
//            Log.e(LOG_TAG, e.toString());
        }
        return ocrText;
    }



}
