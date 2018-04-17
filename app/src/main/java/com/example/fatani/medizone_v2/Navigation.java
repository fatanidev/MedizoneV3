package com.example.fatani.medizone_v2;


import android.app.DialogFragment;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import static com.example.fatani.medizone_v2.Db.dataDoctors;
import static com.example.fatani.medizone_v2.Db.dataPatients;



public class Navigation extends AppCompatActivity {

    private String image_name;
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
            doctorName.setText("Welcome Dr " + doctor_id.getfName() + " " + doctor_id.getlName());
            doctorID = value;

            if (doctorID == 1) {
                doctorImage.setImageDrawable(getDrawable(R.drawable.sabaa));
            } else if (doctorID == 2) {
                doctorImage.setImageDrawable(getDrawable(R.drawable.oxana));
            } else if (doctorID == 3) {
                doctorImage.setImageDrawable(getDrawable(R.drawable.abdo));
            }
        }

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
            DialogFragment dialog = new LoadingDialogFragment();
            dialog.show(getFragmentManager(), "MyDialogFragmentTag");
            bitmap = BitmapFactory.decodeFile(file_uri.getPath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            makeRequest();
        }
    }

    private void getFileUri() {
        image_name = "patient.jpg";
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + image_name);
        file_uri = Uri.fromFile(file);
    }

    private void makeRequest() {
        ImageView iv = findViewById(R.id.imgPatient);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file_uri.getEncodedPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        Bitmap rotated = null;
        if (orientation == "8") {

            rotated = RotateBitmap(bitmap, -90);
        } else {
            rotated = RotateBitmap(bitmap, 90);
        }
        iv.setImageBitmap(rotated);
        Toast.makeText(this, "Facial Recognition Process Started", Toast.LENGTH_LONG).show();
        recognise(rotated);
    }

    public void recognise(final Bitmap image) {
        // listener
        KairosListener listener = new KairosListener() {

            @Override
            public void onSuccess(String response) {
                Log.d("KAIROS DEMO", response);
                String search = "ErrCode";
                String search2 = "\"message\":\"no match found\"";

                if (response.toLowerCase().indexOf(search.toLowerCase()) != -1) {

                    toast("No face Found");
                } else if (response.toLowerCase().indexOf(search2.toLowerCase()) != -1) {
                    enroll(image, "patients", "2");
                    DialogFragment dialog = new UnEnrolledDialogFragment("24", "Male", "0.9", "0.6", "0.1", "0.1");
                    dialog.show(getFragmentManager(), "MyDialogFragmentTag");
                    // Enumeration e = dataPatients.keys();
                    //Integer newKey = (Integer) e.nextElement() + 1;
                    //System.out.println(e.nextElement());
                } else {
                    String verifyFirstWord = "{\"confidence\":";
                    String verifySecondword = ",\"enrollment_timestamp\":";
                    String idFirstWord = ",\"subject_id\":\"";
                    String idSecondword = "\"}],\"transaction\":{\"confidence\":";

                    String text = response;
                    double value = Double.parseDouble(getTextBetweenTwoWords(verifyFirstWord, verifySecondword, text));
                    if (value >= 0.8) {
                        String text2 = getTextBetweenTwoWords(idFirstWord, idSecondword, text);
                        String subjectID = String.valueOf(text2.charAt(0));

                        Integer patient_id = Integer.parseInt(subjectID);
                        enroll(image, "patients", String.valueOf(patient_id));
                        loggedin(patient_id, doctorID);
                    }
                }
            }
            private String getTextBetweenTwoWords(String firstWord, String secondword, String text) {
                return text.substring(text.indexOf(firstWord) + firstWord.length(), text.indexOf(secondword));
            }
            @Override
            public void onFail(String response) {
                Log.d("KAIROS DEMO", response);
            }
        };
  /* * * instantiate a new kairos instance * * */
        Kairos myKairos = new Kairos();

  /* * * set authentication * * */
        String app_id = "88c60968";
        String api_key = "25f9663f3362b2ab187a1c474f00edcd";
        myKairos.setAuthentication(this, app_id, api_key);

        String galleryId = "patients";
        String selector = "FULL";
        String threshold = "0.75";
        String minHeadScale = "0.25";
        String maxNumResults = "25";
        try {
            myKairos.recognize(image,
                    galleryId,
                    selector,
                    threshold,
                    minHeadScale,
                    maxNumResults,
                    listener);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void loggedin(Integer patientId, Integer doctorID) {
        Intent cameraIntent = new Intent(Navigation.this, MainActivity.class);
        cameraIntent.putExtra("EXTRA_PATIENT_ID", patientId);
        cameraIntent.putExtra("EXTRA_DOCTOR_ID", doctorID);
        startActivity(cameraIntent);
    }
    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void enroll(Bitmap image, String galleryID, String patientID) {
        // listener
        KairosListener listener = new KairosListener() {

            @Override
            public void onSuccess(String response) {
                Log.d("KAIROS DEMO", response);
                toast("Image enrolled to increase accuracy of recognition");
                String search = "ErrCode";
                String search2 = "\"message\":\"no match found\"";

                if (response.toLowerCase().indexOf(search.toLowerCase()) != -1) {

                    toast("No face Found");
                }
            }
            private String getTextBetweenTwoWords(String firstWord, String secondword, String text) {
                return text.substring(text.indexOf(firstWord) + firstWord.length(), text.indexOf(secondword));
            }
            @Override
            public void onFail(String response) {
                Log.d("KAIROS DEMO", response);
            }
        };
  /* * * instantiate a new kairos instance * * */
        Kairos myKairos = new Kairos();
  /* * * set authentication * * */
        String app_id = "88c60968";
        String api_key = "25f9663f3362b2ab187a1c474f00edcd";
        myKairos.setAuthentication(this, app_id, api_key);

        String subjectId = patientID;
        String galleryId = galleryID;
        String selector = "FULL";
        String multipleFaces = "false";
        String minHeadScale = "0.25";
        try {
            myKairos.enroll(image,
                    subjectId,
                    galleryId,
                    selector,
                    multipleFaces,
                    minHeadScale,
                    listener);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}