package amir.voicenoded;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import amir.voicenoded.Database.MyDbHelper;

public class MainActivity extends AppCompatActivity {

    private TextView tvList;
    private FloatingActionButton fabRecord;
    private RecyclerView recordsList;

    private MyDbHelper myDbHelper;

    private MyAdapter myAdapter;

    private Chronometer timer;

    private MyRecorder myRecoder;

    private boolean isRecording = false;

    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    public void init(){
        tvList = findViewById(R.id.tv_list);
        fabRecord = findViewById(R.id.fab_record);
        timer = findViewById(R.id.c_chronometer);

//        recycle view setting
        recordsList = findViewById(R.id.rv_records_list);
        LinearLayoutManager myLLM = new LinearLayoutManager(this);
        recordsList.setLayoutManager(myLLM);

//        Database helper
        myDbHelper = new MyDbHelper(this);

//          connect Adapter
        myAdapter = new MyAdapter(this);
        recordsList.setAdapter(myAdapter);
    }

//    class AsyncRecord extends AsyncTask<Void, Void, Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            onRecordClick();
//
//            return null;
//        }
//    }

    public void onRecordClick(View view){
        if (isRecording) {

            fabRecord.setBackgroundColor(getResources().getColor(R.color.play_pause_button));
            myRecoder.stopRecording();
            isRecording = false;
        } else {
            if (checkPermission()) {
                System.out.println("                               000000000000000");
                myRecoder.startRecording();
                System.out.println("                               110010110101010");
                fabRecord.setBackgroundColor(getResources().getColor(R.color.play_pause_off));
                isRecording = true;
            }
        }
    }

    public void doInsert(String title, String path, String date, String time, String duration){
        myDbHelper.insertToDb(title,  path, date, time, duration);
    }

    public boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myDbHelper.openDb();
        myAdapter.updateAdapter(myDbHelper.getListRecords());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            myRecoder.stopRecording();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDbHelper.closeDb();
    }

    public static void timerStart(Chronometer t){
        t.start();
    }
    public static void timerStop(Chronometer t){
        t.stop();
    }
    public static void timerSetBase(Chronometer t){
        t.setBase(SystemClock.elapsedRealtime());
    }
    public static String timerGetTime(Chronometer t){
        return t.getText().toString();
    }
}