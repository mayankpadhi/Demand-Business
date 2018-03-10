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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class DemandDetails extends AppCompatActivity {
    private FirebaseFirestore db;

    TextView vCategory, vDescription, vUpvotes;
    String usrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.deman_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Demand Details");


        Intent asd = getIntent();
        final String requestID= asd.getStringExtra("demandID");

        if(requestID.equals("")) {
            AlertDialog alertDialog = new AlertDialog.Builder(DemandDetails.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Could not fetch login details. Please Login Again!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
        }

        vCategory = (TextView)findViewById(R.id.dispDetCat);
        vDescription = (TextView)findViewById(R.id.dispDetDesc);
        vUpvotes = (TextView)findViewById(R.id.dispDetUpvotes);

        DocumentReference docRef = db.collection("requests").document(requestID);			//PROPNAME :- need to fetch from onclick()
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    vCategory.setText(document.get("Category").toString());
                    vDescription.setText(document.get("Details").toString());
                    usrid= document.get("User Floated").toString();
                    vUpvotes.setText(document.get("Upvotes").toString());
                    if (document != null) {
                        //Log.d(null, "DocumentSnapshot data: " + task.getResult().getData());
                    } else {
                        //Log.d(null, "No such Proposal");
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
