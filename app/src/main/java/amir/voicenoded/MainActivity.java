package amir.voicenoded;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
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

    public static Chronometer mtimer;

    private MyRecorder myRecoder;

    private boolean isRecording = false;

    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private String readPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private String writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private String managePermission = Manifest.permission.MANAGE_EXTERNAL_STORAGE;
    private int PERMISSION_CODE = 21;

//    private AsyncRecord asyncRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    public void init(){
        tvList = findViewById(R.id.tv_list);
        fabRecord = findViewById(R.id.fab_record);
        mtimer = findViewById(R.id.c_chronometer);

//        recycle view setting
        recordsList = findViewById(R.id.rv_records_list);
        LinearLayoutManager myLLM = new LinearLayoutManager(this);
        recordsList.setLayoutManager(myLLM);

        myRecoder = new MyRecorder();

//        Database helper
        myDbHelper = new MyDbHelper(this);

//          connect Adapter
        myAdapter = new MyAdapter(this);
        recordsList.setAdapter(myAdapter);

//        asyncRecord = new AsyncRecord();
    }

    static class AsyncRecord extends AsyncTask<Void, Void, Void>{

        public AsyncRecord() {
        }

        @Override
        protected Void doInBackground(Void... voids) {



            return null;
        }
    }

    public void onRecordClick(View view){
//        asyncRecord.execute();
        if (isRecording) {

//                fabRecord.setBackgroundColor(getResources().getColor(R.color.play_pause_button));
                fabRecord.setBackgroundColor(ContextCompat.getColor(this, R.color.play_pause_button));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myRecoder.stopRecording();
            isRecording = false;
        } else {
            if (checkPermission()) {
                fabRecord.setBackgroundColor(ContextCompat.getColor(this, R.color.play_pause_off));
                System.out.println("                               000000000000000");
                myRecoder.startRecording();
                System.out.println("                               110010110101010");
//                    fabRecord.setBackgroundColor(getResources().getColor(R.color.play_pause_off));

                isRecording = true;
            }
        }
    }

    public void doInsert(String title, String path, String date, String time, String duration){
        myDbHelper.insertToDb(title,  path, date, time, duration);
    }

    public boolean checkPermission(){
//        private String recordPermission = Manifest.permission.RECORD_AUDIO;
//        private int PERMISSION_CODE = 21;
        if (ActivityCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, readPermission) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, writePermission) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, managePermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission, readPermission, writePermission, managePermission}, PERMISSION_CODE);

//            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE, ) &&
//            ActivityCompat.requestPermissions(this, new String[]{readPermission}, PERMISSION_CODE) &&
//            ActivityCompat.requestPermissions(this, new String[]{writePermission}, PERMISSION_CODE) &&
//            ActivityCompat.requestPermissions(this, new String[]{managePermission}, PERMISSION_CODE);
            return false;
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

//    public static void timerStart(Chronometer t){
//        t.start();
//    }
//    public static void timerStop(Chronometer t){
//        t.stop();
//    }
//    public static void timerSetBase(Chronometer t){
//        t.setBase((long)SystemClock.elapsedRealtime());
//
//    }
//    public static String timerGetTime(Chronometer t){
//        return t.getText().toString();
//    }
}