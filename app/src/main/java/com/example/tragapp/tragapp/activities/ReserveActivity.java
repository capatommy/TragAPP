package com.example.tragapp.tragapp.activities;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.tragapp.R;
import com.example.tragapp.tragapp.entities.Prenotazione;
import com.example.tragapp.tragapp.fragments.DatePickerFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReserveActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Button btnPrenota, btnDate, btnCheck;
    private Button minusBtn, plusBtn;
    private String chosenDate;
    private TextView countText, numAd, numRid;
    private int counter;
    final Calendar c = Calendar.getInstance();
    final Calendar cRit = Calendar.getInstance();
    final Calendar cAnd = Calendar.getInstance();
    SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
    private long tsAnd, tsRit;
    FirebaseAuth nAuth = FirebaseAuth.getInstance();
    final FirebaseUser currentUser = nAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);



        final Spinner andataSpinner = findViewById(R.id.spinner_andata);
        final Spinner ritornoSpinner = findViewById(R.id.spinner_ritorno);
        final TextView dateText = findViewById(R.id.text_data);

        numAd = findViewById(R.id.NumberAdults);

        final ArrayAdapter<CharSequence> adapter4 = ArrayAdapter
                .createFromResource(this, R.array.andata, android.R.layout.simple_spinner_item);
        final ArrayAdapter<CharSequence> adapter5 = ArrayAdapter
                .createFromResource(this, R.array.ritorno, android.R.layout.simple_spinner_item);


        andataSpinner.setAdapter(adapter4);
        ritornoSpinner.setAdapter(adapter5);

        minusBtn =(Button) findViewById(R.id.minusBtn);
        plusBtn =(Button) findViewById(R.id.plusBtn);

        initCounter();

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minCounter();
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusCounter();
            }
        });




        btnPrenota=findViewById(R.id.btnPrenota);
        btnPrenota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check(andataSpinner, ritornoSpinner, dateText, counter))
                saveNote(andataSpinner, ritornoSpinner, dateText, counter);
            }
        });



        btnDate=findViewById(R.id.button_data);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();

                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        long millis = System.currentTimeMillis();

        chosenDate = sdformat.format(c.getTime());

        TextView dateText = findViewById(R.id.text_data);
        dateText.setText(chosenDate);
    }


    public void initCounter(){
        counter = 0;
        numAd.setText(String.valueOf(counter));
    }

    public void plusCounter(){
        counter++;
        numAd.setText(String.valueOf(counter));
    }

    public void minCounter(){
        if(counter==0) return;
            else counter--;
        numAd.setText(String.valueOf(counter));

    }

    private void saveNote(Spinner andataSpinner, Spinner ritornoSpinner, final TextView data, final int counter) {
        final int andataOra = Integer.parseInt(andataSpinner.getSelectedItem().toString().substring(0,2));
        Log.e("andataOra", String.valueOf(andataOra));
        final int andataMin = Integer.parseInt(andataSpinner.getSelectedItem().toString().substring(3,5));
        Log.e("andataMin", String.valueOf(andataMin));
        final int ritornoOra = Integer.parseInt(ritornoSpinner.getSelectedItem().toString().substring(0,2));
        Log.e("ritornoOra", String.valueOf(ritornoOra));
        final int ritornoMin = Integer.parseInt(ritornoSpinner.getSelectedItem().toString().substring(3,5));
        Log.e("ritornoMin", String.valueOf(ritornoMin));

        db.collection("Prenotazioni").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        int count = 0;

                        if(!queryDocumentSnapshots.isEmpty()){

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot d : list){

                                Prenotazione p = d.toObject(Prenotazione.class);
                                if(chosenDate.equals(p.getData()) && andataOra == p.getOraAndata() &&
                                        andataMin == p.getMinutiAndata() ){
                                    count += p.getNumAd();


                                }

                            }

                        }

                        if((count+counter)<=10){
                            Toast.makeText(ReserveActivity.this, "Ci sono ancora posti liberi, puoi prenotare!", Toast.LENGTH_LONG).show();
                            Log.d("count", String.valueOf(count));
                            db.collection("utenti").document(currentUser.getEmail()).collection("Prenotazioni")
                                    .add(new Prenotazione(andataOra, andataMin, ritornoOra, ritornoMin, chosenDate, tsAnd, tsRit, counter));

                            db.collection("Prenotazioni")
                                    .add(new Prenotazione(andataOra, andataMin, ritornoOra, ritornoMin, chosenDate, tsAnd, tsRit, counter,currentUser.getEmail()));

                            finish();

                        }else{
                            Toast.makeText(ReserveActivity.this, "Barche piene, seleziona un altro giorno", Toast.LENGTH_LONG).show();

                        }

                    }
                });

    }

    private boolean check(Spinner andataSpinner, Spinner ritornoSpinner, TextView data, int counter){ //controlla prenotazioni
        final int andOra = Integer.parseInt(andataSpinner.getSelectedItem().toString().substring(0,2));
        final int andMinuti = Integer.parseInt(andataSpinner.getSelectedItem().toString().substring(3,5));
        final int ritOra = Integer.parseInt(ritornoSpinner.getSelectedItem().toString().substring(0,2));
        final int ritMinuti = Integer.parseInt(ritornoSpinner.getSelectedItem().toString().substring(3,5));
        final int[] count = {0};


        SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar check = Calendar.getInstance(Locale.ITALY);
        String checkdate = sdformat.format(check.getTime());

        try {
            if(chosenDate==null){
                Toast.makeText(ReserveActivity.this,"Seleziona una data valida",Toast.LENGTH_LONG).show();
                return false;
            }
            if(sdformat.parse(chosenDate).before(sdformat.parse(checkdate))){
                Toast.makeText(ReserveActivity.this,"Seleziona una data valida",Toast.LENGTH_LONG).show();
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(data.getText().equals("Scegli data")) { //se il text data è quello di default crea toast e si stoppa
            Toast.makeText(ReserveActivity.this, "Inserisci data", Toast.LENGTH_LONG).show();
            return false;
        }

        if(counter==0){
            Toast.makeText(ReserveActivity.this, "Inserisci numero di persone", Toast.LENGTH_LONG).show();
            return false;
        }

        if(andOra > ritOra){
            Toast.makeText(ReserveActivity.this, "Errore nella selezione dell'orario",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;


    }


}
