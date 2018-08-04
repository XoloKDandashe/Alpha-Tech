package com.example.www.nfcbusinesscardlocal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Xolo Kagiso Dandashe on 19 Apr 2018.
 */

public class ClientListAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> arrayList;
    ArrayList<String> original;
    private static LayoutInflater inflater = null;
    private ImageView imageView;

    public ClientListAdapter(Context context,ArrayList<String> arrayList){
        this.context = context;
        this.arrayList = arrayList;
        this.original=new ArrayList<>();
        for (String user:arrayList){
            original.add(user);
        }
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public String getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if (vi == null)
            vi = inflater.inflate(R.layout.rows, null);

            String s= arrayList.get(i);
            String [] details=s.split("\n");
            TestUser newCard=new TestUser();
            newCard.setFullname(details[0]);
            newCard.setJobTitle(details[1]);
            newCard.setCompanyName(details[2]);
            newCard.setEmailAddress(details[3]);
            newCard.setMobileNumber(details[4]);
            newCard.setWorkTelephone(details[5]);
            if(details.length>7) {
                newCard.setImageUrl(details[7]);
                imageView = (ImageView) vi.findViewById(R.id.row_picture);
                loadPicture(newCard, vi);
            }
            TextView text = (TextView) vi.findViewById(R.id.row_fullname);
            text.setText(newCard.getFullname());
            /*text = (TextView) vi.findViewById(R.id.row_company);
            text.setText(newCard.getCompanyName());*/
            text = (TextView) vi.findViewById(R.id.row_jobtitle);
            text.setText(newCard.getJobTitle());
            /*text = (TextView) vi.findViewById(R.id.row_emailaddress);
            text.setText(newCard.getEmailAddress());
            text = (TextView) vi.findViewById(R.id.row_phonenumber);
            text.setText(newCard.getMobileNumber());
            text = (TextView) vi.findViewById(R.id.row_worknumber);
            text.setText(newCard.getWorkTelephone());*/

            return vi;
    }
    private void loadPicture(TestUser user,View view){
        imageView.setImageURI(null);
        if(user.getImageUrl()!=""){
            StorageReference httpRef= FirebaseStorage.getInstance().getReferenceFromUrl(user.getImageUrl());
            Glide.with(view.getContext())
                    .using(new FirebaseImageLoader())
                    .load(httpRef)
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(imageView);
        }
    }
    public void filter(String string){
        ArrayList <String> filterlist=new ArrayList<>();
        string= string.toLowerCase(Locale.getDefault());
        if (string.length() == 0) {
            for (String user:original) {
                filterlist.add(user);

                }
        } else {
            for (String user : original) {
                if (user.toLowerCase(Locale.getDefault()).contains(string)) {
                    filterlist.add(user);
                }
            }
        }
        this.arrayList=filterlist;
        this.sortAlphabetically(arrayList);
    }
    public void sortAlphabetically(ArrayList<String> list)
    {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                String [] user1=s.split("\n");
                String [] user2=t1.split("\n");
                return user1[0].compareTo(user2[0]);
            }
        });
    }
    public void sortDate(ArrayList<String> list)
    {
        ArrayList<String> reverse= new ArrayList<>();
        for(int i=list.size()-1;i>-1;i--)
        {
            reverse.add(list.get(i));
        }
        list.clear();
        for (String s:reverse) {
            list.add(s);
        }
    }
    public void sortJob(ArrayList<String> list)
    {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                String [] user1=s.split("\n");
                String [] user2=t1.split("\n");
                return user1[1].compareTo(user2[1]);
            }
        });
    }
}
