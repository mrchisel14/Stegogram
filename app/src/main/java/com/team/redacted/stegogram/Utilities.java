package com.team.redacted.stegogram;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Shawn on 10/16/2015.
 */
public class Utilities {
    public static int DECODE_IMAGE = 0, ENCODE_IMAGE = 1;
    static int index = 0, prog_val_encode [] = {0, 10, 40, 40, 10}, prog_val_decode[] = {40, 40, 20};
    public final static String status_encode [] = {
      "Converting Image", "Encrypting Data", "Encoding Data", "Sending Image", "Finished"
    }, status_decode [] = {
          "Decoding Image", "Decrypting Data", "Finishing"
    };
    static Uri png_uri = null;

    public static int createStegogramRequest(final Activity a, final String recipients, final Uri image_uri, String password, String message, final int type){
        final ProgressDialog fragment = new ProgressDialog();
        fragment.setArgs(a, recipients, image_uri, password, message, type);
        fragment.show(a.getFragmentManager(), "ProgressBar");
        return 0;
    }
    public static int performRequest(final ProgressDialog fragment, final Activity a, final String recipients, final Uri image_uri, String password, String message, final int type){
        View v = fragment.view;
        if(v == null){
            Log.d("Debug", "V is null");
            return -1;
        }
        final ProgressBar prog_bar = (ProgressBar) v.findViewById(R.id.progress_bar);
        prog_bar.setVisibility(View.INVISIBLE);
        final TextView prog_status = (TextView)v.findViewById(R.id.progress_text);
        prog_bar.setMax(100);
        prog_bar.setProgress(0);
        new AsyncTask<String, Void, Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                prog_bar.setVisibility(View.VISIBLE);
                index = 0;
            }

            @Override
            protected Void doInBackground(String... params) {
                // **Code**
                //Looper.prepare();
                if(type == DECODE_IMAGE){
                    publishProgress();
                }
                else if(type == ENCODE_IMAGE){
                    Log.d("Debug", "Encode Image Entered");
                    publishProgress();

                    Bitmap png_image = null;
                    try{
                        Bitmap image = BitmapFactory.decodeFile(image_uri.getPath());
                        png_image = convertJPEGToPNG(a, image);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    SystemClock.sleep(5000);
                    Log.d("Debug", "Finished image compression");
                    if(png_image != null){
                        Bitmap encoded_image = null;
                        String encrypted_message = null;
                        Log.d("Debug", "Png Not null");
                        publishProgress();
                        /*Call Encryption*/
                        SystemClock.sleep(5000);
                        Log.d("Debug", "After Encryption");
                        publishProgress();
                        /*Call Encoding encoded_path = func()*/
                        SystemClock.sleep(5000);
                        Log.d("Debug", "After Encoding");
                        publishProgress();
                        /*Send Image*/
                        SystemClock.sleep(5000);
                        sendPictureMessage(a, recipients, png_image);
                        Log.d("Debug", "After Send Image");
                        publishProgress();
                    }
                    else{
                        Log.d("Debug", "Failed Converting Image");
                        Toast.makeText(a,"Failed converting image", Toast.LENGTH_SHORT);
                    }
                }
                return null;
            }
            protected void onProgressUpdate(Void... progress) {
                super.onProgressUpdate();
                Log.d("Debug", "On Progress Update");
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

            protected void onPostExecute(Void result) {
                Log.d("Debug", "On Post Execute");
                fragment.dismiss();
                a.finish();
            }
        }.execute();
        return -1;
    }
    public static Bitmap convertJPEGToPNG(Context c, Bitmap image){
        try {
            Uri fileUri = getOutputMediaFileUri(c);
            File file = new File(fileUri.getPath());
            if(!file.exists())
                if(!file.createNewFile()){
                    Log.d("Error:","Failed to create new png file");
                }
            FileOutputStream out = new FileOutputStream(fileUri.getPath());
            image = getResizedBitmap(image, 640);
            image.compress(Bitmap.CompressFormat.PNG, 100, out); //100-best quality
            out.close();
            png_uri = fileUri;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }
    public static void sendPictureMessage(Context c, String recipients, Bitmap encoded_image){
        Log.d("Debug", "In Send Picture Message");
        Uri image = png_uri;
        String path = png_uri.getPath();
        if(image == null){
            Log.d("Debug", "Image is null");
            new Toast(c).makeText(c,"Failed to Send Picture Message", Toast.LENGTH_SHORT).show();
            return;
        }
        if(path == null) {
            Log.d("Debug", "Failed to get real path in send Activity");
            new Toast(c).makeText(c,"Failed to Send Picture Message", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent picMessageIntent = new Intent(Intent.ACTION_SEND);
        picMessageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Bundle extras = new Bundle();
        //extras.putString("address", recipients);
        //extras.putString(Intent.EXTRA_STREAM, image.toString());
        //picMessageIntent.putExtra("address", recipients);
        picMessageIntent.putExtra(Intent.EXTRA_STREAM, image);
       // picMessageIntent.putExtras(extras);
        picMessageIntent.setType("image/png");
        c.startActivity(Intent.createChooser(picMessageIntent, null));
    }
    public static Uri getOutputMediaFileUri(Context c){
        File mediaStorageDir;
        if(isExternalStorageWritable()) {
            Log.d("Debug: ", "Can write to external storage");
            mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/Stegogram/");
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
        Log.d("Debug", "Media file is " + mediaFile.getPath());
        return Uri.fromFile(mediaFile);
    }
    public static String getRealPathFromURI(Context c, Uri uri) {
        String ret = "";
        try{
            Log.d("Debug", "get real path from uri: " + uri.getPath());
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
                if (String.valueOf(uri).contains("documents")) {

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

                } else {
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
            }
        }catch (Exception e){
            Log.d("Debug", "Failed to get real path");
            e.printStackTrace();
            return uri.getPath();
        }

        return ret;
    }
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
