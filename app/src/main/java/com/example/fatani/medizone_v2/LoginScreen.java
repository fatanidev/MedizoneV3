package com.example.fatani.medizone_v2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import static com.example.fatani.medizone_v2.Db.populateDB;


public class LoginScreen extends AppCompatActivity {
    private String encoded_string, image_name;

    private Bitmap bitmapImage;
    private File file;
    private Uri file_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        populateDB();
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.med_launcher);

        Button c = (Button) findViewById(R.id.btnLogin);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK) {
           // DialogFragment dialog = new LoadingDialogFragment();
            //dialog.show(getFragmentManager(), "MyDialogFragmentTag");
            bitmapImage = BitmapFactory.decodeFile(file_uri.getPath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(file_uri.getEncodedPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            if (orientation.equals("6")) {
                bitmapImage = RotateBitmap(bitmapImage, 90);

            } else {
                bitmapImage = RotateBitmap(bitmapImage, 270);
            }
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, Base64.NO_WRAP );
            enrollWeb();
        }
    }
    private void getFileUri() {
        image_name = "kairosdoctors.jpeg";
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + image_name);
        file_uri = Uri.fromFile(file);
    }
    private void enrollWeb() {
        ImageView iv = findViewById(R.id.imageView7);
        iv.setImageBitmap(bitmapImage);
        HashMap postData = new HashMap();
        postData.put("recognize", encoded_string);
        postData.put("imageName", image_name);
        PostResponseAsyncTask taskInsert = new PostResponseAsyncTask(LoginScreen.this, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                final String status_complete, status_uncomplete, getId, getConfidence, disgust, fear, joy, sadness, surprise;
                Log.i("TESTING", s);
                status_complete = "\"status\":\"success\",";

                if (s.toLowerCase().indexOf(status_complete.toLowerCase()) != -1) {
                    getConfidence = getTextBetweenTwoWords("\"confidence\":", ",\"enrollment_timestamp\":", s);
                    if(Double.parseDouble(getConfidence) >= 0.6) {
                        getId = getTextBetweenTwoWords(",\"subject_id\":\"", "\",\"topLeftX\":", s);
                        String subjectID = String.valueOf(getId.charAt(0));
                        Integer subID = Integer.parseInt(subjectID);
                        Log.i("SUBJECTID", String.valueOf(subID));
                        loggedin(subID);
                    }
                } else {
                }
                Toast.makeText(LoginScreen.this, s, Toast.LENGTH_LONG).show();
            }
        });
        taskInsert.execute("http://fatanidev.com/demo/insert.php");
    }
    public void loggedin(Integer doctorId) {
        Intent cameraIntent = new Intent(LoginScreen.this, Navigation.class);
        cameraIntent.putExtra("EXTRA_DOCTOR_ID", doctorId);
        startActivity(cameraIntent);
    }
    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    public static String getTextBetweenTwoWords(String firstWord, String secondword, String text) {
        return text.substring(text.indexOf(firstWord) + firstWord.length(), text.indexOf(secondword));
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return true;
    }
}