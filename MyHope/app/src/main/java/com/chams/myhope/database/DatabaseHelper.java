package com.chams.myhope.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by idcs on 6/16/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db_hope";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_IMG_DATASERVER = "img_dataserver";
    public static final String TABLE_VOICE_COMMANDS = "voice_commands";
    public static final String TABLE_LOCATIONS = "location_definition";
    public static final String TABLE_LOCATION_TYPES = "location_type";
    public static final String TABLE_OBJ_TYPES = "obj_type";
    public static final String TABLE_COLORS = "color_codes";

    //Common Table column names...........................
    public static final String KEY_ID = "_id";

    //Location_definitions Table column names...........................
    public static final String KEY_LOC_NAME = "loc_name";
    public static final String KEY_LOC_VOICE_ID = "voice_id";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON = "lon";
    public static final String KEY_LOC_TYPE_ID = "loc_type_id";

    //voice commands table.................................
    public static final String KEY_VOICE_TEXT = "voice_com_text";
    public static final String KEY_VOICE_LANGUAGE = "voice_language";
    public static final String KEY_VOICE_PATH = "voice_path";

    //color_codes table.............................
    public static final String KEY_RVAL_MIN = "r_val_min";
    public static final String KEY_RVAL_MAX = "r_val_max";
    public static final String KEY_GVAL_MIN = "g_val_min";
    public static final String KEY_GVAL_MAX = "g_val_max";
    public static final String KEY_BVAL_MIN = "b_val_min";
    public static final String KEY_BVAL_MAX = "b_val_max";
    public static final String KEY_COLOR_NAME = "color_name";


    //Table creation statement...........................
    // location_definition table create statement........................
    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATIONS + " ("
        + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_LOC_NAME + " TEXT, " +  KEY_LOC_VOICE_ID + " INTEGER, "
        + KEY_LAT + " TEXT, " + KEY_LON + " TEXT, " + KEY_LOC_TYPE_ID + " INTEGER " + ")";

    // colors table create statement for widget........................
    private static final String CREATE_TABLE_COLORS = "CREATE TABLE " + TABLE_COLORS
            + "("+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_RVAL_MIN + " TEXT, " +  KEY_RVAL_MAX + " TEXT, "
            +  KEY_GVAL_MIN + " TEXT, " +  KEY_GVAL_MAX + " TEXT, "
            +  KEY_BVAL_MIN + " TEXT, " +  KEY_BVAL_MAX + " TEXT, " +  KEY_COLOR_NAME + " TEXT" + ")";

    // voice_commands table create statement........................
    private static final String CREATE_TABLE_VOICE = "CREATE TABLE " + TABLE_VOICE_COMMANDS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_VOICE_TEXT + " TEXT, "
            +  KEY_VOICE_LANGUAGE + " TEXT, " +  KEY_VOICE_PATH + " TEXT" + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(null, "Database Created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables........................
        db.execSQL(CREATE_TABLE_LOCATIONS);
        db.execSQL(CREATE_TABLE_COLORS);
        db.execSQL(CREATE_TABLE_VOICE);

//        insertLocations(db,);
//        insertColors(db);
//        insertVoice(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOICE_COMMANDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_TYPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMG_DATASERVER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBJ_TYPES);
        // create new tables
        onCreate(db);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


//    // Insert one raw to module table...............
//    public long createModule(Modules modules) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_SIM_NO, modules.getSimNo());
//        values.put(KEY_PASSWORD, modules.getPassword());
//        values.put(KEY_ON_TIME, modules.getOnTime());
//
//        // insert row
//        long module_id = db.insert(TABLE_MODULE, null, values);
//
//        // assigning tags to todo
////        for (long tag_id : tag_ids) {
////            createModule(modules,module_id);
////        }module_id
//
//        return module_id;
//    }

    //Insert first raw locations table.................
    public void insertLocations(SQLiteDatabase db, String mLocName, int mLocVoiceId, String mLat, String mLon,int mLocTypeId) {
//        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOC_NAME, mLocName);
        values.put(KEY_LOC_VOICE_ID, mLocVoiceId);
        values.put(KEY_LAT, mLat);
        values.put(KEY_LON, mLon);
        values.put(KEY_LOC_TYPE_ID, mLocTypeId);
        // insert row
        db.insert(TABLE_LOCATIONS, null, values);
    }

    //Insert first raw colors table.................
    public void insertColors(SQLiteDatabase db, int mRvalMin, int mRvalMax, int mGvalMin, int mGvalMax, int mBvalMin, int mBvalMax, String mColorName) {
//        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RVAL_MIN, mRvalMin);
        values.put(KEY_RVAL_MAX, mRvalMax);
        values.put(KEY_GVAL_MIN, mGvalMin);
        values.put(KEY_GVAL_MAX, mGvalMax);
        values.put(KEY_BVAL_MIN, mBvalMin);
        values.put(KEY_BVAL_MAX, mBvalMax);
        values.put(KEY_COLOR_NAME, mColorName);
        // insert row
        db.insert(TABLE_COLORS, null, values);
    }

    //Insert first raw voice table.................
    public void insertVoice(SQLiteDatabase db, String mVoiceText, String  mVoiceLang, String mVoicePath) {
//        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VOICE_TEXT, mVoiceText);
        values.put(KEY_VOICE_LANGUAGE, mVoiceLang);
        values.put(KEY_VOICE_PATH, mVoicePath);
        // insert row
        db.insert(TABLE_COLORS, null, values);
    }

//    //Get module data...........................
//    public Modules getSimData() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String selectQuery = "SELECT * FROM " + TABLE_MODULE + " ORDER BY " + KEY_ID + " DESC"; // " WHERE "
////                + KEY_ID + " = " + module_id;
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        if (c != null)
//            c.moveToFirst();
//
//        Modules modules = new Modules();
//        modules.setId(c.getInt(c.getColumnIndex(KEY_ID)));
//        if (c.getString(c.getColumnIndex(KEY_DEVICE_NAME)) != null) modules.setDeviceName(c.getString(c.getColumnIndex(KEY_DEVICE_NAME)));
//        if (c.getString(c.getColumnIndex(KEY_SIM_NO)) != null) modules.setSimNo(c.getString(c.getColumnIndex(KEY_SIM_NO)));
//        if (c.getString(c.getColumnIndex(KEY_PASSWORD)) != null) modules.setPassword(c.getString(c.getColumnIndex(KEY_PASSWORD)));
//        modules.setOnTime(c.getInt(c.getColumnIndex(KEY_ON_TIME)));
//        modules.setNotify(c.getInt(c.getColumnIndex(KEY_NOTIFICATION)));
//        if (c.getString(c.getColumnIndex(KEY_SIGNAL_STRENGTH)) != null) modules.setSignal(c.getString(c.getColumnIndex(KEY_SIGNAL_STRENGTH)));
//        if (c.getString(c.getColumnIndex(KEY_IMEI)) != null) modules.setImei(c.getString(c.getColumnIndex(KEY_IMEI)));
////        modules.setTagOn(c.getInt(c.getColumnIndex(KEY_TAG_ON)));
////        modules.setTagOff(c.getInt(c.getColumnIndex(KEY_TAG_OFF)));
//
//        return modules;
//    }
//
//    //Get module data for widget...........................
//    public ModulesWd getSimDataWd() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String selectQuery = "SELECT  * FROM " + TABLE_MODULE_WD + " ORDER BY " + KEY_ID + " DESC"; // " WHERE " + KEY_WD_ID + " = " + mWidgetId;
//        Cursor c = db.rawQuery(selectQuery, null);
//        if (c != null)
//            c.moveToFirst();
//
//        ModulesWd modulesWd = new ModulesWd();
//        modulesWd.setId(c.getInt(c.getColumnIndex(KEY_ID)));
//        if (c.getString(c.getColumnIndex(KEY_DEVICE_NAME)) != null) modulesWd.setDeviceName(c.getString(c.getColumnIndex(KEY_DEVICE_NAME)));
//        if (c.getString(c.getColumnIndex(KEY_SIM_NO)) != null) modulesWd.setSimNo(c.getString(c.getColumnIndex(KEY_SIM_NO)));
//        return modulesWd;
//    }
//
//    public ModulesWd getSimDataWd(String mClickedId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String selectQuery = "SELECT  * FROM " + TABLE_MODULE_WD + " WHERE " + KEY_WD_ID + " = '" + mClickedId + "'"; // " WHERE " + KEY_WD_ID + " = " + mWidgetId;
//        Cursor c = db.rawQuery(selectQuery, null);
//        if (c != null)
//            c.moveToFirst();
//
//        ModulesWd modulesWd = new ModulesWd();
//        modulesWd.setId(c.getInt(c.getColumnIndex(KEY_ID)));
//        if (c.getString(c.getColumnIndex(KEY_DEVICE_NAME)) != null) modulesWd.setDeviceName(c.getString(c.getColumnIndex(KEY_DEVICE_NAME)));
//        if (c.getString(c.getColumnIndex(KEY_SIM_NO)) != null) modulesWd.setSimNo(c.getString(c.getColumnIndex(KEY_SIM_NO)));
//        if (c.getString(c.getColumnIndex(KEY_COLOR_CODE)) != null) modulesWd.setColorCode(c.getString(c.getColumnIndex(KEY_COLOR_CODE)));
//        return modulesWd;
//    }
//
//    public ModulesWd getWidgetData(String mWidgetName) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String selectQuery = "SELECT  * FROM " + TABLE_MODULE_WD + " WHERE " + KEY_DEVICE_NAME + " = '" + mWidgetName + "'";
//        Cursor c = db.rawQuery(selectQuery, null);
//        if (c != null)
//            c.moveToFirst();
//
//        ModulesWd modulesWd = new ModulesWd();
//        modulesWd.setId(c.getInt(c.getColumnIndex(KEY_ID)));
//        if (c.getString(c.getColumnIndex(KEY_DEVICE_NAME)) != null) modulesWd.setDeviceName(c.getString(c.getColumnIndex(KEY_DEVICE_NAME)));
//        if (c.getString(c.getColumnIndex(KEY_SIM_NO)) != null) modulesWd.setSimNo(c.getString(c.getColumnIndex(KEY_SIM_NO)));
//        modulesWd.setWdId(c.getInt(c.getColumnIndex(KEY_WD_ID)));
//        return modulesWd;
//    }

    //Get phone number list from database...........................
//    public List<String> getAllPhoneNumbers() {
//        List<String> numberList = new ArrayList<String>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        String selectQuery = "SELECT  * FROM " + TABLE_PHONE_NUMBERS + " ORDER BY " + KEY_ID + " ASC";
//
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // go through all rows and adding to list
//        if (cursor.moveToFirst()) {
////            do {
//                PhoneNumbers phoneNumbers = new PhoneNumbers();
//                phoneNumbers.setId(cursor.getInt(0));
//                phoneNumbers.setPhoneNumber(cursor.getString(1));
//
//                // Adding phone number to list
//                numberList.add(cursor.getString(1));
////            } while (cursor.moveToNext());
//        }
//        return numberList;
//    }



//    public NfcTags getAllNfcTags() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String selectQuery = "SELECT  * FROM " + TABLE_NFC_TAGS + " ORDER BY " + KEY_TAG_DEVICE_ID + " ASC";
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        if (c != null) c.moveToFirst();
//        NfcTags nfcTags = new NfcTags();
//        nfcTags.setId(c.getInt(c.getColumnIndex(KEY_ID)));
//        nfcTags.setTagItem(c.getInt(c.getColumnIndex(KEY_TAG_DEVICE_ID)));
//        if (c.getString(c.getColumnIndex(KEY_TAG_COMMAND)) != null) nfcTags.setTagCommand(c.getString(c.getColumnIndex(KEY_TAG_COMMAND)));
//        return nfcTags;
//    }

//    public Cursor fetchAllNfcTags() {
//        SQLiteDatabase db = this.getReadableDatabase();
////        String[] columns = new String[] { "nfc_tags._id", DatabaseHelper.TABLE_MODULE+"."+DatabaseHelper.KEY_DEVICE_NAME, DatabaseHelper.TABLE_NFC_TAGS+"."+DatabaseHelper.KEY_TAG_COMMAND };
////        Cursor cursor =  db.query(DatabaseHelper.TABLE_NFC_TAGS
////                + " FULL OUTER JOIN "+DatabaseHelper.TABLE_MODULE+" ON "+DatabaseHelper.TABLE_NFC_TAGS+"._id="+DatabaseHelper.TABLE_MODULE+"._id", columns, null, null, null, null, null);
//
//        Cursor cursor = db.rawQuery("SELECT _id, " + DatabaseHelper.KEY_TAG_DEVICE_NAME + ", " + DatabaseHelper.KEY_TAG_COMMAND + ", " + DatabaseHelper.KEY_TAG_SERIAL
//            + " FROM " + DatabaseHelper.TABLE_NFC_TAGS, null);
//
////        Cursor cursor = db.rawQuery("SELECT _id, " + KEY_DEVICE_NAME + ", " + KEY_TAG_ON + ", " + KEY_TAG_OFF
////                + " FROM " + DatabaseHelper.TABLE_MODULE ,null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//        }
//        return cursor;
//    }

//    //Get module data...........................
//    public NfcTags getNfcBySerial(String mSerial) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String selectQuery = "SELECT " + DatabaseHelper.KEY_TAG_DEVICE_ID + ", " + DatabaseHelper.KEY_TAG_DEVICE_NAME + ", " + DatabaseHelper.KEY_TAG_COMMAND + ", " + DatabaseHelper.KEY_TAG_SERIAL
//                + " FROM " + DatabaseHelper.TABLE_NFC_TAGS + " WHERE " + DatabaseHelper.KEY_TAG_SERIAL + " = '" + mSerial + "'";
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        if (c != null)
//            c.moveToFirst();
//
//        NfcTags nfcTags = new NfcTags();
//        if (c.getCount() > 0) {
//            nfcTags.setId(c.getInt(c.getColumnIndex(KEY_TAG_DEVICE_ID)));
//            nfcTags.setDeviceName(c.getString(c.getColumnIndex(KEY_TAG_DEVICE_NAME)));
//            nfcTags.setTagCommand(c.getString(c.getColumnIndex(KEY_TAG_COMMAND)));
//            nfcTags.setSerial(c.getString(c.getColumnIndex(KEY_TAG_SERIAL)));
//        } else {
//            nfcTags.setId(0);
//            nfcTags.setDeviceName(null);
//            nfcTags.setTagCommand(null);
//            nfcTags.setSerial(null);
//        }
//        return nfcTags;
//    }

//    public void deleteNfcTag(long _id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(DatabaseHelper.TABLE_NFC_TAGS, DatabaseHelper.KEY_ID + "=" + _id, null);
//    }

//    //Get count...........................
//    public int getDataCount() {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        String selectQuery = "SELECT COUNT("+KEY_ID+") FROM " + TABLE_MODULE;
//        Cursor c = db.rawQuery(selectQuery, null);
//
////        if (c != null)
////            c.moveToFirst();
//
//        int count = c.getCount();
//
////        Modules modules = new Modules();
////        modules.setId(c.getInt(c.getColumnIndex(KEY_ID)));
//
//        return count;
//    }

    public String getLocation(String mLat, String mLon) {
        String location = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT "+KEY_LOC_NAME+" FROM " + TABLE_LOCATIONS + " WHERE "+KEY_LAT+" = '"+mLat+"' AND " + KEY_LON + " = '"+mLon+"' ORDER BY " + KEY_ID + " DESC";
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) c.moveToFirst();
        if (c.getString(c.getColumnIndex(KEY_LOC_NAME)) != null) location = c.getString(c.getColumnIndex(KEY_LOC_NAME)); // phoneNumbers.setPhoneNumber(c.getString(c.getColumnIndex(KEY_PHONE_NUMBERS)));
        return location;
    }



//    public Modules getId() {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        String selectQuery = "SELECT id FROM " + TABLE_MODULE + " WHERE "+ KEY_SIM_NO +" = ''"; // " WHERE "
////                + KEY_ID + " = " + module_id;
//
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        if (c != null)
//            c.moveToFirst();
//
//        Modules modules = new Modules();
//
//        modules.setSimNo(c.getString(c.getColumnIndex(KEY_SIM_NO)));
//        modules.setPassword(c.getString(c.getColumnIndex(KEY_PASSWORD)));
//
//        return modules;
//    }

//    //Update module data...........................
//    public int updateModuleSimNo(String newSimNo, int id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_SIM_NO, newSimNo);
//
//        // updating row
//        return db.update(TABLE_MODULE, values, KEY_ID + " = ?",
//                new String[] { String.valueOf(id) });
//    }


//    //Update app password................
//    public int updateAppPassword(String newPassword, int id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(KEY_APP_PASSWORD, newPassword);
//        // updating row
//        return db.update(TABLE_APP, values, KEY_ID + " >= ?", new String[] { String.valueOf(id) });
//    }

}
