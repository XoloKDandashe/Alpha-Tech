package com.example.www.nfcbusinesscardlocal;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CardDeleteInformation extends AppCompatActivity implements Listener {

    public static final String TAG = CardDeleteInformation.class.getSimpleName();
    private CardDeleteInformationFragment cardDeleteInformationFragment;
    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;
    private NfcAdapter nfcAdapter;
    private Button btn_delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_delete_information);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        btn_delete=(Button) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteFragment();
            }
        });
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
    public void onBackPressed() {
        super.onBackPressed();
    }
    private void showDeleteFragment() {

        isWrite = true;

        cardDeleteInformationFragment= (CardDeleteInformationFragment) getFragmentManager().findFragmentByTag(CardDeleteInformationFragment.TAG);

        if (cardDeleteInformationFragment == null) {

            cardDeleteInformationFragment = CardDeleteInformationFragment.newInstance();
        }
        cardDeleteInformationFragment.show(getFragmentManager(),CardDeleteInformationFragment.TAG);

    }
    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if(tag != null) {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

            if (isDialogDisplayed) {

                if (isWrite) {
                    cardDeleteInformationFragment= (CardDeleteInformationFragment) getFragmentManager().findFragmentByTag(CardDeleteInformationFragment.TAG);
                    cardDeleteInformationFragment.onNfcDetected(ndef,"");
                    Toast.makeText(this, "Information has been deleted.", Toast.LENGTH_SHORT).show();
                    finish();

                } else {

                    Toast.makeText(this,"Unable to delete.",Toast.LENGTH_SHORT);
                    finish();
                    return;
                }
            }
        }
    }
}
