package com.swalla.campusdock.Utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.swalla.campusdock.listeners.OnFileDownloadCompleteListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.swalla.campusdock.Utils.Config.BASE_CLASS_URL;

/**
 * Created by ogil on 03/03/18.
 */

public class DownloadFileFromURL extends AsyncTask<String, String, String> {
    private OnFileDownloadCompleteListener listener;
    private File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "CampusDock");
    private ProgressBar progressBar;

    public DownloadFileFromURL(ProgressBar progressBar, OnFileDownloadCompleteListener listener){
        this.progressBar = progressBar;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        if(progressBar!=null){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
        }
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            String fileUrl = BASE_CLASS_URL + f_url[0];

            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();
            connection.connect();

            InputStream input = new BufferedInputStream(url.openStream(), 8192); //8k buffer

            if(!folder.exists()){
                folder.mkdirs();
            }
            folder = new File(folder, f_url[0]);
            if(folder.exists()){
                folder.delete();
            }

            OutputStream output = new FileOutputStream(folder);

            byte data[] = new byte[1024];

            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String file_url) {
        if(progressBar!=null){
            progressBar.setVisibility(View.GONE);
        }
        listener.OnFileDownloadComplete();
    }

    public static boolean fileExists(String file){
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "CampusDock");
        folder = new File(folder, file);
        if(folder.exists())
            return true;
        else
            return false;
    }
}
