package com.example.mayankpadhi.demandbusiness;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CompanyInfoActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    TextView vCompName, vCompEmail, vCompContact, vCompAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Company Details");

        vCompName = (TextView)findViewById(R.id.dispCompName);
        vCompEmail = (TextView)findViewById(R.id.dispCompEmail);
        vCompContact = (TextView)findViewById(R.id.dispCompContact);
        vCompAddress = (TextView)findViewById(R.id.dispCompAddress);

        Intent asd = getIntent();
        final String companyID= asd.getStringExtra("companyID");
        if(companyID.equals("")) {
            AlertDialog alertDialog = new AlertDialog.Builder(CompanyInfoActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Could not fetch details");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
        }

        DocumentReference docRef = db.collection("company").document(companyID);			//USERNAME :- need to fetch from auth
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    vCompName.setText(document.get("Name").toString());
                    vCompEmail.setText(companyID);
                    vCompContact.setText(document.get("Contact").toString());
                    String addressNow="";
                    addressNow= addressNow+" "+ document.get("Address.Street").toString();
                    addressNow= addressNow+" "+ document.get("Address.City").toString();
                    addressNow= addressNow+" "+ document.get("Address.State").toString();
                    addressNow= addressNow+" "+ document.get("Address.PIN").toString();
                    vCompAddress.setText(addressNow);

                    if (document != null) {
                        //Log.d(null, "DocumentSnapshot data: " + task.getResult().getData());
                    } else {
                        //Log.d(null, "User not found");
                    }
                } else {
                    //Log.d(null, "get failed with ", task.getException());
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
