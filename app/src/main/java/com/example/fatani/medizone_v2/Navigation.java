package com.example.fatani.medizone_v2;


import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static com.example.fatani.medizone_v2.Db.dataDoctors;
import static com.example.fatani.medizone_v2.LoginScreen.RotateBitmap;
import static com.example.fatani.medizone_v2.LoginScreen.getTextBetweenTwoWords;


public class Navigation extends AppCompatActivity {

    private String image_name, encoded_string;
    private Bitmap bitmap;
    private File file;
    private Uri file_uri;
    Integer doctorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        TextView doctorName = findViewById(R.id.txtDoctorName);
        ImageView doctorImage = findViewById(R.id.imgDoctorFirst);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Integer value = extras.getInt("EXTRA_DOCTOR_ID");
            Doctor doctor_id = dataDoctors.get(value);
            doctorName.setText("Welcome Dr " + doctor_id.getlName());
            doctorID = value;
            if (doctorID == 1) {
                doctorImage.setImageDrawable(getDrawable(R.drawable.sabaa));
            } else if (doctorID == 2) {
                doctorImage.setImageDrawable(getDrawable(R.drawable.oxana));
            } else if (doctorID == 3) {
                doctorImage.setImageDrawable(getDrawable(R.drawable.abdo));
            }
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.med_launcher);
        Button c = (Button) findViewById(R.id.btnScanPatent);
        c.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getFileUri();

                i.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);
                startActivityForResult(i, 10);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            DialogFragment dialog = new BackDialogFragment("Logging Out", "Are you sure you would like to log out?");
            dialog.show(getFragmentManager(), "MyDialogFragmentTag");
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK) {
           // DialogFragment dialog = new LoadingDialogFragment();
            //dialog.show(getFragmentManager(), "MyDialogFragmentTag");
            bitmap = BitmapFactory.decodeFile(file_uri.getPath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(file_uri.getEncodedPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            Log.d("ORI", orientation);
            if (orientation.equals("6")) {
                bitmap = RotateBitmap(bitmap, 90);
            } else {
                bitmap = RotateBitmap(bitmap, 270);
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, Base64.NO_WRAP );
            recogniseWeb();

        }
    }

    private void getFileUri() {
        image_name = "kairospatient.jpeg";
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + image_name);
        file_uri = Uri.fromFile(file);
    }

    private void recogniseWeb() {
        HashMap postData = new HashMap();
        postData.put("recognize", encoded_string);
        postData.put("imageName", image_name);
        PostResponseAsyncTask taskInsert = new PostResponseAsyncTask(Navigation.this, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                final String status_complete, status_uncomplete, getId, getConfidence, disgust, fear, joy, sadness, surprise;
                Log.i("TESTING", s);
                status_complete = "\"status\":\"success\",";
                status_uncomplete = "emotions\":{\"anger\":";


                if (s.toLowerCase().indexOf(status_complete.toLowerCase()) != -1) {
                    getConfidence = getTextBetweenTwoWords("\"confidence\":", ",\"enrollment_timestamp\":", s);
                    if(Double.parseDouble(getConfidence) >= 0.6) {
                        getId = getTextBetweenTwoWords(",\"subject_id\":\"", "\",\"topLeftX\":", s);
                        String subjectID = String.valueOf(getId.charAt(0));
                        Integer subID = Integer.parseInt(subjectID);
                        Log.i("SUBJECTID", String.valueOf(subID));
                        //enrollWeb(subID);
                        emotionWeb(subID, doctorID);
                    }
                } else {
                }
                Toast.makeText(Navigation.this, s, Toast.LENGTH_LONG).show();
            }
        });
        taskInsert.execute("http://fatanidev.com/demo/insert.php");
    }

    private void emotionWeb(final Integer subID, final Integer doctorID) {
        HashMap postData = new HashMap();
        postData.put("encodedString", encoded_string);
        postData.put("imageName", image_name);
        PostResponseAsyncTask taskInsert = new PostResponseAsyncTask(Navigation.this, postData, new AsyncResponse() {
            @Override
            public void processFinish(String emotionResponse) {
                loggedin(subID, doctorID, emotionResponse);
                Toast.makeText(Navigation.this, emotionResponse, Toast.LENGTH_LONG).show();
            }
        });
        taskInsert.execute("http://fatanidev.com/demo/insert.php");
    }


    private void loggedin(Integer patientId, Integer doctorID, String emotionResponse) {
        Intent cameraIntent = new Intent(Navigation.this, MainActivity.class);
        cameraIntent.putExtra("EXTRA_PATIENT_ID", patientId);
        cameraIntent.putExtra("EXTRA_DOCTOR_ID", doctorID);
        cameraIntent.putExtra("EXTRA_EMOTION_RESPONSE", emotionResponse);
        startActivity(cameraIntent);
    }


}