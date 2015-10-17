package com.team.redacted.stegogram;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Shawn on 10/12/2015.
 */
public class SelectImageDialogFragment extends DialogFragment {
    String path_name = null;
    int PICK_IMAGE = 1, CAPTURE_IMAGE = 2;
    private Uri fileUri;
    EditText path;
    OnDialogDismissListener mCallback;

    // Container Activity must implement this interface
    public interface OnDialogDismissListener {
        void onDialogDismissListener(String path);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_setimage, null);
        ImageButton select_image = (ImageButton)v.findViewById(R.id.select_file);
        ImageButton take_photo = (ImageButton)v.findViewById(R.id.take_picture_button);
        Button cancel_button = (Button)v.findViewById(R.id.select_image_cancel);
        Button ok_button = (Button)v.findViewById(R.id.select_image_ok);
        path = (EditText)v.findViewById(R.id.select_path);

        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Search for image in file system and update path
                 getFilePath();
            }
        });
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call take picture activity and update path name
                path_name = getFilePathTakePicture();
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImageDialogFragment.this.getDialog().cancel();
            }
        });
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path_name != null) {
                    // Call Activity back and pass path_name
                    mCallback.onDialogDismissListener(path_name);
                    SelectImageDialogFragment.this.getDialog().cancel();
                } else {
                    Toast toast = Toast.makeText(getActivity(), "Image path not selected", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        builder.setView(v);
        return builder.create();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnDialogDismissListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDialogDismissListener");
        }
    }
    void getFilePath(){
        //Open file manager grab path of selected image ensure image is png
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }
    String getFilePathTakePicture(){
        //open camera return path for picture taken
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE);
        return path_name;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Debug: ", "In On Activity Result");
            if (resultCode == PICK_IMAGE) {
                Log.d("Debug: ", "In Pick Image");
                if(resultCode == Activity.RESULT_OK) {
                    Log.d("Debug: ", "Result OKay");
                    path_name = getRealPathFromURI(data.getData());
                    if(path_name != null) {
                        path.setText(path_name);
                        Log.d("Debug: ", "Path Name = " + path_name);
                    }
                    else
                        Toast.makeText(getActivity(), "Failed selecting image from file system", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getActivity(), "Failed retrieving data from activity", Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == CAPTURE_IMAGE) {
                if(resultCode == Activity.RESULT_OK) {
                    path_name = getRealPathFromURI(fileUri);
                    if(path_name != null)
                        path.setText(path_name);
                    else
                        Toast.makeText(getActivity(), "Failed selecting image from Camera", Toast.LENGTH_SHORT).show();
                }
            }
    }
    Uri getOutputMediaFileUri(){
        File mediaStorageDir;
        if(isExternalStorageWritable()) {
            Log.d("Debug: ", "Can write to external storage");
            mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Stegogram");
        }
        else
            mediaStorageDir = getActivity().getFilesDir();
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
        return Uri.fromFile(mediaFile);
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
