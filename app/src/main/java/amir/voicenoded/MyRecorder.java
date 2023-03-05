package amir.voicenoded;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyRecorder {

    MainActivity mainActivity = new MainActivity();

    Context context;

    private MediaRecorder mediaRecorder;
    private String recordPath;
    private String recordFile;




    Chronometer timer;


    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    DateFormat df = new SimpleDateFormat("HH:mm",Locale.getDefault());

    public void startRecording(){
        System.out.println("                               111111112");
        MainActivity.timerSetBase(timer);
        System.out.println("                               222222222");
        MainActivity.timerStart(timer);
        System.out.println("                               3333333333");

        recordPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        System.out.println("                               444444444444");

        String date = format.format(new Date());

        String time = df.format(Calendar.getInstance().getTime());

        recordFile = "REC_" + time + "_" + date + ".3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }




    public void stopRecording(){
        MainActivity.timerStop(timer);
        String isDuration = MainActivity.timerGetTime(timer);
        MainActivity.timerSetBase(timer);

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        LayoutInflater li = LayoutInflater.from(context);
        View saveView = li.inflate(R.layout.layout_save_dailog, null);

        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);

        mDialogBuilder.setView(saveView);

        final EditText userInput = (EditText) saveView.findViewById(R.id.et_save_name);
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Сохранить",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if (userInput.getText().toString().matches("")) {
                                    userInput.setError("Введите название");
                                } else {
                                    String title = userInput.getText().toString();
                                    String path = recordPath + "/" + recordFile;
                                    String date = format.format(new Date());
                                    String time = df.format(Calendar.getInstance().getTime());
                                    String duration = isDuration;

                                    mainActivity.doInsert(title,  path, date, time, duration);
                                }
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();

    }



//    public boolean checkPermissions(){
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
//            ActivityCompat.requestPermissions(this, permissions,0)
//    }

}
