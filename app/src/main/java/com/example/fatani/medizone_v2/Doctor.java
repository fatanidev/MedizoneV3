package com.example.fatani.medizone_v2;

/**
 * Created by Fatani on 4/17/2018.
 */

public class Doctor {
    String fName;
    String lName;
    String dob;
    String ethnicity;
    String location;
    String phoneNo;
    String job;

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }


    public Doctor(String fName, String lName, String dob, String ethnicity, String location, String phoneNo, String job) {
        this.fName = fName;
        this.lName = lName;
        this.dob = dob;
        this.ethnicity = ethnicity;
        this.location = location;
        this.phoneNo = phoneNo;
        this.job = job;
    }


}
