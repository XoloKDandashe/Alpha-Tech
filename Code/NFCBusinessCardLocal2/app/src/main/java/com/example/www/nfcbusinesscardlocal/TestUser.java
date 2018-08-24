package com.example.www.nfcbusinesscardlocal;

import android.os.Parcelable;
import android.telecom.Call;

import junit.framework.Test;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.MessageDigest;

public class TestUser implements Serializable{
    //Attributes
    private String fullname, jobTitle, password;
    private String companyName,mobileNumber,workTelephone,emailAddress,workAddress;
    private String recievedCards="",imageUrl="";
    //String facebook_link,linkedIn_link,googleplus_link;
    public TestUser(){
    }
    public TestUser(TestUser copy)
    {
        fullname=copy.getFullname(); jobTitle=copy.getJobTitle();password=copy.getPassword();
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

    public void setPassword(String password) //throws NoSuchAlgorithmException,NoSuchProviderException
    {
        //byte[] salt=getSalt();
        this.password = password;//getSecurePassword(password,salt);
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
        return workAddress;
    }
    public String getRecievedCards(){
        if(recievedCards.isEmpty()||workAddress.compareTo("")==0)
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
            returnString+=getFullname()+"%";
            returnString+=getJobTitle()+"%";
            returnString+=getCompanyName()+"%";
            returnString+=getEmailAddress()+"%";
            returnString+=getMobileNumber()+"%";
            returnString+=getWorkTelephone()+"%";
            returnString+=getWorkAddress()+"%";
            returnString+=getImageUrl();

        return returnString;
    }
    private static String getSecurePassword(String passwordToHash, byte[] salt)
    {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(salt);
            //Get the hash's bytes
            byte[] bytes = md.digest(passwordToHash.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    //Add salt
    private static byte[] getSalt() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        //Always use a SecureRandom generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        //Create array for salt
        byte[] salt = new byte[16];
        //Get a random salt
        sr.nextBytes(salt);
        //return salt
        return salt;
    }
}
