package com.example.www.nfcbusinesscardlocal;

import android.content.Context;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class CardReadInformationFragment extends DialogFragment {
    public static final String TAG = CardReadInformationFragment.class.getSimpleName();

    public static CardReadInformationFragment newInstance() {

        return new CardReadInformationFragment();
    }

    private TextView mTvMessage;
    private Listener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card_read_information,container,false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {

        mTvMessage = (TextView) view.findViewById(R.id.tv_message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (CardReadInformation)context;
        mListener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mListener!=null)
        mListener.onDialogDismissed();
    }

    public String onNfcDetected(Ndef ndef){

        return readFromNFC(ndef);
    }

    private String readFromNFC(Ndef ndef) {

        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            mTvMessage.setText("Read was successful");
            if(ndefMessage==null)
            {

                return "No information available on card.";

            }
            String message = new String(ndefMessage.getRecords()[0].getPayload());


            ndef.close();
            if(message.trim().compareTo("")==0||message.trim().length()==0)
            {
                return "No information available.";

            }
            String[] details=message.split("\n");
            String payload="";
            int length=details.length;
            if(details.length==0)
            {
                return "No information available.";
            }
            else if(details.length<6){
                return "Information is not for our application.";
            }
            if(length>7)
                length--;
            for(int i=0;i<length;i++) {
                payload += details[i];
                if((i+1)<length)
                    payload+="\n";
            }
            return payload;

        } catch (IOException | FormatException e) {
            e.printStackTrace();

        }
        return "Unable to read card";
    }
}
