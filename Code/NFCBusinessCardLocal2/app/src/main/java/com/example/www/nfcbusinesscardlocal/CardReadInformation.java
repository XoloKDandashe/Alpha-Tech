package com.example.www.nfcbusinesscardlocal;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CardReadInformation extends AppCompatActivity implements Listener{

    //nfc
    public static final String TAG = CardReadInformation.class.getSimpleName();
    private Button mBtRead;
    private TextView textView;
    private CardReadInformationFragment cardReadInformationFragment;
    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;
    private NfcAdapter nfcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_read_information);
        mBtRead = (Button) findViewById(R.id.btn_read);
        textView= (TextView) findViewById(R.id.tv_read_message);
        mBtRead.setOnClickListener(view -> showReadFragment());
        nfcAdapter=NfcAdapter.getDefaultAdapter(this);
    }


    private void showReadFragment() {
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            return;
        }
        cardReadInformationFragment = (CardReadInformationFragment) getFragmentManager().findFragmentByTag(CardReadInformationFragment.TAG);

        if (cardReadInformationFragment == null) {

            cardReadInformationFragment = CardReadInformationFragment.newInstance();
        }
        cardReadInformationFragment.show(getFragmentManager(),CardReadInformationFragment.TAG);

    }
    @Override
    public void onDialogDisplayed() {

        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {

        isDialogDisplayed = false;
        isWrite = false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if(nfcAdapter!= null)
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nfcAdapter!= null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, "onNewIntent: "+intent.getAction());

        if(tag != null) {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

            if (isDialogDisplayed) {

                if (isWrite) {

                    Toast.makeText(this,"Unable to read.",Toast.LENGTH_SHORT);
                    cardReadInformationFragment.dismiss();
                    return;

                } else {

                    cardReadInformationFragment = (CardReadInformationFragment)getFragmentManager().findFragmentByTag(CardReadInformationFragment.TAG);
                    textView.setText(cardReadInformationFragment.onNfcDetected(ndef));
                    cardReadInformationFragment.dismiss();
                }
            }
        }
    }
}
