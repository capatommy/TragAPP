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

public class ReserveActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Button btnPrenota, btnDate, btnCheck;
    private Button minusBtn, plusBtn;
    private String chosenDate;
    private TextView countText, numAd, numRid;
    private int counter;
    final Calendar c = Calendar.getInstance();
    final Calendar cRit = Calendar.getInstance();
    private long tsAnd, tsRit;
    FirebaseAuth nAuth = FirebaseAuth.getInstance();
    final FirebaseUser currentUser = nAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);


        final Spinner andataSpinner = findViewById(R.id.spinner_andata);
        final Spinner ritornoSpinner = findViewById(R.id.spinner_ritorno);
        final TextView dateText = findViewById(R.id.text_data);

        countText=findViewById(R.id.text_count);
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
        btnPrenota.setEnabled(false);
        btnPrenota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        btnCheck=findViewById(R.id.button_check);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check(andataSpinner, ritornoSpinner, dateText, counter);
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");


        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        long millis=System.currentTimeMillis();
        java.sql.Date dateToday=new java.sql.Date(millis);
        chosenDate = DateFormat.getDateInstance().format(c.getTime());

        try {
            if(sdformat.parse(chosenDate).compareTo(dateToday) < 0){
                Toast.makeText(ReserveActivity.this,"Seleziona una data valida",Toast.LENGTH_LONG);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
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


        //creo due percorsi, uno per utenti uno per supermercati
        final CollectionReference notebookRef = FirebaseFirestore.getInstance()
                .collection("utenti").document(currentUser.getEmail()).collection("Prenotazioni");


        notebookRef.document(currentUser.getEmail()+ data.getText())//metodo che controlla se esiste già una mia prenotazione per qeul supermercato in quel giorno, procede solo in caso di false
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(ReserveActivity.this, "Cancella prima la vecchia prenotazione", Toast.LENGTH_LONG).show();

                    } else {
                        notebookRef.add(new Prenotazione(andataOra, andataMin, ritornoOra, ritornoMin, chosenDate, tsAnd, tsRit, counter));

                        Log.e("a", c.toString());
                        finish();
                    }
                } else {

                }
            }
        });
    }

    private void check(Spinner andataSpinner, Spinner ritornoSpinner, TextView data, int counter){ //controlla prenotazioni
        final int andOra = Integer.parseInt(andataSpinner.getSelectedItem().toString().substring(0,2));
        final int andMinuti = Integer.parseInt(andataSpinner.getSelectedItem().toString().substring(3,5));
        final int ritOra = Integer.parseInt(ritornoSpinner.getSelectedItem().toString().substring(0,2));
        final int ritMinuti = Integer.parseInt(ritornoSpinner.getSelectedItem().toString().substring(3,5));
        final int[] count = {0};
        Calendar today = Calendar.getInstance();

        today.set(Calendar.HOUR_OF_DAY,0);

        if(data.getText().equals("Scegli data")){ //se il text data è quello di default crea toast e si stoppa
            Toast.makeText(ReserveActivity.this, "Inserisci data", Toast.LENGTH_LONG).show();
            return;
        }else if(data.getText().equals(today)){ //da correggere
            Toast.makeText(ReserveActivity.this, "Inserisci data posteriore a quella odierna", Toast.LENGTH_LONG).show();
            return;
        }

        if(counter==0){
            Toast.makeText(ReserveActivity.this, "Inserisci numero di persone", Toast.LENGTH_LONG).show();
            return;
        }

        if(andOra > ritOra){
            Toast.makeText(ReserveActivity.this, "Errore nella selezione dell'orario",Toast.LENGTH_LONG).show();
            return;
        }

        //aggiungo al calendario ora e minuti
        c.set(Calendar.HOUR, andOra);
        c.set(Calendar.MINUTE, andMinuti);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        tsAnd=c.getTimeInMillis()/1000;//creo variabile time stamp in base al calendario creato, divido per mille perche non conto i millisecondi

        cRit.set(Calendar.HOUR, ritOra);
        cRit.set(Calendar.MINUTE, ritMinuti);
        cRit.set(Calendar.SECOND, 0);
        cRit.set(Calendar.MILLISECOND, 0);
        tsRit=cRit.getTimeInMillis()/1000;



        FirebaseFirestore.getInstance()
                .collection("Prenotazioni")
                .whereEqualTo("tsAnd", tsAnd).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() { // vado nel percorso di firebase e guardo se ci sono documenti con il mio stesso timestamp
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments(); //creo una lista con ogni documento trovato con quel ts
                        for(DocumentSnapshot snapshot : list){ //per ogni documento della lista incremento contatore di uno
                            count[0] += 1;
                        }

                        if(count[0]<=5) {
                            countText.setTextColor(Color.GREEN);
                            countText.setText(String.format("Posti liberi: %s persone.", String.valueOf(count[0])));
                        }
                        else{
                            countText.setTextColor(Color.RED);
                            countText.setText(String.format("Barche piene: %s persone.", String.valueOf(count[0])));
                        }

                    }

                });

        btnPrenota.setEnabled(true);
        Log.e("tsAnd", String.valueOf(tsAnd));


    }
}
