package com.example.fatani.medizone_v2;

import android.support.v7.app.AppCompatActivity;

import java.util.Hashtable;

/**
 * Created by Fatani on 4/17/2018.
 */

public class Db extends AppCompatActivity {

    static Hashtable<Integer, Patient> dataPatients = new Hashtable<Integer, Patient>();
    static Hashtable<Integer, Doctor> dataDoctors = new Hashtable<Integer, Doctor>();

    public static void populateDB(){
        //patients
        dataPatients.put(1, new Patient("Ahmed", "Tunisi", "24", "Asian","Croyden", "07123456789", "07987654321", "Croyden", "B+", "154", "62", "Lower Respiratory Infections", "Chronic Venous "));
        dataPatients.put(2, new Patient("Akber", "Ali", "23", "Asian","Hayes", "07123456789", "07987654321", "Hayes", "A", "169", "80", "Type 2 Diabetes", "Chronic Venous "));
        dataPatients.put(3, new Patient("Daniel", "Omoregie", "24", "African","Barking", "07123456789", "07987654321", "Barking", "A", "185", "90", "Alzheimers", "Chronic Venous "));
        dataPatients.put(4, new Patient("Mason", "Nosam", "35", "Asian","Chiswick", "07123456789", "07987654321", "White City", "B", "185", "84", "Coronary Artery", "Chronic Venous "));
        //doctors
        dataDoctors.put(1, new Doctor("Sabaa", "Fatani", "23", "Arab", "Reading", "075812345867", "Doctor"));
        dataDoctors.put(2, new Doctor("Oxana", "Inkina", "20", "Russian", "Ealing", "07559864276", "Doctor"));
        dataDoctors.put(3, new Doctor("Abdullah", "Ahmed", "20", "African", "Hayes", "07896758461", "Doctor"));
    }
}

