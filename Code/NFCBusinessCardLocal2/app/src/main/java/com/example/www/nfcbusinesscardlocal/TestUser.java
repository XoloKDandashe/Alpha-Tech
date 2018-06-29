package com.example.www.nfcbusinesscardlocal;

import android.os.Parcelable;
import android.telecom.Call;

import junit.framework.Test;

import java.io.Serializable;



public class TestUser implements Serializable{
    //Attributes
    private String fullname, jobTitle, password;
    private String companyName,mobileNumber,workTelephone,emailAddress,workAddress;
    //String facebook_link,linkedIn_link,googleplus_link;
    public TestUser()
    {
        fullname=jobTitle=password=companyName=workAddress=mobileNumber=workTelephone=emailAddress="";//facebook_link=linkedIn_link=googleplus_link="";
    }
    public TestUser(TestUser copy)
    {
        fullname=copy.getFullname(); jobTitle=copy.getJobTitle();password=copy.getPassword();
        companyName=copy.getCompanyName();mobileNumber=copy.getMobileNumber();workTelephone=copy.getWorkTelephone();
        emailAddress=copy.getEmailAddress();workAddress=copy.getWorkAddress();
    }
    //setters
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setWorkTelephone(String workTelephone) {
        this.workTelephone = workTelephone;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public void setWorkAddress(String workAddress) {this.workAddress = workAddress;}

    /* void setFacebook_link(String facebook_link) {
        this.facebook_link = facebook_link;
    }

    public void setGoogleplus_link(String googleplus_link) {
        this.googleplus_link = googleplus_link;
    }

    public void setLinkedIn_link(String linkedIn_link) {
        this.linkedIn_link = linkedIn_link;
    }*/
    //getters

    public String getFullname() {

        if(fullname.isEmpty()||fullname.compareTo("")==0)
        { return "n/a";}
            return fullname;
    }

    public String getJobTitle() {
        if((jobTitle.isEmpty())||jobTitle.compareTo("")==0)
        { return "n/a";}
        return jobTitle;
    }

    public String getPassword() {
        if(password.isEmpty()||password.compareTo("")==0)
        { return "n/a";}
        return password;
    }

    public String getCompanyName() {
        if(companyName.isEmpty()||companyName.compareTo("")==0)
        { return "n/a";}
        return companyName;
    }

    public String getWorkTelephone() {
        if(workTelephone.isEmpty()||workTelephone.compareTo("")==0)
        { return "n/a";}
        return workTelephone;
    }

    public String getMobileNumber() {
        if(mobileNumber.isEmpty()||mobileNumber.compareTo("")==0)
        { return "n/a";}
        return mobileNumber;}

    public String getEmailAddress() {
        if(emailAddress.isEmpty()||emailAddress.compareTo("")==0)
        { return "n/a";}
        return emailAddress;
    }

    public String getWorkAddress() {
       if(workAddress.isEmpty()||workAddress.compareTo("")==0)
        { return "n/a";}
        /*String [] stringArray=workAddress.split(",");
        String ReturnString="";
        for(int i=0;i<stringArray.length;i++)
        {
            ReturnString+=stringArray[i];
            if((i+1)<stringArray.length==true)
            {
                ReturnString+=",";
            }
        }*/
        return workAddress;
    }
    public String getDetails()
    {
        String returnString="";
            returnString+=getFullname()+"\n";
            returnString+=getJobTitle()+"\n";
            returnString+=getCompanyName()+"\n";
            returnString+=getEmailAddress()+"\n";
            returnString+=getMobileNumber()+"\n";
            returnString+=getWorkTelephone()+"\n";
            returnString+=getWorkAddress()+"";

        return returnString;
    }
    public String generateDetails()
    {
        String returnString="";
            returnString+=getFullname()+",";
            returnString+=getJobTitle()+",";
            returnString+=getCompanyName()+",";
            returnString+=getEmailAddress()+",";
            returnString+=getMobileNumber()+",";
            returnString+=getWorkTelephone()+",";
            returnString+=getWorkAddress()+"";


        return returnString;
    }
    /*public String getFacebook_link() {
        return facebook_link;
    }

    public String getLinkedIn_link() {
        return linkedIn_link;
    }

    public String getGoogleplus_link() {
        return googleplus_link;
    }*/
    //class functions
}
