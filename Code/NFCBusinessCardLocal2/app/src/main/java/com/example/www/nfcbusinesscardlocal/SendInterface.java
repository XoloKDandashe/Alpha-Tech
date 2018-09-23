package com.example.www.nfcbusinesscardlocal;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SendInterface extends DialogFragment {

    private Button btn_nfc;
    private Button btn_qr;
    private Listener mListener;
    public static final String TAG = SendInterface.class.getSimpleName();
    public static SendInterface newInstance() {

        return new SendInterface();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_send_interface, container, false);
        btn_nfc = (Button) view.findViewById(R.id.btn_nfc);
        if(!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC))
        {
            btn_nfc.setVisibility(View.INVISIBLE);
        }

        btn_nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC))
                {
                    Toast.makeText(view.getContext(), "NFC is not available. Use QR option.", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent mov = new Intent(view.getContext(), NFCActivity.class);
                startActivity(mov);
            }
        });
        btn_qr = (Button) view.findViewById(R.id.btn_qr);
        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mov = new Intent(view.getContext(), GenerateQRCode.class);
                startActivity(mov);
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
        if(mListener!=null)
        mListener.onDialogDismissed();
    }
}
