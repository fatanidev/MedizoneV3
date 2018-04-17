package com.example.fatani.medizone_v2;

/**
 * Created by Fatani on 4/17/2018.
 */

public class Patient {
    String fName;
    String lName;
    String dob;
    String ethnicity;
    String location;
    String phoneNo;
    String familyContact;
    String familyLocation;
    String bloodType;

    public Patient(String fName, String lName, String dob, String ethnicity, String location, String phoneNo, String familyContact, String familyLocation, String bloodType, String height, String weight, String sufferingDisease, String insufficiency) {
        this.fName = fName;
        this.lName = lName;
        this.dob = dob;
        this.ethnicity = ethnicity;
        this.location = location;
        this.phoneNo = phoneNo;
        this.familyContact = familyContact;
        this.familyLocation = familyLocation;
        this.bloodType = bloodType;
        this.height = height;
        this.weight = weight;
        this.sufferingDisease = sufferingDisease;
        this.insufficiency = insufficiency;
    }

    String height;
    String weight;
    String sufferingDisease;
    String insufficiency;




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

    public String getFamilyContact() {
        return familyContact;
    }

    public void setFamilyContact(String familyContact) {
        this.familyContact = familyContact;
    }

    public String getFamilyLocation() {
        return familyLocation;
    }

    public void setFamilyLocation(String familyLocation) {
        this.familyLocation = familyLocation;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSufferingDisease() {
        return sufferingDisease;
    }

    public void setSufferingDisease(String sufferingDisease) {
        this.sufferingDisease = sufferingDisease;
    }

    public String getInsufficiency() {
        return insufficiency;
    }

    public void setInsufficiency(String insufficiency) {
        this.insufficiency = insufficiency;
    }


}
