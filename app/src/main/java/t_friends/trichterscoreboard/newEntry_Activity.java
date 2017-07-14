package t_friends.trichterscoreboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.Calendar;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class newEntry_Activity extends AppCompatActivity {

    static SQLiteDatabase db;

    EditText etPersonName;
    EditText etTime;
    EditText etEventName;
    Button btnAdd;
    Button btnGet;
    Button btnCancel;
    Button btnDelete;
    TextView tvSBEntry;
    String autofill_pn;     //Liste mit PersonNmen welche für das Autofill genutzt werden
    String autofill_en;     //Liste mit EventNamen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newentry_layout);

        // init views
        etPersonName = (EditText) findViewById(R.id.editText_InsertName);
        etTime = (EditText) findViewById(R.id.editText_InsertTime);
        etEventName = (EditText) findViewById(R.id.editText_InsertEvent);
        btnAdd = (Button) findViewById(R.id.button_add);
        btnGet = (Button) findViewById(R.id.button_get);
        btnCancel = (Button) findViewById(R.id.button_cancel);
        btnDelete = (Button) findViewById(R.id.button_delete);
        tvSBEntry = (TextView) findViewById(R.id.textView_ShowName);


        //setup database
        PracticeDatabaseHelper dbHelper = new PracticeDatabaseHelper(this);
        dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 2);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        //setup autofill
        //String[] autofill_pn = cupboard().withDatabase(db).query(TrichterRun.class).get();
        //getResources().getStringArray(R.array.list_of_countries);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,countries);
        //actv.setAdapter(adapter);

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
                    cupboard().withDatabase(db).put(tp);
                    // empty the edit text time
                    etTime.setText("");
                }else{
                    Toast.makeText(getApplicationContext(),"Name of Person must be given!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnGet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                TrichterPerson tr = cupboard().withDatabase(db).query(TrichterPerson.class).get();
                //TrichterRun tr = cupboard().withDatabase(db).query(TrichterRun.class).withSelection( "PersonName = ?", "Thomas").get();

                String pn =tr.getPersonName();
                if (!pn.isEmpty()) {
                    tvSBEntry.setText("The Name is: " +pn);
                }else{
                    Toast.makeText(getApplicationContext(),"No Name found!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                etPersonName.setText("");
                etTime.setText("");
                etEventName.setText("");
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                cupboard().withDatabase(db).delete(TrichterPerson.class,null);
            }
        });

    }
}
