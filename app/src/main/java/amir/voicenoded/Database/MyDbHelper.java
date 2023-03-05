package amir.voicenoded.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyDbHelper extends SQLiteOpenHelper {

        public static final String TABLE_NAME = "records_table";
        public static final String _ID = "_id";
        public static final String TITLE = "title";
        public static final String PATH = "path";
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String DURATION = "duration";

        public static final String DB_NAME = "my_db.db";
        public static final int DB_VERSION = 1;

        public static final String TABLE_STRUCTURE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + TITLE + " TEXT," +
                PATH + " TEXT," + DATE + " TEXT," + TIME + " TEXT," + DURATION + " TEXT)";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;


    public MyDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_STRUCTURE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void openDb(){
        getWritableDatabase();
    }

    public void closeDb(){
        close();
    }

    public void insertToDb(String title, String path, String date, String time, String duration){
        ContentValues cv = new ContentValues();
        cv.put(TITLE, title);
        cv.put(PATH, path);
        cv.put(DATE, date);
        cv.put(TIME, time);
        cv.put(DURATION, duration);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, cv);
    }

    void deleteContact(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, _ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<Record> getListRecords(){
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        List<Record> listRecord = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do {
                String id = cursor.getString(0);
                String title = cursor.getString(1);
                String path = cursor.getString(2);
                String date = cursor.getString(3);
                String time = cursor.getString(4);
                String duration = cursor.getString(5);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listRecord;
    }


}
