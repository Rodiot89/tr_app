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

import static t_friends.trichterscoreboard.Scoreboard_Activity.GlobalData;


public class newEntry_Activity extends AppCompatActivity {


    Toolbar tbNewEntry;
    AutoCompleteTextView etPersonName;
    EditText etTime;
    EditText etEventName;
    Button btnAdd;
    Button btnCancel;
    Boolean GlobalDataChanged;
    String autofill_pn;     //Liste mit PersonNmen welche für das Autofill genutzt werden
    String autofill_en;     //Liste mit EventNamen

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
        etEventName = (EditText) findViewById(R.id.editText_InsertEvent);
        btnAdd = (Button) findViewById(R.id.button_add);
        btnCancel = (Button) findViewById(R.id.button_cancel);

        // init variabels
        GlobalDataChanged = Boolean.FALSE;
        //TrichterPerson tpp = GlobalData.get(GlobalData.size()-1);
        //etPersonName.setText(tpp.getPersonName());


        //setup autofill
        int ii = GlobalData.size();
        String[] autofill_pn = new String[ii];
        int i=0;
        for (TrichterPerson tp : GlobalData){
            autofill_pn[i]=tp.getPersonName();
            i++;
        }
        Log.d("tag", "newEntry before ArrayAdapter");
        //getResources().getStringArray(R.array.list_of_countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,autofill_pn);
        etPersonName.setAdapter(adapter);

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
                if (!(pn.isEmpty() || t==0)) {
                    //if()
                    TrichterPerson tp = new TrichterPerson(pn);
                    tp.create_event(t, d, en);
                    GlobalData.add(tp);
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
}
//  Toast.makeText(getApplicationContext(),"No Name found!",Toast.LENGTH_SHORT).show();