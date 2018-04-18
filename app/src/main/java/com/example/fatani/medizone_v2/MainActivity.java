package com.example.fatani.medizone_v2;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static com.example.fatani.medizone_v2.Db.dataPatients;

public class MainActivity extends AppCompatActivity {
    Integer doctorId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView name = findViewById(R.id.txtFname);
        TextView age = findViewById(R.id.txtAge);
        TextView ethloc = findViewById(R.id.txtEthLoc);
        TextView weight = findViewById(R.id.txtWeight);
        TextView height = findViewById(R.id.txtHeight);
        TextView blood = findViewById(R.id.txtBloodType);
        TextView suffering = findViewById(R.id.txtSuffering);
        TextView insufficiency = findViewById(R.id.txtInsufficiency);
        TextView familyContact = findViewById(R.id.txtFamilyContact);
        TextView familyLocation = findViewById(R.id.txtFamilyLocation);

        ImageView patientImage = findViewById(R.id.imgPatientFirst);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Integer value = extras.getInt("EXTRA_PATIENT_ID");
            Patient patient_id = dataPatients.get(value);



            name.setText(patient_id.fName + " " + patient_id.lName);
            ethloc.setText(patient_id.getEthnicity() + " - " + patient_id.getLocation());
            age.setText(patient_id.getDob());
            weight.setText(patient_id.getWeight());
            height.setText(patient_id.getHeight());
            blood.setText(patient_id.getBloodType());
            suffering.setText(patient_id.getSufferingDisease());
            insufficiency.setText(patient_id.getInsufficiency());
            familyContact.setText(patient_id.getFamilyContact());
            familyLocation.setText(patient_id.getFamilyLocation());

            String imgName = patient_id.getfName();
            doctorId = extras.getInt("EXTRA_DOCTOR_ID");


            if (value == 1) {
                patientImage.setImageDrawable(getDrawable(R.drawable.ahmed));
            } else if (value == 2) {
                patientImage.setImageDrawable(getDrawable(R.drawable.akber));
            } else if (value == 3) {
                patientImage.setImageDrawable(getDrawable(R.drawable.daniel));
            } else if (value == 4) {
                patientImage.setImageDrawable(getDrawable(R.drawable.mason));
            }


        }

        logoActionBar();


        GraphView graph = (GraphView) findViewById(R.id.graph);
        BarGraphSeries < DataPoint > series = new BarGraphSeries < > (new DataPoint[] {
                new DataPoint(0.5, 0),
                new DataPoint(1, 3),
                new DataPoint(2, 3),
                new DataPoint(3, 5),
                new DataPoint(4, 0),
        });
        graph.addSeries(series);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {
                "-",
                "Joy",
                "Suprise",
                "Sadness",
                "Anger",
                "Disgust",
                "Fear",
                "-"
        });
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        GraphView heartRateGraph = (GraphView) findViewById(R.id.graph2);
        LineGraphSeries < DataPoint > lineSeries = new LineGraphSeries < > (new DataPoint[] {
                new DataPoint(0.5, 0),
                new DataPoint(1, 3),
                new DataPoint(2, 3),
                new DataPoint(3, 5),
                new DataPoint(4, 0),
        });
        heartRateGraph.addSeries(lineSeries);

        //series.setValuesOnTopSize(50);
        // use static labels for horizontal and vertical labels
        StaticLabelsFormatter staticLabelsFormatter2 = new StaticLabelsFormatter(heartRateGraph);
        staticLabelsFormatter2.setHorizontalLabels(new String[] {
                "-",
                "Lorem",
                "Ipsum",
                "Dolor",
                "Sit",
                "Amet",
                "Constectetur",
                "-"
        });
        heartRateGraph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter2);
        Button c = (Button) findViewById(R.id.button2);
        c.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        TabHost tabs = (TabHost) findViewById(R.id.tabHost);
        tabs.setup();


        TabHost.TabSpec calculatorTab = tabs.newTabSpec("Facial Analysis");
        calculatorTab.setContent(R.id.FacialAnalysis);
        calculatorTab.setIndicator("Facial Analysis");
        tabs.addTab(calculatorTab);


        TabHost.TabSpec homeTab = tabs.newTabSpec("Scans");
        homeTab.setContent(R.id.Scans);
        homeTab.setIndicator("Scans");
        tabs.addTab(homeTab);


        TabHost.TabSpec faqTab = tabs.newTabSpec("Treatments");
        faqTab.setContent(R.id.Treatments);
        faqTab.setIndicator("Treatments");
        tabs.addTab(faqTab);

        ImageButton LastVisit = (ImageButton) findViewById(R.id.btnLastVisit);
        LastVisit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


            }
        });


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            DialogFragment dialog = new BackPatientDialogFragment("Close Session", "Are you sure you want to cancel session with this patient?", doctorId);
            dialog.show(getFragmentManager(), "MyDialogFragmentTag");
        }
        return true;
    }
    public void logoActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);

        actionBar.setIcon(R.mipmap.med_launcher);
    }


}