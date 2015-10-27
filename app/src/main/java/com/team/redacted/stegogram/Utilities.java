package com.team.redacted.stegogram;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Shawn on 10/16/2015.
 */
public class Utilities {
    public static int DECODE_IMAGE = 0, ENCODE_IMAGE = 1;
    static int index = 0, prog_val_encode [] = {10, 40, 40, 10}, prog_val_decode[] = {40, 40, 20};
    public final static String status_encode [] = {
      "Converting Image", "Encrypting Data", "Encoding Data", "Sending Image"
    }, status_decode [] = {
          "Decoding Image", "Decrypting Data", "Finishing"
    };

    public static String createStegogramRequest(final Activity a, final String path_name, String password, String message, final int type){
        final ProgressDialog fragment = new ProgressDialog();
        fragment.show(a.getFragmentManager(), "ProgressBar");
        View v = fragment.getView();
        final ProgressBar prog_bar = (ProgressBar) v.findViewById(R.id.progress_bar);
        final TextView prog_status = (TextView)v.findViewById(R.id.progress_text);
        prog_bar.setMax(100);
        prog_bar.setProgress(0);
        new AsyncTask<String, Void, Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                index = 0;
            }

            @Override
            protected Void doInBackground(String... params) {
                // **Code**
                if(type == DECODE_IMAGE){
                    publishProgress();
                }
                else if(type == ENCODE_IMAGE){
                    publishProgress();
                    String png_path_name = convertJPEGToPNG(path_name);
                    if(png_path_name != null){
                        String encoded_path = null;
                        publishProgress();
                        /*Call Encryption*/
                        SystemClock.sleep(5000);
                        publishProgress();
                        /*Call Encoding encoded_path = func()*/
                        SystemClock.sleep(5000);
                        publishProgress();
                        /*Send Image*/
                        SystemClock.sleep(5000);
                        sendPictureMessage(encoded_path);
                    }
                    else{
                        Toast.makeText(a,"Failed converting image", Toast.LENGTH_SHORT);
                    }
                }
                return null;
            }
            protected void onProgressUpdate(Integer... progress) {
                if(type == ENCODE_IMAGE) {
                    prog_bar.incrementProgressBy(prog_val_encode[index]);
                    prog_status.setText(status_encode[index]);
                }
                else if(type == DECODE_IMAGE){
                    prog_bar.incrementProgressBy(prog_val_decode[index]);
                    prog_status.setText(status_decode[index]);
                }
                ++index;
            }

            protected void onPostExecute(String result) {
                fragment.dismiss();
            }
        };
        return null;
    }
    public static String convertJPEGToPNG(String path){
        String png_path = null;


        return png_path;
    }
    public static void sendPictureMessage(String encoded_image_path){

    }

    public static String getRealPathFromURI(Activity a, Uri uri) {
        Cursor cursor = a.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
