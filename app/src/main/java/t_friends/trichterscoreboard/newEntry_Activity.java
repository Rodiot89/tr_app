package t_friends.trichterscoreboard;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
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
import java.util.Collections;
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
    Button btnClear;
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
        btnClear = (Button) findViewById(R.id.button_clear);

        // init variabels
        GlobalDataChanged = Boolean.FALSE;
        PersonAlreadyExits = Boolean.FALSE;







        // Set Layout Listener
        // **1** Toolbar


        // **2** Auto Complete PersonName
        autofill_pn=getUniquePersonNamesOfGlobalData(); // holt unique PersonName aus der Globaldata
        final ArrayAdapter<String> adapter_pn = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,autofill_pn.toArray(new String[autofill_pn.size()])); // HashSet --> ArrayList<String> : autofill_pn.toArray(new String[autofill_pn.size()])
        etPersonName.setAdapter(adapter_pn);

        etPersonName.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(final View view, boolean b) {  // Dropdown menu mit auto suggestion bereits beim ersten Klick auf Feld
                etPersonName.showDropDown();
            }
        });
        etPersonName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {    // Dropdown menu mit auto suggestion immer bei klicken auf Feld
                etPersonName.showDropDown();
            }
        });

        etPersonName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                //ColorDrawable drawable = (ColorDrawable) etPersonName.getBackground();
                for (String pn : autofill_pn){      // prüft ob eingegebener text als Name im GlobalData existiert
                    if (pn.equals(text.toString())) {
                        etPersonName.setBackgroundColor(Color.GREEN);  // setzt Farbe neu wenn True
                        break;
                    }
                    else{
                        etPersonName.setBackgroundColor(Color.TRANSPARENT);  // andernfalls return to transparent
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        // **3** Auto Complete EventName
        autofill_en=getUniqueEventNamesOfSortList();
        final ArrayAdapter<String> adapter_en = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,autofill_en.toArray(new String[autofill_en.size()])); // HashSet --> ArrayList<String> : autofill_pn.toArray(new String[autofill_pn.size()])
        etEventName.setAdapter(adapter_en);

        etEventName.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(final View view, boolean b) {
                etEventName.showDropDown();
            }
        });
        etEventName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                etEventName.showDropDown();
            }
        });

        etEventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                //ColorDrawable drawable = (ColorDrawable) etPersonName.getBackground();
                for (String en : autofill_en){      // prüft ob eingegebener text als Name im GlobalData existiert
                    if (en.equals(text.toString())) {
                        etEventName.setBackgroundColor(Color.GREEN);  // setzt Farbe neu wenn True
                        break;
                    }
                    else{
                        etEventName.setBackgroundColor(Color.TRANSPARENT);  // andernfalls return to transparent
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });




        // **4** Add Entry
        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                String pn = etPersonName.getText().toString().trim();
                double t = Double.parseDouble(etTime.getText().toString());
                long d = Calendar.getInstance().getTimeInMillis();
                String en = etEventName.getText().toString().trim();
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
                    Toast.makeText(getApplicationContext(),"Name of Person and Time must be given!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // **5** Clear Entry
        btnClear.setOnClickListener(new View.OnClickListener(){
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
        Collections.sort(SortList, new BeanComparator("date"));
        Collections.reverse(SortList);
        for (TrichterPerson.TrichterEvent te :SortList) {
            autofill_pn.add(te.getParentPersonName());
        }
        return autofill_pn;
    }

    // **2**
    private HashSet<String> getUniqueEventNamesOfSortList(){
        Collections.sort(SortList, new BeanComparator("date"));
        Collections.reverse(SortList);
        for (TrichterPerson.TrichterEvent te : SortList){
            autofill_en.add(te.getEventName());
        }
        return autofill_en;
    }






}
//  Toast.makeText(getApplicationContext(),"No Name found!",Toast.LENGTH_SHORT).show();