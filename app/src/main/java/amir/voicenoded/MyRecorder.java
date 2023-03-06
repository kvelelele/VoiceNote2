package amir.voicenoded;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import amir.voicenoded.Database.MyDbHelper;

public class MyRecorder {


    Context context;

    private MediaRecorder mediaRecorder;
    private String recordPath;
    private String recordFile;

    private MyDbHelper myDbHelper = new MyDbHelper(context);


    Chronometer timer;
//            = MainActivity.mtimer;


    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    DateFormat df = new SimpleDateFormat("HH:mm",Locale.getDefault());



    public void startRecording(){
        System.out.println("                               111111111");
        MainActivity.mtimer.setBase(SystemClock.elapsedRealtime());
        System.out.println("                               222222222");
        MainActivity.mtimer.start();
        System.out.println("                               3333333333");

        recordPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        System.out.println("                               RECORD PATH " + recordPath);

        System.out.println("                               444444444444");

        String date = format.format(new Date());

        String time = df.format(Calendar.getInstance().getTime());

        recordFile = "REC" + time + "." + date + ".3gp";
        System.out.println("                               RECORD FILE   " + recordFile);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // add insert to db

        try {
            System.out.println("                               55555555555");
            mediaRecorder.prepare();

            System.out.println("                               66666666");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("                               77777777");
            Thread.sleep(5000);
            System.out.println("                               88888888888");
            mediaRecorder.start();
            System.out.println("                               999999999");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("                               999999999");

        System.out.println("                               AAAAAAAAAA");
    }




    public void stopRecording(){
        MainActivity.mtimer.stop();
        String isDuration = MainActivity.mtimer.getText().toString();
        MainActivity.mtimer.setBase(SystemClock.elapsedRealtime());

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        LayoutInflater li = LayoutInflater.from(context);
        View saveView = li.inflate(R.layout.layout_save_dailog, null);

        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);

        mDialogBuilder.setView(saveView);

        final EditText userInput = saveView.findViewById(R.id.et_save_name);
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
//                                    String duration = isDuration;

                                    String duration = "dur";
                                    myDbHelper.insertToDb(title, path, date, time, duration);
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

}
