package com.example.fatani.medizone_v2;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;


public class UnEnrolledDialogFragment extends DialogFragment {
    String age;
    String gender;
    String asian;
    String black;
    String white;
    String hispanic;

    public UnEnrolledDialogFragment(String age, String gender, String asian, String black, String white, String hispanic) {
        this.age = age;
        this.gender = gender;
        this.asian = asian;
        this.black = black;
        this.white = white;
        this.hispanic = hispanic;
    }
    public void setAge(String age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAsian(String asian) {
        this.asian = asian;
    }

    public void setBlack(String black) {
        this.black = black;
    }

    public void setWhite(String white) {
        this.white = white;
    }

    public void setHispanic(String hispanic) {
        this.hispanic = hispanic;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.patientdialog, null));
        String search  = "ErrCode";
        String search2 = "\"message\":\"no match found\"";


        // Create the AlertDialog object and return it
        return builder.create();
    }

}