package com.example.mayankpadhi.demandbusiness;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class addProposal extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String[] SPINNERLIST = {"SELECT CATEGORY","Gas Station", "Cafe", "ATM", "Convenience Store", "Hotel", "Restaurant", "Clinic", "Movies", "Stationery", "Mall", "Barber"};

    EditText ipDescription;
    Button addPrp;
    Spinner catSpinner;
    String ipCategory, userEmail;
    String demandLatitude="";
    String demandLongitude="";
    Map<String, Object> newProposal = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser.getEmail() == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(addProposal.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Could not fetch login details. Please Login Again!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // logs out the user
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(addProposal.this, Login.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            alertDialog.show();
        }

        userEmail= currentUser.getEmail();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_proposal);
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        */
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(addProposal.this,
                android.R.layout.simple_dropdown_item_1line, SPINNERLIST){
            //Important these methods are overide for adding a placeholder, adding textcolor,
            // !!!!!!!!!!! Don't Change these ovverider methods.

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        Spinner materialDesignSpinner = (Spinner)
                findViewById(R.id.prodCat);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        materialDesignSpinner.setAdapter(arrayAdapter);

        Intent asd = getIntent();
        demandLatitude= asd.getStringExtra("demandLatitude");
        demandLongitude= asd.getStringExtra("demandLongitude");

        if(demandLatitude.equals("") || demandLongitude.equals("")) {
            AlertDialog alertDialog = new AlertDialog.Builder(addProposal.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Add Pin not placed! Please try again!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
        }

        ipDescription = (EditText)findViewById(R.id.description);
        catSpinner = (Spinner)findViewById(R.id.prodCat);
        addPrp = (Button)findViewById(R.id.submitAddDmd);

        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ipCategory = parent.getItemAtPosition(pos).toString();
                //Log.d("Category", ipCategory);
            }
            public void onNothingSelected(AdapterView<?> parent) {
                //Log.d("Category", "Nothing Selected");
            }
        });

        addPrp.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view)
                    {
                        if(ipDescription== null){
                            AlertDialog alertDialog = new AlertDialog.Builder(addProposal.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Empty Field(s)");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        } else if(ipCategory.equals("SELECT CATEGORY")) {
                            AlertDialog alertDialog = new AlertDialog.Builder(addProposal.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Category not Selected");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                        else {
                            addProposaldb();
                        }
                    }
                });
    }

    private void addProposaldb() {
        newProposal.put("Details", ipDescription.getText().toString());
        newProposal.put("Category", ipCategory);
        newProposal.put("Upvotes", 0);
        newProposal.put("Company Floated", userEmail);
        newProposal.put("Latitude", demandLatitude);
        newProposal.put("Longitude", demandLongitude);
        newProposal.put("Timestamp", FieldValue.serverTimestamp());


        addToProposalCollection();

    }

    private void addToProposalCollection() {
        //String demandID= "yo";
        db.collection("proposals")//.document(ourVariableReqID)				//HAS TO be UNIQUE
                .add(newProposal)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Log.d("Proposal to DB", "DocumentSnapshot successfully added!");
                        getProposal(documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w("Proposal To DB Failed", "Error adding document", e);
                    }
                });
        //return demandID;
    }

    private void createReferenceToCompany(String proposalID) {
        Map<String, Object> tryProposal = new HashMap<>();
        tryProposal.put("ProposalID", proposalID);
        tryProposal.put("Upvotes", 0);
        tryProposal.put("Category", ipCategory);
        tryProposal.put("Timestamp", FieldValue.serverTimestamp());

        //Log.d("See here", proposalID);
        DocumentReference requestRef = db.collection("company").document(userEmail)
                .collection("proposals").document(proposalID);
        requestRef.set(tryProposal)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d("Link to Company", "DocumentSnapshot successfully written!");
                        Intent intent = new Intent(addProposal.this, MapsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Link to Company Failed", "Error writing document", e);
                    }
                });

    }


    private void getProposal(String id) {

        createReferenceToCompany(id);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(addProposal.this, MapsActivity.class);
        startActivity(intent);
        finish();
    }
}