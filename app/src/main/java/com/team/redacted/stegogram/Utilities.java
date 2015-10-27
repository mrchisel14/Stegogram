package com.team.redacted.stegogram;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static int createStegogramRequest(final Activity a, final Uri image_uri, String password, String message, final int type){
        final ProgressDialog fragment = new ProgressDialog();
        fragment.setArgs(a, image_uri, password, message, type);
        fragment.show(a.getFragmentManager(), "ProgressBar");
        return 0;
    }
    public static int performRequest(final ProgressDialog fragment, final Activity a, final Uri image_uri, String password, String message, final int type){
        View v = fragment.getView();
        if(v == null){
            Log.d("Debug", "V is null");
            return -1;
        }
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
                    Bitmap png_image = null;
                    try{
                        Bitmap image = MediaStore.Images.Media.getBitmap(a.getContentResolver(), image_uri);
                        png_image = convertJPEGToPNG(a, image);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if(png_image != null){
                        Bitmap encoded_image = null;
                        publishProgress();
                        /*Call Encryption*/
                        SystemClock.sleep(5000);
                        publishProgress();
                        /*Call Encoding encoded_path = func()*/
                        SystemClock.sleep(5000);
                        publishProgress();
                        /*Send Image*/
                        SystemClock.sleep(5000);
                        sendPictureMessage(encoded_image);
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
        return -1;
    }
    public static Bitmap convertJPEGToPNG(Context c, Bitmap image){
        try {
            Uri fileUri = getOutputMediaFileUri(c);
            File file = new File(fileUri.getPath());
            if(!file.exists())file.createNewFile();
            FileOutputStream out = new FileOutputStream(getRealPathFromURI(c, fileUri));
            image.compress(Bitmap.CompressFormat.PNG, 100, out); //100-best quality
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }
    public static void sendPictureMessage(Bitmap encoded_image){

    }
    public static Uri getOutputMediaFileUri(Context c){
        File mediaStorageDir;
        if(isExternalStorageWritable()) {
            Log.d("Debug: ", "Can write to external storage");
            mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/Stegogram");
        }
        else
            mediaStorageDir = c.getFilesDir();
        Log.d("Debug: ", "Media Storage Directory = " + mediaStorageDir.getPath());
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        File mediaFile;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
        if(mediaFile == null){
            Log.d("Debug", "Media File is null");
        }
        else
            Log.d("Debug", "Media file is " + mediaFile.getPath().toString());
        return Uri.fromFile(mediaFile);
    }
    public static String getRealPathFromURI(Context c, Uri uri) {
        String ret = "";
        try{
            // SDK < API11
            if (Build.VERSION.SDK_INT < 11){
                String[] proj = { MediaStore.Images.Media.DATA };
                Cursor cursor = c.getContentResolver().query(uri, proj, null, null, null);
                int column_index
                        = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                ret = cursor.getString(column_index);
                cursor.close();
            }
            // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19){
                String[] proj = { MediaStore.Images.Media.DATA };

                CursorLoader cursorLoader = new CursorLoader(
                        c,
                        uri, proj, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();

                if(cursor != null){
                    int column_index =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    ret = cursor.getString(column_index);
                }
                cursor.close();
            }
            // SDK > 19 (Android 4.4)
            else{
                String wholeID = DocumentsContract.getDocumentId(uri);

                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];

                String[] column = { MediaStore.Images.Media.DATA };

                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                Cursor cursor = c.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    ret = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        }catch (Exception e){
            Log.d("Debug", e.getMessage());
            e.printStackTrace();
            return null;
        }

        return new File(ret).getName();
    }
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
