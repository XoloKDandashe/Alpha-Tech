package com.example.www.nfcbusinesscardlocal;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class ReceiverInterface extends DialogFragment {

    private TestUser person;
    private Button btn_nfc,btn_scanner,btn_ocr;
    private Listener mListener;
    public static final String TAG = ReceiverInterface.class.getSimpleName();
    public static ReceiverInterface newInstance() {

        return new ReceiverInterface();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_receiver_interface, container, false);
        btn_nfc=(Button) view.findViewById(R.id.btn_nfc_rec);
        btn_nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC))
                {
                    Toast.makeText(view.getContext(), "NFC is not available. Use QR option.", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent mov = new Intent(view.getContext(), ReceiverActivity.class);
                startActivity(mov);
            }
        });
        btn_scanner=(Button) view.findViewById(R.id.btn_scanner);
        btn_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),ReaderActivity.class);
                startActivity(intent);
            }
        });
        btn_ocr=(Button) view.findViewById(R.id.btn_ocr);
        btn_ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),RecieverOCR.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (MainActivity)context;
        mListener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onDialogDismissed();
    }
}
