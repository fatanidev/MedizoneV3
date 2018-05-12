package  com.example.fatani.medizone_v2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.fatani.medizone_v2.Navigation.RotateBitmap;
import static com.example.fatani.medizone_v2.Navigation.getTextBetweenTwoWords;

public class Test extends AppCompatActivity {

    private Button button;
    private String encoded_string, image_name;
    private Bitmap bitmap;
    private File file;
    private Uri file_uri;

    private Context mContext;
    private Activity mActivity;

    private LinearLayout mRootLayout;
    private WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Button t = (Button) findViewById(R.id.start);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getFileUri();
                i.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);
                startActivityForResult(i, 10);
            }
        });






        HashMap postData = new HashMap();
        postData.put("emotionImage", "patient1.jpg");
        PostResponseAsyncTask taskInsert = new PostResponseAsyncTask(Test.this, postData, new AsyncResponse() {


            @Override
            public void processFinish(String s) {
                LineChart chart2 = findViewById(R.id.chart2);
               String status_complete, status_uncomplete;
                Log.i("TESTING", s);
                String response = s;
                status_complete = "\"status_message\":\"Complete\",";
                status_uncomplete = "emotions\":{\"anger\":";

                List<Entry> angerEntries = new ArrayList<>();
                List<Entry> fearEntries = new ArrayList<>();
                List<Entry> entries3 = new ArrayList<>();
                List<Entry> entries4 = new ArrayList<>();
                List<Entry> entries5 = new ArrayList<>();
                List<Entry> entries6 = new ArrayList<>();

                Integer a = s.indexOf("\"emotions\":{");
                Float xPoints = 0f;
                while (a >= 0){

                    String text = s.substring(a);
                    Log.i("TESTING", text);

                   Integer anger = Integer.parseInt(getTextBetweenTwoWords("{\"anger\":", ",\"disgust\":", text));
                    Integer disgust = Integer.parseInt(getTextBetweenTwoWords(",\"disgust\":", ",\"fear\":", text));
                    Integer fear = Integer.parseInt(getTextBetweenTwoWords(",\"fear\":", ",\"joy\":", text));
                    Integer joy = Integer.parseInt(getTextBetweenTwoWords(",\"joy\":", ",\"sadness\":", text));
                    Integer sadness = Integer.parseInt(getTextBetweenTwoWords(",\"sadness\":", ",\"surprise\":", text));
                    Integer  surprise = Integer.parseInt(getTextBetweenTwoWords(",\"surprise\":", "},\"tracking\"", text));
                    System.out.println(fear);
                    angerEntries.add(new Entry(xPoints, anger));

                    fearEntries.add(new Entry(xPoints, disgust));

                    entries3.add(new Entry(xPoints, fear));

                    entries4.add(new Entry(xPoints, joy));

                    entries5.add(new Entry(xPoints, sadness));


                    entries6.add(new Entry(xPoints, surprise));

                    xPoints = xPoints +1 ;

                    a = s.indexOf("\"emotions\":{", a + 1);
                }

                LineDataSet ange = new LineDataSet(angerEntries, "Anger");

                ange.setColor(Color.parseColor("#f44242"));

                LineDataSet disgus = new LineDataSet(fearEntries, "Disgust");

                disgus.setColor(Color.parseColor("#f49b41"));

                LineDataSet fea = new LineDataSet(entries3, "Fear");
                fea.setColor(Color.parseColor("#e541f4"));

                LineDataSet jo = new LineDataSet(entries4, "Joy");
                jo.setColor(Color.parseColor("#43f441"));

                LineDataSet sadnes = new LineDataSet(entries5, "Sadness");
                sadnes.setColor(Color.parseColor("#41f4d9"));

                LineDataSet surpris = new LineDataSet(entries6, "Surprise");
                surpris.setColor(Color.parseColor("#41e2f4"));


                LineData data2 = new LineData(ange, disgus, fea, jo, sadnes, surpris);






                XAxis xAxis = chart2.getXAxis();
                xAxis.setGridLineWidth(5f);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextSize(10f);
                xAxis.setTextColor(Color.RED);
                xAxis.setDrawAxisLine(true);
                xAxis.setDrawGridLines(false);

                String[] values = new String[] {"Dec","Jan","Feb","March","April","May"};
                xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

                chart2.setData(data2);

                chart2.invalidate(); // refresh




                if (s.toLowerCase().indexOf(status_complete.toLowerCase()) != -1 && s.toLowerCase().indexOf(status_uncomplete.toLowerCase()) != -1 ) {


                } else {

                    Toast.makeText(Test.this, s, Toast.LENGTH_LONG).show();
                }
            }
        });
        taskInsert.execute("http://fatanidev.com/demo/insert.php");


        HashMap postDatas = new HashMap();
        postDatas.put("emotionImage2", "patient1.jpg");
        PostResponseAsyncTask taskInsert2 = new PostResponseAsyncTask(Test.this, postDatas, new AsyncResponse() {


            @Override
            public void processFinish(String s) {
                LineChart chart2 = findViewById(R.id.chart3);
                String status_complete, status_uncomplete;
                Log.i("TESTING", s);
                String response = s;
                status_complete = "\"status_message\":\"Complete\",";
                status_uncomplete = "emotions\":{\"anger\":";

                List<Entry> angerEntries = new ArrayList<>();
                List<Entry> fearEntries = new ArrayList<>();
                List<Entry> entries3 = new ArrayList<>();
                List<Entry> entries4 = new ArrayList<>();
                List<Entry> entries5 = new ArrayList<>();
                List<Entry> entries6 = new ArrayList<>();

                Integer a = s.indexOf("\"emotions\":{");
                Float xPoints = 0f;
                while (a >= 0){

                    String text = s.substring(a);
                    Log.i("TESTING", text);

                    Integer anger = Integer.parseInt(getTextBetweenTwoWords("{\"anger\":", ",\"disgust\":", text));
                    Integer disgust = Integer.parseInt(getTextBetweenTwoWords(",\"disgust\":", ",\"fear\":", text));
                    Integer fear = Integer.parseInt(getTextBetweenTwoWords(",\"fear\":", ",\"joy\":", text));
                    Integer joy = Integer.parseInt(getTextBetweenTwoWords(",\"joy\":", ",\"sadness\":", text));
                    Integer sadness = Integer.parseInt(getTextBetweenTwoWords(",\"sadness\":", ",\"surprise\":", text));
                    Integer  surprise = Integer.parseInt(getTextBetweenTwoWords(",\"surprise\":", "},\"tracking\"", text));
                    System.out.println(fear);
                    angerEntries.add(new Entry(xPoints, anger));

                    fearEntries.add(new Entry(xPoints, disgust));

                    entries3.add(new Entry(xPoints, fear));

                    entries4.add(new Entry(xPoints, joy));

                    entries5.add(new Entry(xPoints, sadness));


                    entries6.add(new Entry(xPoints, surprise));

                    xPoints = xPoints +1 ;

                    a = s.indexOf("\"emotions\":{", a + 1);
                }

                LineDataSet ange = new LineDataSet(angerEntries, "Anger");

                ange.setColor(Color.parseColor("#f44242"));

                LineDataSet disgus = new LineDataSet(fearEntries, "Disgust");

                disgus.setColor(Color.parseColor("#f49b41"));

                LineDataSet fea = new LineDataSet(entries3, "Fear");
                fea.setColor(Color.parseColor("#e541f4"));

                LineDataSet jo = new LineDataSet(entries4, "Joy");
                jo.setColor(Color.parseColor("#43f441"));

                LineDataSet sadnes = new LineDataSet(entries5, "Sadness");
                sadnes.setColor(Color.parseColor("#41f4d9"));

                LineDataSet surpris = new LineDataSet(entries6, "Surprise");
                surpris.setColor(Color.parseColor("#41e2f4"));

                LineData data2 = new LineData(ange, disgus, fea, jo, sadnes, surpris);






                XAxis xAxis = chart2.getXAxis();
                xAxis.setGridLineWidth(5f);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextSize(10f);
                xAxis.setTextColor(Color.RED);
                xAxis.setDrawAxisLine(true);
                xAxis.setDrawGridLines(false);

                String[] values = new String[] {"Jun","Jul","Aug","Sep","Oct","Nov"};
                xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

                chart2.setData(data2);

                chart2.invalidate(); // refresh




                if (s.toLowerCase().indexOf(status_complete.toLowerCase()) != -1 && s.toLowerCase().indexOf(status_uncomplete.toLowerCase()) != -1 ) {


                } else {

                    Toast.makeText(Test.this, s, Toast.LENGTH_LONG).show();
                }
            }
        });
        taskInsert2.execute("http://fatanidev.com/demo/insert.php");






        Float test = 32f;







    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK) {
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
            Log.d("TEST", encoded_string);
            byte[] decodedString = Base64.decode(encoded_string, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ImageView image = findViewById(R.id.imageView5);
            image.setImageBitmap(decodedByte);
        makeRequest();
        }
    }

    private void makeRequest() {

       HashMap postData = new HashMap();
       postData.put("encodedString", encoded_string);
       postData.put("imageName", image_name);
        PostResponseAsyncTask taskInsert = new PostResponseAsyncTask(Test.this, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                final String status_complete, status_uncomplete, getId, anger, disgust, fear, joy, sadness, surprise;
                Log.i("TESTING", s);
                status_complete = "\"status_message\":\"Complete\",";
                status_uncomplete = "emotions\":{\"anger\":";
                getId = getTextBetweenTwoWords("{\"id\":\"", "\",\"media_info\":", s);
                Button c = findViewById(R.id.btnRefresh);
                c.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        refreshEmotion(getId);
                    }
                });
                if (s.toLowerCase().indexOf(status_complete.toLowerCase()) != -1 && s.toLowerCase().indexOf(status_uncomplete.toLowerCase()) != -1 ) {
                    anger = getTextBetweenTwoWords("{\"anger\":", ",\"disgust\":", s);
                    disgust = getTextBetweenTwoWords(",\"disgust\":", ",\"fear\":", s);
                    fear = getTextBetweenTwoWords(",\"fear\":", ",\"joy\":", s);
                    joy = getTextBetweenTwoWords(",\"joy\":", ",\"sadness\":", s);
                    sadness = getTextBetweenTwoWords(",\"sadness\":", ",\"surprise\":", s);
                    surprise = getTextBetweenTwoWords(",\"surprise\":", "},\"tracking\"", s );
                    BarChart chart = (BarChart) findViewById(R.id.chart);



                    Float test = 32f;
                    List<BarEntry> entries = new ArrayList<>();
                    entries.add(new BarEntry(0f, Float.parseFloat(anger)));
                    entries.add(new BarEntry(1f, Float.parseFloat(disgust)));
                    entries.add(new BarEntry(2f, Float.parseFloat(fear)));
                    entries.add(new BarEntry(3f, Float.parseFloat(joy)));
                    entries.add(new BarEntry(4f, Float.parseFloat(sadness)));
                    entries.add(new BarEntry(5f, Float.parseFloat(surprise)));

                    BarDataSet ange = new BarDataSet(entries, "");
                    ange.setColors(ColorTemplate.VORDIPLOM_COLORS);


                    BarData data = new BarData(ange);

                    data.setBarWidth(0.9f); // set custom bar width
                    XAxis xAxis = chart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setTextSize(10f);
                    xAxis.setTextColor(Color.RED);
                    xAxis.setDrawAxisLine(true);
                    xAxis.setDrawGridLines(false);

                    String[] values = new String[] { "Anger", "Disgust", "Fear", "Joy", "Sadness", "Surprise"};
                    xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

                    chart.setData(data);

                    chart.setFitBars(true); // make the x-axis fit exactly all bars
                    chart.invalidate(); // refresh
                } else {


}
                Toast.makeText(Test.this, s, Toast.LENGTH_LONG).show();
            }
        });
        taskInsert.execute("http://fatanidev.com/demo/insert.php");
    }

    private void refreshEmotion(String getId) {
        HashMap postData = new HashMap();
        postData.put("mediaId", getId);
        PostResponseAsyncTask taskInsert = new PostResponseAsyncTask(Test.this, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                String status_complete, getId, anger, disgust, fear, joy, sadness, surprise;
                Log.i("TESTING", s);
                status_complete = "\"status_message\":\"Complete\",";

                if (s.toLowerCase().indexOf(status_complete.toLowerCase()) != -1) {
                    anger = getTextBetweenTwoWords("{\"anger\":", ",\"disgust\":", s);
                    disgust = getTextBetweenTwoWords(",\"disgust\":", ",\"fear\":", s);
                    fear = getTextBetweenTwoWords(",\"fear\":", ",\"joy\":", s);
                    joy = getTextBetweenTwoWords(",\"joy\":", ",\"sadness\":", s);
                    sadness = getTextBetweenTwoWords(",\"sadness\":", ",\"surprise\":", s);
                    surprise = getTextBetweenTwoWords(",\"surprise\":", "},\"tracking\"", s );
                    BarChart chart = (BarChart) findViewById(R.id.chart);

                    List<BarEntry> entries = new ArrayList<>();
                    entries.add(new BarEntry(0f, Float.parseFloat(anger)));
                    entries.add(new BarEntry(1f, Float.parseFloat(disgust)));
                    entries.add(new BarEntry(2f, Float.parseFloat(fear)));
                    entries.add(new BarEntry(3f, Float.parseFloat(joy)));
                    entries.add(new BarEntry(4f, Float.parseFloat(sadness)));
                    entries.add(new BarEntry(5f, Float.parseFloat(surprise)));

                    BarDataSet ange = new BarDataSet(entries, "");
                    ange.setColors(ColorTemplate.VORDIPLOM_COLORS);


                    BarData data = new BarData(ange);

                    data.setBarWidth(0.9f); // set custom bar width
                    XAxis xAxis = chart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setTextSize(10f);
                    xAxis.setTextColor(Color.RED);
                    xAxis.setDrawAxisLine(true);
                    xAxis.setDrawGridLines(false);

                    String[] values = new String[] { "Anger", "Disgust", "Fear", "Joy", "Sadness", "Surprise"};
                    xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

                    chart.setData(data);

                    chart.setFitBars(true); // make the x-axis fit exactly all bars
                    chart.invalidate(); // refresh
                } else {


                }
                Toast.makeText(Test.this, s, Toast.LENGTH_LONG).show();
            }
        });
        taskInsert.execute("http://fatanidev.com/demo/insert.php");
    }


    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }

    }
    private void getFileUri() {
        image_name = "emotion.jpeg";
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + image_name
        );
        file_uri = Uri.fromFile(file);
    }


        }