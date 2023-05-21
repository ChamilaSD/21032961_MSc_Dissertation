package com.chams.myhope;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY_INV;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.chams.myhope.Utilities.GlobalVariables;
import com.chams.myhope.color.GeneralColor;
import com.chams.myhope.image_processing.ImageProcessing;
import com.chams.myhope.location.AppLocationService;
import com.chams.myhope.location.GPSTracker;
import com.chams.myhope.location.LocationTrack;
import com.chams.myhope.ocr.CharacterRecognision;
import com.google.android.gms.common.api.GoogleApiClient;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

//@TargetApi(Build.VERSION_CODES.FROYO)
public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, RecognitionListener{ //} SurfaceHolder.Callback,  {
    Context context = this;
    private static final String TAG = "MainActivity";
    AppLocationService appLocationService;
    // GPSTracker class
    GPSTracker gps;
    static TextToSpeech textToSpeech;
    private static boolean isColor = false, isObject = false, isCurrency = false, isLanguage = false;
    private ImageView iv_image;
    private static final int REQUEST_RECORD_PERMISSION = 100;

    // a variable to store a reference to the Surface View at the main.xml file
    private SurfaceView sv;

    // a bitmap to display the captured image
    private Bitmap bmp;
    Mat rgba;

    // Camera variables
// a surface holder
    private SurfaceHolder sHolder;
    // a variable to control the camera
    Camera mCamera;
    int CAMERA_CODE = 101;

    // the camera parameters
    private Camera.Parameters parameters;
    Camera.PictureCallback mCall;
    Button bnSpeech, bnObject, bnColor, bnLocation, bnCurrency, bnTime, bnDate, bnLanguage, bnFirst, bnLast;
    Handler handler;
    TextView tvColorName, tvColorHex, tvRgbRed, tvRgbGreen, tvRgbBlue;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int width = 0, height = 0, appMode=0;
    private Camera.Size pictureSize;
    String colorName = "";
    boolean mStopHandler = false;
    private static final HashMap<String, String> sColorNameMap;

    //speach activity................
    Button bnSpeachNow;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    //    private Socket client;
    private PrintWriter printwriter;
    String cmd;
    LinearLayout loutTop, loutBottom;
    RelativeLayout loutJavaCamera;
    HorizontalScrollView hScrollView;
    int intFocus = 0;
    private static final int VM_NORMAL = 0;
    private int mVMode = VM_NORMAL;
    private int mHLowerMax = 15;
    private int mHUpperMin = 165;
    private int mSMin = 110;
    private int mVMin = 110;
    private static int readingTimes = 0;
    private static long startTime;
    private Uri imageUri;
    private String directoryName = "images";
    private String fileName = "myobject.png";
    private boolean external;

    ImageProcessing imageProcessing;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    static {
        sColorNameMap = new HashMap();
        sColorNameMap.put("#000000", "black");
        sColorNameMap.put("#A9A9A9", "darkgray");
        sColorNameMap.put("#808080", "gray");
        sColorNameMap.put("#D3D3D3", "lightgray");
        sColorNameMap.put("#FFFFFF", "white");


    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

//            if (cameraBridgeViewBase != null) {
            // do your stuff - don't create a new runnable here!

//                mCamera.setPreviewDisplay((getHolder());
//                mCamera.startPreview();
//                mCamera.takePicture(null, null, mCall);

//                if (!mStopHandler) {
//                    handler.postDelayed(this, 500);
//                }
//            }
        }
    };

//    private GoogleApiClient client;

    private CameraBridgeViewBase cameraBridgeViewBase;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback (context) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    cameraBridgeViewBase.setMaxFrameSize(960, 540);
                    cameraBridgeViewBase.enableView();
                } break;
                default:
                {
                    Log.i(TAG, "OpenCV can't load");
//                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        getPermission();
        cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.opencvObjectView);
//        cameraBridgeViewBase.setVisibility(View.VISIBLE);
//        cameraBridgeViewBase.setCvCameraViewListener(this);
//        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
//            @Override
//            public void onCameraViewStarted(int width, int height) {
//
//            }
//
//            @Override
//            public void onCameraViewStopped() {
//
//            }
//
//            @Override
//            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//                return inputFrame.rgba();
//            }
//        });
        if (OpenCVLoader.initDebug()) {
            Log.d("Opencv", "OpenCV installed successfully");
            cameraBridgeViewBase.enableView();
        } else {
            Log.d("Opencv", "OpenCV not installed ");
        }

        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        if (savedInstanceState != null) {
//            try {
                imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
//            } catch (Exception e){
//                Log.d(TAG, e.getMessage());
//            }
        }
        //capturing..............
        handler = new Handler();
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        bnSpeech = (Button)findViewById(R.id.bnSpeech);
//        loutJavaCamera = (RelativeLayout)findViewById(R.id.loutJavaCamera);

        bnObject = (Button)findViewById(R.id.bnObject);
        bnColor = (Button)findViewById(R.id.bnColor);
        bnLocation = (Button)findViewById(R.id.bnLocation);
        bnCurrency = (Button)findViewById(R.id.bnCurrency);
        bnSpeachNow = (Button)findViewById(R.id.bnSpeachNow);
        bnTime = (Button)findViewById(R.id.bnTime);
        bnDate = (Button)findViewById(R.id.bnDate);
        bnLanguage = (Button)findViewById(R.id.bnLanguage);
        bnFirst = (Button)findViewById(R.id.bnFirst);
        bnLast = (Button)findViewById(R.id.bnLast);
        tvColorName = (TextView) findViewById(R.id.tvColorName);
        tvColorHex = (TextView) findViewById(R.id.tvColorHex);
        tvRgbRed = (TextView) findViewById(R.id.tvRgbRed);
        tvRgbGreen = (TextView) findViewById(R.id.tvRgbGreen);
        tvRgbBlue = (TextView) findViewById(R.id.tvRgbBlue);
        loutTop = (LinearLayout) findViewById(R.id.loutTop);
        hScrollView = (HorizontalScrollView) findViewById(R.id.hScrollView);

//        loutBottom = (LinearLayout) findViewById(R.id.loutBottom);
        // get the Image View at the main.xml file
        iv_image = (ImageView) findViewById(R.id.imageView);
//        sv = (SurfaceView) findViewById(R.id.surfaceView);
        // Get a surface
//        sHolder = sv.getHolder();
//        sHolder.addCallback(this);
        // tells Android that this surface will have its data constantly
        // replaced
//        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //Activate Opencv camera view................

//        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
//        cameraBridgeViewBase.enableView();
//        cameraBridgeViewBase.setCvCameraViewListener(MainActivity.this);

        bnSpeech.setFocusable(true);
        bnSpeech.setFocusableInTouchMode(true);///add this line
        bnSpeech.requestFocus();
        voiceOut(textToSpeech, "Speech", context);
//        intFocus = 1;



        bnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceOut(textToSpeech, "Sorry, you are on top most of the list. Please press the down key", context);
            }
        });
        bnFirst.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                bnLanguage.setFocusable(true);
                bnLanguage.setFocusableInTouchMode(true);///add this line
                bnLanguage.requestFocus();
//                voiceOut(textToSpeech, "Sorry, you are on top most of the list. Please press the down key", context);
            }
        });
        bnSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appMode = 1; //speech mode
                ActivityCompat.requestPermissions (MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_PERMISSION);
//                speech.startListening(recognizerIntent);
            }
        });

        bnSpeech.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    if(intFocus == 0) {
                    Toast.makeText(context, "Speech", Toast.LENGTH_SHORT).show();
                    voiceOut(textToSpeech, "Speech", context);
//                    } else if (intFocus == 1)
//                        voiceOut(textToSpeech, "Sorry, you are on top most of the list. Please press the down key", context);
//                intFocus = 1;
                }
            }
        });

        bnObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appMode = 2;
                isObject = true;
                cameraBridgeViewBase.setCvCameraViewListener(MainActivity.this);
                objTimerHandler.postDelayed(objTimerRunnable,1000);
            }
        });

        bnObject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isObject = false;
                if (hasFocus) {
                    intFocus = 0;
                    Toast.makeText(context, "Object", Toast.LENGTH_SHORT).show();
                    voiceOut(textToSpeech, "Object", context);
                }
            }
        });

        bnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//        mCamera.takePicture(null, null, mCall);
                appMode = 3;
                colorName = "";
                isColor = true;
                cameraBridgeViewBase.setCvCameraViewListener(MainActivity.this);

//                handler.postDelayed(runnable,3000);

//                cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
//                cameraBridgeViewBase.enableView();

//                cameraBridgeViewBase.disableView();
//                cameraBridgeViewBase.setAlpha(0);
//                loutJavaCamera.setVisibility(View.GONE);
            }
        });

        bnColor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isColor = false;
                if (hasFocus) {
                    intFocus = 0;
                    voiceOut(textToSpeech, "Color", context);
                }
            }
        });

        bnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appMode = 4;
                getLocation();
            }
        });
        bnLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    intFocus = 0;
                    Toast.makeText(context, "Location", Toast.LENGTH_SHORT).show();
                    voiceOut(textToSpeech, "Location", context);
                }
            }
        });

        bnCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appMode = 5;
                isCurrency = true;
                readingTimes = 0;
                startTime = SystemClock.elapsedRealtime();
                cameraBridgeViewBase.setCvCameraViewListener(MainActivity.this);
                currTimerHandler.postDelayed(currTimerRunnable, 1000);
            }
        });
//        bnCurrency.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
////                Intent intent = new Intent(context,SpeachActivity.class);
////                finish();
////                startActivity(intent);
//                hScrollView.setVisibility(View.GONE);
//                loutTop.setVisibility(View.GONE);
//                bnSpeachNow.setVisibility(View.VISIBLE);
//                return false;
//            }
//        });
        bnCurrency.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    isCurrency = false;
                    intFocus = 0;
                    Toast.makeText(context, "Currency", Toast.LENGTH_SHORT).show();
                    voiceOut(textToSpeech, "Currency", context);
                }
            }
        });

        bnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appMode = 6;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                    textToSpeech.speak(getTime(), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        bnTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    intFocus = 0;
                    Toast.makeText(context, "Time", Toast.LENGTH_SHORT).show();
                    voiceOut(textToSpeech, "Time", context);
                }
            }
        });

        bnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appMode = 7;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                    textToSpeech.speak(getDate(), TextToSpeech.QUEUE_FLUSH, null);
                }

            }
        });
        bnDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    intFocus = 0;
                    Toast.makeText(context, "Date", Toast.LENGTH_SHORT).show();
                    voiceOut(textToSpeech, "Date", context);
                }
            }
        });

        bnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appMode = 8;
//                cameraBridgeViewBase.setCvCameraViewListener(MainActivity.this);
//                handler.postDelayed(ocrRunnable,10000);
            }
        });

        bnLanguage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if(intFocus == 0) {
                        Toast.makeText(context, "Language", Toast.LENGTH_SHORT).show();
                        voiceOut(textToSpeech, "Language", context);
                    } else if (intFocus == 2)
                        voiceOut(textToSpeech, "Sorry, you are on end of the list. Please press the up key", context);
//                    intFocus = 2;
                }
            }
        });

        bnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceOut(textToSpeech, "Sorry, you are on end of the list. Please press the up key", context);
            }
        });
        bnLast.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    bnSpeech.setFocusable(true);
                    bnSpeech.setFocusableInTouchMode(true);///add this line
                    bnSpeech.requestFocus();

//                voiceOut(textToSpeech, "Sorry, you are on end of the list. Please press the up key", context);
                }    }
        });

        bnSpeachNow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                Intent intent = new Intent(context,SpeachActivity.class);
//                finish();
//                startActivity(intent);
                bnSpeachNow.setVisibility(View.GONE);
                hScrollView.setVisibility(View.VISIBLE);
                loutTop.setVisibility(View.VISIBLE);
                return false;
            }
        });

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        bnSpeachNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions (MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_PERMISSION);
                    speech.startListening(recognizerIntent);
            }
        });

//        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
//        cameraBridgeViewBase.enableView();
//        cameraBridgeViewBase.setCvCameraViewListener(this);
    }

    void getPermission (){
        if(checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }
    }

    void getLocation(){
        // create class object
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }else {
            // Write you code here if permission already given.
            // Call LocationHelper

            gps = new GPSTracker(this);
            // check if GPS enabled
            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                // \n is for new line
//            Toast.makeText(context, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                try {
                    getAddress(latitude, longitude);
//            getAddress(6.886813d, 79.882527d);
                } catch (Exception e) {
                    Log.d("TAG", e.getMessage());
                }
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
        }
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String   add0 = obj.getAddressLine(0);
            String   add1 = obj.getAddressLine(1); // .getAddressLine(1); //featured name
            String   add2 = obj.getAddressLine(2); //country

            locationSpeeker(textToSpeech,add0 +", "+ add1,context);
            Toast.makeText(context, "Your Location is - \n" + add0 + "\n" + add1 + "\n" + add2, Toast.LENGTH_LONG).show();
            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            locationSpeeker(textToSpeech,e.getMessage(),context);
        }
    }

    public void locationSpeeker(TextToSpeech mTextToSpeech, String mColor, Context mContext){
        mTextToSpeech.speak( mColor, TextToSpeech.QUEUE_FLUSH, null);
//        Toast.makeText(mContext,mColor,Toast.LENGTH_SHORT).show();
    }

    public void voiceOut(TextToSpeech mTextToSpeech, String focusOn, Context mContext){
            mTextToSpeech.speak( focusOn, TextToSpeech.QUEUE_FLUSH, null);
//        Toast.makeText(mContext,mColor,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isColor = false;
        isObject = false;
//        if (cameraBridgeViewBase != null)
//            cameraBridgeViewBase.disableView();
//        mCamera.release();
//
////        mCamera.stopPreview();
//        if(textToSpeech !=null){
//            textToSpeech.stop();
//            textToSpeech.shutdown();
//        }
//        if (speech != null) {
//            speech.destroy();
//            Log.i("LOG_TAG", "destroy");
//        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        textToSpeech.setLanguage(Locale.ENGLISH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//            if (cameraBridgeViewBase != null)
//                cameraBridgeViewBase.disableView();
//                mCamera.release();
////        mCamera.stopPreview();
//
//        if(textToSpeech !=null){
//            textToSpeech.stop();
//            textToSpeech.shutdown();
//        }
//        if (speech != null) {
//            speech.destroy();
//            Log.i("LOG_TAG", "destroy");
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ActivityResult", ""+ requestCode);
        if (requestCode == CAMERA_CODE && data != null) {
            try {
//                bitmap = (Bitmap) data.getExtras().get("data");
//                imageView.setImageBitmap(bitmap);
//                mat = new Mat();
//                Utils.bitmapToMat(bitmap, mat);
//                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
//                Utils.matToBitmap(mat, bitmap);
//                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermission();
                } else {
                    //permission denied
                }
                return;
            }
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast .LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onBackPressed() {
            textToSpeech.speak("Sorry, you can't exit here", TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public static Bitmap decodeBitmap(byte[] data) {

        Bitmap bitmap = null;
        BitmapFactory.Options bfOptions = new BitmapFactory.Options();
        bfOptions.inDither = false; // Disable Dithering mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            bfOptions.inPurgeable = true; // Tell to gc that whether it needs free
        }
        // memory, the Bitmap can be cleared
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            bfOptions.inInputShareable = true; // Which kind of reference will be
        }
        // used to recover the Bitmap data
        // after being clear, when it will
        // be used in the future
        bfOptions.inTempStorage = new byte[32 * 1024];

        if (data != null)
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                    bfOptions);
        return bitmap;
    }

    public Bitmap rotateImage(Bitmap src, float degree) {
        // create new matrix object
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        // return new bitmap rotated using matrix
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(),
                matrix, true);
    }

    private void setBesttPictureResolution() {
        // get biggest picture size
        width = pref.getInt("Picture_Width", 0);
        height = pref.getInt("Picture_height", 0);

        if (width == 0 | height == 0) {
            pictureSize = getBiggesttPictureSize(parameters);
            if (pictureSize != null)
                parameters
                        .setPictureSize(pictureSize.width, pictureSize.height);
            // save width and height in sharedprefrences
            width = pictureSize.width;
            height = pictureSize.height;
            editor.putInt("Picture_Width", width);
            editor.putInt("Picture_height", height);
            editor.commit();

        } else {
            // if (pictureSize != null)
            parameters.setPictureSize(width, height);
        }
    }

    private Camera.Size getBiggesttPictureSize(Camera.Parameters parameters) {
        Camera.Size result = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            for (Camera.Size size : parameters.getSupportedPictureSizes()) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        return (result);
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.dpit.deuron.hopefinal/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.dpit.deuron.hopefinal/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        ocrTimerHandler.removeCallbacks(ocrTimerRunnable);
//        ocrNameBoardHandler.removeCallbacks(ocrTimerRunnable);
//        objTimerHandler.removeCallbacks(objTimerRunnable);
//        client.disconnect();
    }

    //Speach recognizer...........................................
    @Override
    public void onResume() {
        super.onResume();
        cameraBridgeViewBase.enableView();
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i("LOG_TAG", "onBeginningOfSpeech");
//        progressBar.setIndeterminate(false);
//        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i("LOG_TAG", "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i("LOG_TAG", "onEndOfSpeech");
//        progressBar.setIndeterminate(true);
//        toggleButton.setChecked(false);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d("LOG_TAG", "FAILED " + errorMessage);
//        returnedText.setText(errorMessage);
//        toggleButton.setChecked(false);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i("LOG_TAG", "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i("LOG_TAG", "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i("LOG_TAG", "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i("LOG_TAG", "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        cmd = matches.get(0);
        for (String result : matches)
            text += result + "\n";

        System.out.println(text);
        getFeedback(text);
//        returnedText.setText(text);
//        SendMessage sendMessageTask = new SendMessage();
//        sendMessageTask.execute();
    }

    void getFeedback(String mText){
        //Identifying color.................
        if((mText.toLowerCase().contains("room"))||(mText.toLowerCase().contains("mushroom"))||(mText.toLowerCase().contains("washroom"))||((mText.toLowerCase().contains("wash room")) )) {
            appMode = 2;
            startTime = SystemClock.elapsedRealtime();
            cameraBridgeViewBase.setCvCameraViewListener(MainActivity.this);
//            ocrTimerHandler.postDelayed(ocrTimerRunnable, 1000);
        } else if((mText.toLowerCase().contains("street"))||((mText.toLowerCase().contains("road")) ) ||((mText.toLowerCase().contains("road")) )) {
            appMode = 2;
            startTime = SystemClock.elapsedRealtime();
            cameraBridgeViewBase.setCvCameraViewListener(MainActivity.this);
//            ocrNameBoardHandler.postDelayed(ocrNameBoardRunnable, 1000);
        }
        else if((mText.toLowerCase().contains("what"))&&((mText.toLowerCase().contains("color")) ||(mText.toLowerCase().contains("colour")))) {
//            handler.postDelayed(runnable, 100);
            appMode = 3;
            colorName = "";
            isColor = true;
            cameraBridgeViewBase.setCvCameraViewListener(MainActivity.this);
        }

        else if(((mText.toLowerCase().contains("what")) && mText.toLowerCase().contains("location")) || ((mText.toLowerCase().contains("where")) && mText.toLowerCase().contains("i")))
            getLocation();
        else if(((mText.toLowerCase().contains("what")) && mText.toLowerCase().contains("currency")) || ((mText.toLowerCase().contains("what")) && mText.toLowerCase().contains("value")) ) {
            appMode = 5;
            isCurrency = true;
            readingTimes = 0;
            startTime = SystemClock.elapsedRealtime();
            cameraBridgeViewBase.setCvCameraViewListener(MainActivity.this);
            handler.postDelayed(currTimerRunnable, 1000);
        }
        else if(((mText.toLowerCase().contains("what")) && mText.toLowerCase().contains("date")))
            textToSpeech.speak(getDate(), TextToSpeech.QUEUE_FLUSH, null);
        else if(((mText.toLowerCase().contains("what")) && mText.toLowerCase().contains("time")))
            textToSpeech.speak(getTime(), TextToSpeech.QUEUE_FLUSH, null);
        else
            textToSpeech.speak(GlobalVariables.sorryIcantGetYou, TextToSpeech.QUEUE_FLUSH, null);
    }

    String getDate(){
        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String dateString = sdf.format(date);
        dateString = "Today is "+ dateString;
        return dateString;
    }

    String getTime(){
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        String dateString = sdf.format(date);
        dateString = "It is "+ dateString;
        return dateString;
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i("LOG_TAG", "onRmsChanged: " + rmsdB);
//        progressBar.setProgress((int) rmsdB);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        Mat mRgba = inputFrame.rgba();
        rgba = inputFrame.rgba(); // new Mat();
        switch (appMode){
            case 1:
                break;
            case 2: //object
                //        rgba.copyTo(rgba);
                appMode = 0;
                _gamma(rgba, rgba, 1.2);
                Imgproc.medianBlur(rgba, rgba, 5);

                if(rgba != null) {
                    Bitmap bitmap1 = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(rgba, bitmap1);
                    Matrix matrix1 = new Matrix();
                    matrix1.postRotate(90);
                    bitmap1 = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix1, true);
                    width = bitmap1.getWidth();
                    height = bitmap1.getHeight();
                }

//                        Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGB2HSV, 4);
//                Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGBA2GRAY, THRESH_BINARY_INV);
//                        Imgproc.threshold(rgba, rgba, 180, 255, 2);
//                        Imgproc.threshold(rgba, rgba, 150, 255, 0);
//                        _redFilter(rgba, rgba);

//                if(isReadyToObject) {
//                    Toast.makeText(context, "Ready to Object", Toast.LENGTH_SHORT).show();
//                    isReadyToObject = false;
//                    ImageMapping(mRgba, Imgproc.TM_CCOEFF);
//                }
                rgba.copyTo(rgba);
                break;
            case 3: //color
                if(colorName.equals("")) {
                    try {
                        bmp = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(rgba, bmp);
                    } catch (Exception e) {
                        Log.d("Exception", e.getMessage());
                    }
                    GeneralColor generalColor = new GeneralColor();
                    colorName = generalColor.getColorName(bmp);
                    if (isColor) {
                        generalColor.colorSpeeker(textToSpeech, colorName, context);
                    }
                }
                break;
            case 4:
                break;
            case 5: //currency
                Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGB2GRAY, 1);
                // get the thresholded image
                Imgproc.threshold(rgba, rgba , 128, 255, Imgproc.THRESH_BINARY);

                break;
            case 8: //language

//                _gamma(rgba, rgba, 1.2);
//                Imgproc.medianBlur(rgba, rgba, 5);
                //        Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGB2HSV, 4);
//                Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGBA2GRAY, THRESH_BINARY_INV);
//                        Imgproc.threshold(rgba, rgba, 180, 255, 2);

                if(isLanguage){

                }
                break;
        }

//        Mat orig = new Mat();
//        Core.transpose(rgba,rgba);
//        Core.flip(rgba, rgba, 1);
//        rgba.copyTo(rgba);
//        _gamma(rgba, rgba, 1.2);
//        Imgproc.medianBlur(rgba, rgba, 5);
////        Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGB2HLS, 3);
//        Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGB2HSV, 4);
//        Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGBA2GRAY, THRESH_BINARY_INV);

//        Imgproc.threshold(rgba, rgba, 180, 255, 2);
//        Imgproc.threshold(rgba, rgba, 150, 255, 0);
//
//        _redFilter(rgba, rgba);
//        _blueFilter(rgba,rgba);
//        Imgproc.medianBlur(rgba, rgba, 5);
//
//        Mat circles = new Mat();
//        Imgproc.HoughCircles(rgba, circles, Imgproc.HOUGH_GRADIENT, 1.0, 20.0, 50, 30, 5, 1000);
////
//        Mat dst;
//        if (mVMode == VM_NORMAL) {
//            dst = orig;
//            rgba.release();
//        }
//        else if (mVMode == VM_HSV) {
//            orig.release();
//            dst = rgba;
//        }
//        else {
//            throw new AssertionError("Invalid VideoMode: " + mVMode);
//        }
////
//        for(int i = 0; i < circles.cols(); i++) {
//            double[] circ = circles.get(0, i);
//            if (circ == null)
//                break;
//            Point pt = new Point(Math.round(circ[0]), Math.round(circ[1]));
//            int r = (int) Math.round(circ[2]);
//            run(rgba, R.drawable.tp, rgba, Imgproc.TM_CCOEFF);
////            Imgproc.rectangle(rgba, new Point(pt.x - r - 5, pt.y - r - 5), new Point(pt.x + r + 5, pt.y + r + 5), new Scalar(255, 0, 255), 3);
//        }
//        circles.release();

//        Bitmap bm = Bitmap.createBitmap(rgba.cols(),rgba.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(rgba, bm);
//        ImageView iv = (ImageView) findViewById(R.id.imageView);
//        iv.setImageBitmap(bm);
        return rgba;
    }

    private void ImageMapping(Mat img, int match_method){

//        Mat img= new Mat();
        Mat templ= new Mat();

        try {
            img= Utils.loadResource(this, R.drawable.male_image);
            templ= Utils.loadResource(this,R.drawable.female_sign);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // / Create the result matrix
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        // / Do the Matching and Normalize
        Imgproc.matchTemplate(img, templ, result, match_method);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // / Localizing the best match with minMaxLoc
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        Point matchLoc;
        if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;

        } else {
            matchLoc = mmr.maxLoc;
        }

        // / Show me what you got
//        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()), new Scalar(0, 255, 0));
//        Imgproc.rectangle(img, new Point(matchLoc.x, matchLoc.y), new Point(matchLoc.x + templ.width(), matchLoc.y + templ.height()),new Scalar(0, 255, 0));

        System.out.println(matchLoc.x+" , "+templ.width()+" , "+matchLoc.y+" , "+templ.height());
        // Save the visualized detection.
        System.out.println("Writing "+ "outFile.png");
//        Toast.makeText(getApplicationContext(),"Object identified",Toast.LENGTH_SHORT).show();
//        Imgcodecs.imwrite(outFile, img);
//        boolean x = Imgcodecs.imwrite("tpresult.png", result);
//        System.out.println(x);
    }

    private void _redFilter(Mat hsv, Mat dst) {
        Mat lower = new Mat();
        Mat upper = new Mat();
        Core.inRange(hsv, new Scalar(0, mSMin, mVMin), new Scalar(mHLowerMax, 255, 255), lower);
        Core.inRange(hsv, new Scalar(mHUpperMin, mSMin, mVMin), new Scalar(179, 255, 255), upper);

        Core.addWeighted(lower, 1.0, upper, 1.0, 0.0, dst);
    }

    public static void _gamma(Mat srcMat, Mat dstMat, double gamma)
    {
        double invGamma = 1.0/gamma;
        Mat lutMat = new Mat(1, 256, CvType.CV_8UC1);

        int size = (int)(lutMat.total() * lutMat.channels());
        byte[] temp = new byte[size];
        lutMat.get(0, 0, temp);

        for(int j = 0; j < 256; ++j)
        {
            temp[j] = (byte)(Math.pow((double)j/255.0, invGamma)* 255.0);
        }
        lutMat.put(0, 0, temp);
        Core.LUT(srcMat, lutMat, dstMat);
    }


    private void getColor(){

    }


    public void save(Bitmap bitmapImage) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(createFile());
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap load() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(createFile());
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void saveImage(Bitmap finalBitmap) {

//        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/saved_images");
//        myDir.mkdirs();
//        Random generator = new Random();
//        int n = 10000;
//        n = generator.nextInt(n);
//        String fname = "Image-"+ n +".jpg";
//        File file = new File (myDir, fname);
//        if (file.exists ()) file.delete ();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        String root = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
            root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        }
        File myDir = new File(root + "/hope");
        myDir.mkdirs();
        Random generator = new Random();
//        int n = 10000;
//        n = generator.nextInt(n);
//        String fname = "Image-" + n + ".png";
        String fname = "object.png";
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


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.

            MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
    }

    //    @NonNull
    private File createFile() {
        File directory;
        if(external){
            directory = getAlbumStorageDir(directoryName);
        }
        else {
            directory = context.getDir(directoryName, Context.MODE_PRIVATE);
        }
        return new File(directory, fileName);
    }

    private File getAlbumStorageDir(String albumName) {
        File file = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), albumName);
        }
        if (!file.mkdirs()) {
            Log.e("ImageSaver", "Directory not created");
        }
        return file;
    }

    //Wash room.......... with ocr
    Handler ocrTimerHandler = new Handler();
    //Ocr detecter.........
    Runnable ocrTimerRunnable = new Runnable() {
        @Override
        public void run() {
            CharacterRecognision cr = new CharacterRecognision();
            Mat ocrMat = new Mat();
//            rgba.copyTo(ocrMat);
            if(rgba != null) {
                Bitmap bitmap = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(rgba, bitmap);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                String ocrText = cr.imageOCR(bitmap, context);
                if (ocrText.contains("Male")) {
                    voiceOut(textToSpeech, "Male wash room found", context);
                    ocrTimerHandler.removeCallbacks(ocrTimerRunnable);
                } else if (ocrText.contains("Female")) {
                    voiceOut(textToSpeech, "Female wash room found", context);
                    ocrTimerHandler.removeCallbacks(ocrTimerRunnable);
                } else {
                    if (SystemClock.elapsedRealtime() / 1000 - startTime / 1000 < 10) {
                        ocrTimerHandler.postDelayed(this, 00);
                    } else {
                        voiceOut(textToSpeech, "Sorry I can't found the wash room here", context);
                        ocrTimerHandler.removeCallbacks(ocrTimerRunnable);
                    }
                }
            }
        }
    };

    //Wash room.......... with ocr
    Handler ocrNameBoardHandler = new Handler();
    //Ocr detecter.........
    Runnable ocrNameBoardRunnable = new Runnable() {
        @Override
        public void run() {
            CharacterRecognision cr = new CharacterRecognision();
            Mat ocrMat = new Mat();
//            rgba.copyTo(ocrMat);
            if(rgba != null) {
                Bitmap bitmap = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(rgba,bitmap);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                String ocrText = cr.imageOCR(bitmap, context);
                if ((ocrText.toLowerCase().contains("street")) || (ocrText.contains("STREET"))) {
                    voiceOut(textToSpeech, "Main Street found", context);
                    ocrNameBoardHandler.removeCallbacks(ocrNameBoardRunnable);
                } else {
                    if (SystemClock.elapsedRealtime() / 1000 - startTime / 1000 < 10) {
                        ocrNameBoardHandler.postDelayed(this, 00);
                    } else {
                        voiceOut(textToSpeech, "Sorry I can't found any road here", context);
                        ocrNameBoardHandler.removeCallbacks(ocrNameBoardRunnable);
                    }
                }
            }
        }
    };

    Handler objTimerHandler = new Handler();
    //Object detecter.........
    Runnable objTimerRunnable = new Runnable() {
        @Override
        public void run() {
            imageProcessing = new ImageProcessing();

            if(rgba != null) {
                Bitmap bitmap1 = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(rgba, bitmap1);
                Matrix matrix1 = new Matrix();
                matrix1.postRotate(90);
                bitmap1 = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix1, true);

                if (bitmap1 != null) {
//                    imageProcessing.saveImage(bmp, "object.png", context);
                    String strResult = imageProcessing.imageMapping(bitmap1, Imgproc.TM_CCOEFF, context);
//                    Toast.makeText(context,strResult, Toast.LENGTH_SHORT);
                    if(isObject) {
                        isObject = false;
                        voiceOut(textToSpeech, strResult, context);
                        objTimerHandler.removeCallbacks(objTimerRunnable);
                    } else {
                        if (SystemClock.elapsedRealtime() / 1000 - startTime / 1000 < 10) {
                            objTimerHandler.postDelayed(this, 00);
                        } else {
                            voiceOut(textToSpeech, "Sorry I can't found any sign here", context);
                            objTimerHandler.removeCallbacks(objTimerRunnable);
                        }
                    }
                }
            }
            else
            if(isObject) {
                isObject = false;
                voiceOut(textToSpeech, "Sorry, Try again", context);
            }
        }
    };

    //currency................
    Handler currTimerHandler = new Handler();
    //Ocr detecter.........
    Runnable currTimerRunnable = new Runnable() {
        @Override
        public void run() {
            CharacterRecognision cr = new CharacterRecognision();
//            Mat crrMat = new Mat();
//            rgba.copyTo(ocrMat);
            Bitmap bmp = null;
            Mat tmp = new Mat (400, 200, CvType.CV_8U, new Scalar(4));
            if(rgba != null) {
                bmp = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(tmp, bmp);
                Bitmap bitmap1 = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap (rgba, bitmap1);
                Matrix matrix1 = new Matrix();
                Bitmap bitmap2 = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(rgba, bitmap2);
                Matrix matrix2 = new Matrix();
                matrix1.postRotate(90);
                matrix2.postRotate(270);
                bitmap1 = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix1, true);
                bitmap2 = Bitmap.createBitmap(bitmap2, 0, 0, bitmap2.getWidth(), bitmap2.getHeight(), matrix2, true);
                String crrText1 = cr.imageOCR(bitmap1, context);
                String crrText2 = cr.imageOCR(bitmap2, context);
                if (((crrText1.contains("£10")) || (crrText1.contains("E10")) || (crrText1.contains("Ten")))  || ((crrText2.contains("£10")) || (crrText2.contains("E10")) ||(crrText2.contains("Ten")))){
                    voiceOut(textToSpeech, "It is a Ten pounds note ", context);
                    currTimerHandler.removeCallbacks(this);
                } else if (((crrText1.contains("£20")) || (crrText1.contains("E20")) || (crrText1.contains("TWENTY"))) || ((crrText2.contains("£20")) || (crrText2.contains("E20")) || (crrText2.contains("TWENTY")))) {
                    voiceOut(textToSpeech, "This is a Twenty pounds note ", context);
                    currTimerHandler.removeCallbacks(this);
                } else if (((crrText1.contains("£50")) || (crrText1.contains("E50")) || (crrText1.contains("FIFTY"))) || (((crrText2.contains("£50")) || (crrText2.contains("E50")) || (crrText2.contains("FIFTY"))))){
                    voiceOut(textToSpeech, "It is a hundred pounds note ", context);
                    currTimerHandler.removeCallbacks(this);
                } else {
                    if (SystemClock.elapsedRealtime() / 1000 - startTime / 1000 < 10) {
                        currTimerHandler.postDelayed(this, 00);
                    } else {
                        voiceOut(textToSpeech, "Sorry I can't identify the currency here", context);
                        currTimerHandler.removeCallbacks(this);
                    }
                }
            }
        }
    };

}
