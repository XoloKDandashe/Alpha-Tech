package com.example.www.nfcbusinesscardlocal;

import android.app.DialogFragment;
import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.nio.charset.Charset;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardDeleteInformationFragment extends DialogFragment {

    private TextView mTvMessage;
    private Listener mListener;
    public static final String TAG = CardDeleteInformationFragment.class.getSimpleName();
    public static CardDeleteInformationFragment newInstance() {

        return new CardDeleteInformationFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_card_delete_information, container, false);
        mTvMessage = (TextView) view.findViewById(R.id.tv_del_message);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (CardDeleteInformation)context;
        mListener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onDialogDismissed();
    }

    public void onNfcDetected(Ndef ndef, String messageToWrite){
        writeToNfc(ndef,messageToWrite);
    }

    private void writeToNfc(Ndef ndef, String message){

        mTvMessage.setText(getString(R.string.message_delete_progress));
        if (ndef != null) {

            try {
                ndef.connect();
                NdefRecord mimeRecord = NdefRecord.createMime("text/plain", message.getBytes(Charset.forName("US-ASCII")));
                ndef.writeNdefMessage(new NdefMessage(mimeRecord));
                ndef.close();
                //Write Successful
                mTvMessage.setText(getString(R.string.message_delete_success));
                dismiss();

            } catch (IOException | FormatException e) {
                e.printStackTrace();
                mTvMessage.setText(getString(R.string.message_delete_error));

            } finally {
            }

        }
    }
}
