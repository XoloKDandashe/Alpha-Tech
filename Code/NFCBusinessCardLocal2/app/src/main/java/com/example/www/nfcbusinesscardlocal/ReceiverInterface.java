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
import android.widget.Toast;

public class ReceiverInterface extends DialogFragment {

    TestUser person;
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
        return view;
    }
    public void openNFCRecieve(View view)
    {
        if(!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC))
        {
            Toast.makeText(view.getContext(), "NFC is not available. Use QR option.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent mov = new Intent(view.getContext(), ReceiverActivity.class);
        startActivity(mov);
    }
    public void openQRRecieve(View view)
    {
        Intent intent = new Intent(view.getContext(),ReaderActivity.class);
        startActivity(intent);
    }
    public void openRecieveImport(View view)
    {
        Intent intent = new Intent(view.getContext(),RecieverOCR.class);
        startActivity(intent);
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
