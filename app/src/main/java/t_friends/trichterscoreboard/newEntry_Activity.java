package t_friends.trichterscoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import static t_friends.trichterscoreboard.Scoreboard_Activity.GlobalData;
import static t_friends.trichterscoreboard.Scoreboard_Activity.SortList;


public class newEntry_Activity extends AppCompatActivity {


    Toolbar tbNewEntry;
    AutoCompleteTextView etPersonName;
    EditText etTime;
    AutoCompleteTextView etEventName;
    Button btnAdd;
    Button btnCancel;
    Boolean GlobalDataChanged;
    Boolean PersonAlreadyExits;
    HashSet<String> autofill_pn = new HashSet<>();      //AutoFillListe mit PersonNamen (ohne Duplikate)
    HashSet<String> autofill_en = new HashSet<>();           //AutoFillListe mit EventNamen (ohne Duplikate)



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newentry_layout);

        // init views
        tbNewEntry = (Toolbar) findViewById(R.id.toolbar_newEntry);
        setSupportActionBar(tbNewEntry);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etPersonName = (AutoCompleteTextView) findViewById(R.id.editText_InsertName);
        etTime = (EditText) findViewById(R.id.editText_InsertTime);
        etEventName = (AutoCompleteTextView) findViewById(R.id.editText_InsertEvent);
        btnAdd = (Button) findViewById(R.id.button_add);
        btnCancel = (Button) findViewById(R.id.button_cancel);

        // init variabels
        GlobalDataChanged = Boolean.FALSE;
        PersonAlreadyExits = Boolean.FALSE;


        //setup autofill
        autofill_pn=getUniquePersonNamesOfGlobalData();
        final ArrayAdapter<String> adapter_pn = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,autofill_pn.toArray(new String[autofill_pn.size()])); // HashSet --> ArrayList<String> : autofill_pn.toArray(new String[autofill_pn.size()])
        etPersonName.setAdapter(adapter_pn);

        autofill_en=getUniqueEventNamesOfSortList();
        final ArrayAdapter<String> adapter_en = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,autofill_en.toArray(new String[autofill_en.size()])); // HashSet --> ArrayList<String> : autofill_pn.toArray(new String[autofill_pn.size()])
        etEventName.setAdapter(adapter_en);


        // Set Layout Listener
        // **1** Toolbar



        // **2** Add Entry
        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                String pn = etPersonName.getText().toString();
                double t = Double.parseDouble(etTime.getText().toString());
                long d = Calendar.getInstance().getTimeInMillis();
                String en = etEventName.getText().toString();
                if (!(pn.isEmpty() || t==0)) {  // Testfeld darf nicht leer sein --> Toast
                    //Prüfen ob Person bereits vorhanden
                    for (TrichterPerson tp : GlobalData){
                        if (tp.getPersonName().equals(pn)){     // wenn PersonenName bereits vorhanden
                            tp.create_event(t,d,en);            // erstelle für diese Person neues Event
                            PersonAlreadyExits = Boolean.TRUE;
                            Toast.makeText(getApplicationContext(),"Notice: New Event added to an EXISTING Person!",Toast.LENGTH_SHORT).show();
                        }break;
                    }
                    if (PersonAlreadyExits == Boolean.FALSE) {      // wenn Person noch nicht existiert
                        TrichterPerson tp = new TrichterPerson(pn); // erstelle neue Person mit Event
                        tp.create_event(t, d, en);
                        GlobalData.add(tp);
                        Toast.makeText(getApplicationContext(),"Notice: New Event added to a NEW Person!",Toast.LENGTH_SHORT).show();
                    }
                    GlobalDataChanged = Boolean.TRUE;

                    Intent backToMain =new Intent(newEntry_Activity.this, Scoreboard_Activity.class);
                    backToMain.putExtra("GlobalDataChanged",GlobalDataChanged);
                    startActivity(backToMain);


                    // empty the edit text time
                    etTime.setText("");
                }else{
                    Toast.makeText(getApplicationContext(),"Name of Person must be given!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // **3** Cancel Entry
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                etPersonName.setText("");
                etTime.setText("");
                etEventName.setText("");
            }
        });

    }

    // Hilfsmethoden
    // **1**
    private  HashSet<String> getUniquePersonNamesOfGlobalData(){
        for (TrichterPerson tp : GlobalData){
            autofill_pn.add(tp.getPersonName());
        }
        return autofill_pn;
    }

    // **2**
    private HashSet<String> getUniqueEventNamesOfSortList(){
        for (TrichterPerson.TrichterEvent te : SortList){
            autofill_en.add(te.getEventName());
        }
        return autofill_en;
    }






}
//  Toast.makeText(getApplicationContext(),"No Name found!",Toast.LENGTH_SHORT).show();