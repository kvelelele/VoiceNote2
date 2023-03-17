package amir.voicenoded;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import amir.voicenoded.Database.MyDbHelper;
import amir.voicenoded.Database.Record;

public class MainActivity extends AppCompatActivity {

    Context context;

    private TextView tvList;
    private FloatingActionButton fabRecord;
    private RecyclerView recordsList;
    private Chronometer timer;

    private MyDbHelper myDbHelper;

    private MyAdapter myAdapter;

    private MediaRecorder mediaRecorder;
    private String recordPath;
    private String recordFile;

    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    DateFormat df = new SimpleDateFormat("HH:mm",Locale.getDefault());

    private boolean isRecording = false;

    AsyncRecord asyncRecord;


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
//        timer.setVisibility(View.INVISIBLE); // WHITE in xml

//        recycle view setting
        recordsList = findViewById(R.id.rv_records_list);
        LinearLayoutManager myLLM = new LinearLayoutManager(this);
        recordsList.setLayoutManager(myLLM);

//        Database helper
        myDbHelper = new MyDbHelper(this);
        List<Record> listRec = myDbHelper.getListRecords();
        if(listRec.size() > 0) {
            recordsList.setVisibility(View.VISIBLE);
            //          connect Adapter
            myAdapter = new MyAdapter(this, listRec);
            recordsList.setAdapter(myAdapter);
        } else {
            recordsList.setVisibility(View.GONE);
            Toast.makeText(this, "There is no contact in the database. Start adding now", Toast.LENGTH_LONG).show();
        }




    }

    class AsyncRecord extends AsyncTask <Void, Void, Void> {

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            if(isRecording)
                fabRecord.setBackgroundColor(getResources().getColor(R.color.play_pause_button));
            else
                fabRecord.setBackgroundColor(getResources().getColor(R.color.play_pause_off));

        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (isRecording) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopRecording();
                publishProgress();
                isRecording = false;
            } else {
                if (checkPermission()) {
                    startRecording();
                    publishProgress();
                    isRecording = true;
                }
            }

            return null;
        }
    }

    public void onRecordClick(View view){
        asyncRecord = new AsyncRecord();
        asyncRecord.execute();

    }

    public void startRecording(){
        recordPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        String date = format.format(new Date());
        String time = df.format(Calendar.getInstance().getTime());

        recordFile = "REC_" + time + "_" + date + ".3gpp";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // add insert to db

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }


    public void stopRecording(){

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;


        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setTitle("Заметка создана")
                .setCancelable(false);

        LayoutInflater li = LayoutInflater.from(this);
        View saveView = li.inflate(R.layout.layout_save_dailog, null);
        mDialogBuilder.setView(saveView);

        final EditText userInput = saveView.findViewById(R.id.et_save_name);
        mDialogBuilder
//                .setCancelable(false)
                .setPositiveButton("Сохранить",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if (userInput.getText().toString().matches("")) {
                                    userInput.setError("Введите название");
                                } else {

                                    String title = userInput.getText().toString();
                                    String path = recordPath + "/" + recordFile;
                                    String date = format.format(new Date());
                                    String time = "в" + df.format(Calendar.getInstance().getTime());
                                    String duration = "dur";

                                    Record newRec = new Record(title, path, date, time, duration);

                                    myDbHelper.insertToDb(newRec);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            myAdapter.updateAdapter(myDbHelper.getListRecords());

                                        }
                                    });


                                }
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
            }
        });

    }


    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private String readPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private String writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private String managePermission = Manifest.permission.MANAGE_EXTERNAL_STORAGE;
    private int PERMISSION_CODE = 21;

    public boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, readPermission) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, writePermission) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, managePermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission, readPermission, writePermission, managePermission}, PERMISSION_CODE);

            return true;
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                myAdapter.updateAdapter(myDbHelper.getListRecords());

            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            stopRecording();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myDbHelper != null)
            myDbHelper.closeDb();
    }

}