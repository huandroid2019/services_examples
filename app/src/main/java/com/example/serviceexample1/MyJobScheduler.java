package com.example.serviceexample1;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;

public class MyJobScheduler extends JobService {
    public MyJobScheduler() {
    }
    JobParameters parameters;
    MyTask task;
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.parameters = jobParameters;
        doWork();
        return true;//false - означает, что работа уже выполнена
    }
    void doWork(){

        task = new MyTask(new WeakReference<MyJobScheduler>(this));

    }
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        task.cancel(true);
        return false;
    }

    public static class MyTask extends AsyncTask<Void, Void, Void>{
        public MyTask(WeakReference<MyJobScheduler> reference) {
            this.reference = reference;
        }

        WeakReference<MyJobScheduler> reference;
        @Override
        protected Void doInBackground(Void... voids) {

            if(reference.get() == null){
                return null;
            }
            fileWriter(reference.get());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(reference.get()!=null) {
                Toast.makeText(reference.get(), "job service to its work!", Toast.LENGTH_SHORT).show();
                reference.get().jobFinished(reference.get().parameters,false);
            }
        }
    }
    static void fileWriter(Context context){
        File sd = context.getExternalFilesDir(null);
        Log.d("file",sd.getAbsolutePath());
        File dir = new File(sd+"/my_dir");
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dir+"/log.txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try {

            FileWriter fileWriter = new FileWriter(file,true);
            fileWriter.write("\nsheduler launched:"+ Calendar.getInstance().getTime().toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
