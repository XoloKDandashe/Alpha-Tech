package com.example.www.nfcbusinesscardlocal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by Xolo Kagiso Dandashe on 24 Apr 2018.
 */

public class AppointmentListAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> arrayList;
    ArrayList<String> original;
    private static LayoutInflater inflater = null;

    public AppointmentListAdapter(Context context,ArrayList<String> arrayList){
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
    vi = inflater.inflate(R.layout.appointments, null);

    String s= arrayList.get(i);
    String [] details=s.split("\n");
    Appointment newAppointment=new Appointment();
            newAppointment.setName(details[0]);
            newAppointment.setDate(details[1]);
            newAppointment.setTime(details[2]);
            if(details.length>3)
            newAppointment.setNotes(details[3]);

    TextView text = (TextView) vi.findViewById(R.id.appointment_name);
            text.setText(newAppointment.getName());
    text = (TextView) vi.findViewById(R.id.appointment_date);
            text.setText(newAppointment.getDate());
    text = (TextView) vi.findViewById(R.id.appointment_time);
            text.setText(newAppointment.getTime());
    text = (TextView) vi.findViewById(R.id.appointment_notes);
            text.setText(newAppointment.getNotes());
        return vi;
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
