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
import android.widget.Toast;

import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import static com.example.fatani.medizone_v2.Db.populateDB;


public class LoginScreen extends AppCompatActivity {

    private String image_name;
    private Bitmap bitmap;
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
            DialogFragment dialog = new LoadingDialogFragment();
            dialog.show(getFragmentManager(), "MyDialogFragmentTag");
            bitmap = BitmapFactory.decodeFile(file_uri.getPath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            makeRequest();
        }
    }

    private void getFileUri() {
        image_name = "testing123.jpg";
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + image_name);
        file_uri = Uri.fromFile(file);
    }

    private void makeRequest() {
        ImageView iv = findViewById(R.id.imageView7);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file_uri.getEncodedPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        System.out.println("the orentation is " + orientation);
        Integer ori = Integer.parseInt(orientation);
        Bitmap rotated = null;

        if (ori == 8 || ori == 8) {
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
                    toast("No Match Found");
                } else {
                    String verifyFirstWord = "{\"confidence\":";
                    String verifySecondword = ",\"enrollment_timestamp\":";

                    String idFirstWord = ",\"subject_id\":\"";
                    String idSecondword = "\"}],\"transaction\":{\"confidence\":";

                    String text = response;
                    double value = Double.parseDouble(getTextBetweenTwoWords(verifyFirstWord, verifySecondword, text));
                    if (value >= 0.75) {
                        String text2 = getTextBetweenTwoWords(idFirstWord, idSecondword, text);
                        String subjectID = String.valueOf(text2.charAt(0));
                        Integer doctorID = Integer.parseInt(subjectID);
                        enroll(image, String.valueOf(doctorID));
                        loggedin(doctorID);


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


                String galleryId = "doctors";
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



    public void enroll(Bitmap image, String doctorID) {
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


                String subjectId = doctorID;
                String galleryId = "doctors";
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
    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return true;
    }
}