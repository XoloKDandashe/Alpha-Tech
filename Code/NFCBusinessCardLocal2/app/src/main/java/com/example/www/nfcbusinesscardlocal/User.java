package com.example.www.nfcbusinesscardlocal;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.MessageDigest;

public class User implements Serializable{
    //Attributes
    private String fullname, jobTitle, website="";
    private String companyName,mobileNumber,workTelephone,emailAddress,workAddress;
    private String recievedCards="",imageUrl="";
    //String facebook_link,linkedIn_link,googleplus_link;
    public User(){
    }
    public User(User copy)
    {
        fullname=copy.getFullname(); jobTitle=copy.getJobTitle();website=copy.getWebsite();
        companyName=copy.getCompanyName();mobileNumber=copy.getMobileNumber();workTelephone=copy.getWorkTelephone();
        emailAddress=copy.getEmailAddress();workAddress=copy.getWorkAddress();
        recievedCards=getRecievedCards();
    }
    //setters
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setWebsite(String website)
    {
        this.website = website;
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

    public void setRecievedCards(String recievedCards){
        this.recievedCards=recievedCards;
    }

    public void setImageUrl(String imageUrl){this.imageUrl=imageUrl;}
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

    public String getWebsite() {
        if(website.isEmpty()||website.compareTo("")==0)
        { return "n/a";}
        return website;
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
        return workAddress;
    }
    public String getRecievedCards(){
        if(recievedCards.isEmpty()||recievedCards.compareTo("")==0||recievedCards.compareTo("[]")==0)
            return "";
        return recievedCards;
    }
    public String getImageUrl(){
        if(imageUrl.isEmpty()||imageUrl.compareTo("")==0)
            return "";
        return imageUrl;
    }
    //class functions
    public String getDetails()
    {
        String returnString="";
            returnString+=getFullname()+"\n";
            returnString+=getJobTitle()+"\n";
            returnString+=getCompanyName()+"\n";
            returnString+=getEmailAddress()+"\n";
            returnString+=getMobileNumber()+"\n";
            returnString+=getWorkTelephone()+"\n";
            returnString+=getWorkAddress()+" \n";
            returnString+=getImageUrl();

        return returnString;
    }
    public String generateDetails()
    {
        String returnString="";
        returnString+=getFullname()+"\n";
        returnString+=getJobTitle()+"\n";
        returnString+=getCompanyName()+"\n";
        returnString+=getEmailAddress()+"\n";
        returnString+=getMobileNumber()+"\n";
        returnString+=getWorkTelephone()+"\n";
        returnString+=getWorkAddress()+" \n";
        returnString+=getImageUrl();

        return returnString;
    }

}
