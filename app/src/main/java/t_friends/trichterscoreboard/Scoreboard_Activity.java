package t_friends.trichterscoreboard;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class Scoreboard_Activity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // Definition der Variablen
    public static ArrayList<TrichterPerson> GlobalData;
    public static ArrayList<TrichterPerson.TrichterEvent> SortList = new ArrayList<>();
    final CustomArrayAdapter adapter = new CustomArrayAdapter(this, SortList);
    public long date = Calendar.getInstance().getTimeInMillis();
    public int[] sortStatus = new int[] {0,0,0,0,0}; // Status der Sortierung: 0: zuvor andere Sortierung; 1: zuvor gleiche Sortierung; {xx,name,time,date,event}


    // Definition der Layoutfelder
    Toolbar tbMain;
    SearchView searchView;
    TextView header_rank;
    AppCompatImageButton header_sortArrow;
    Button header_name;
    Button header_time;
    Button header_date;
    Button header_event;
    ListView listview;
    FloatingActionButton sw_to_newEntry_Activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("tag", "hallo");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoreboard_layout);

        // Initial of layout fields
        tbMain = (Toolbar) findViewById(R.id.toolbar_main);
        //searchView.setOnQueryTextListener(this);
        setSupportActionBar(tbMain);
        header_rank = (TextView) findViewById(R.id.header_rank);
        header_sortArrow = (AppCompatImageButton) findViewById(R.id.header_sortArrow);
        header_name = (Button) findViewById(R.id.header_name);
        header_time = (Button) findViewById(R.id.header_time);
        header_date = (Button) findViewById(R.id.header_date);
        header_event = (Button) findViewById(R.id.header_event);
        listview = (ListView) findViewById(R.id.listview_frame);
        sw_to_newEntry_Activity = (FloatingActionButton) findViewById(R.id.switchToNewEntryActivity);

        // Initial fill of Scoreboard
        buildGlobalDataList();
        buildSortList_initial();
        //final CustomArrayAdapter adapter = new CustomArrayAdapter(this, SortList);
        listview.setAdapter(adapter);

        // Switch Activity
       final Bundle intentFromNewEntry = getIntent().getExtras();
        if (intentFromNewEntry != null && intentFromNewEntry.getBoolean("GlobalDataChanged")){
            //GlobalData = (ArrayList<TrichterPerson>) getIntent().getSerializableExtra("GlobalData");
            adapter.notifyDataSetChanged();
            Log.d("tag", "adapter notify after intent");
        }



        // Set Layout Listener
        // **1** Name
        header_name.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                sortStatus[2]=0; sortStatus[3]=0; sortStatus[4]=0;
                if (sortStatus[1]==0) {
                    Collections.sort(SortList, new BeanComparator("time")); //event werden nach time sortiert
                    Collections.sort(SortList, new BeanComparator("ParentPersonName")); // anschließend nur noch nach Namen (Zeit wurde schon)
                    sortStatus[1] = 1;  //Anzeige das Sortierung ausgeführt wurde
                }
                else if(sortStatus[1]==1) {             // wenn unmittelbar zuvor eine Sortierung nach Name ausgeführt wurde --> Liste invertieren (Wechsel aus-/abwärts sortiert)
                    Collections.reverse(SortList);
                    float deg = (header_sortArrow.getRotation() == 180F) ? 0F : 180F;
                    header_sortArrow.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
                }
                adapter.notifyDataSetChanged();
            }
        });

        // **2** Time
        header_time.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                sortStatus[1]=0; sortStatus[3]=0; sortStatus[4]=0;
                if (sortStatus[2]==0) {
                    Collections.sort(SortList, new BeanComparator("time"));
                    sortStatus[2] = 1;  //Anzeige das Sortierung ausgeführt wurde
                }
                else if(sortStatus[2]==1){
                    Collections.reverse(SortList);
                    float deg = (header_sortArrow.getRotation() == 180F) ? 0F : 180F;
                    header_sortArrow.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
                }
                adapter.notifyDataSetChanged();
            }
        });
        // **3** Date
        header_date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                sortStatus[1]=0; sortStatus[2]=0; sortStatus[4]=0;
                if (sortStatus[3]==0) {
                    Collections.sort(SortList, new BeanComparator("date"));
                    sortStatus[3] = 1;  //Anzeige das Sortierung ausgeführt wurde
                }
                else if(sortStatus[3]==1){
                    Collections.reverse(SortList);
                    float deg = (header_sortArrow.getRotation() == 180F) ? 0F : 180F;
                    header_sortArrow.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
                }
                adapter.notifyDataSetChanged();
            }
        });
        // **4** Event
        header_event.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                sortStatus[1]=0; sortStatus[2]=0; sortStatus[3]=0;
                if (sortStatus[4]==0) {
                    Collections.sort(SortList, new BeanComparator("eventName"));
                    sortStatus[4] = 1;  //Anzeige das Sortierung ausgeführt wurde
                }
                else if(sortStatus[4]==1){
                    Collections.reverse(SortList);
                    float deg = (header_sortArrow.getRotation() == 180F) ? 0F : 180F;
                    header_sortArrow.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
                }
                adapter.notifyDataSetChanged();
            }
        });

        // **5** Button new Entry
        sw_to_newEntry_Activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Scoreboard_Activity.this, newEntry_Activity.class));   // set Intent to switch to new entry
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1_layout, menu);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search_menu1));
        return super.onCreateOptionsMenu(menu);
    }


    // Methoden
    // **1**
    public void buildGlobalDataList() {
        if (GlobalData != null) {
            Log.d("tag", "GlobalData!=null");
        } else {
            GlobalData = new ArrayList<>();
            //check ob GlobalData geladen werden kann
            //to do

            //wenn kein GlobalData vorhanden, erstelle eine neue
            TrichterPerson tp1 = new TrichterPerson("test1");
            tp1.create_event(0.0, date, "noEvent1");
            tp1.create_event(0.1, date, "noEvent2");
            TrichterPerson tp2 = new TrichterPerson("test2");

            tp2.create_event(0.0, date, "noEvent4");
            tp2.create_event(0.2, date, "noEvent5");
            tp2.create_event(0.3, date, "spät");
            tp2.create_event(0.3, date, "spät");
            tp2.create_event(0.3, date, "spät");
            tp2.create_event(0.3, date, "spät");
            tp2.create_event(0.3, date, "spät");
            tp2.create_event(0.3, date, "spät");
            tp2.create_event(0.3, date, "spät88");
            tp2.create_event(0.3, date, "spät");

            GlobalData.add(tp1);
            GlobalData.add(tp2);
            Log.d("tag", "GlobalData anlegen");
        }
    }

    // **2**
    public void buildSortList_initial(){
        SortList.clear();
        for (TrichterPerson tp : GlobalData) {
            for (TrichterPerson.TrichterEvent ev : tp.eventlist) {
                SortList.add(ev);
            }
        }
        // Printausgabe
        for (TrichterPerson.TrichterEvent ev : SortList) {
            //System.out.println(ev.getTime());
            System.out.println(ev.getFullTrichterEventAsString());
        }
        Log.d("tag", "buildSortlist_initial");
    }

    // **3**




    // Hilfklassen
    //**1**


    // **2**
  /*  private static class  BeanComparator implements Comparator<Object> {

        private String getter;

        private BeanComparator(String field) {
            this.getter = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
        }

        public int compare(Object o1, Object o2) {
            try {
                if (o1 != null && o2 != null) {
                    o1 = o1.getClass().getMethod(getter, new Class[0]).invoke(o1, new Object[0]);
                    o2 = o2.getClass().getMethod(getter, new Class[0]).invoke(o2, new Object[0]);
                }
            } catch (Exception e) {
                // If this exception occurs, then it is usually a fault of the developer.
                throw new RuntimeException("Cannot compare " + o1 + " with " + o2 + " on " + getter, e);
            }

            return (o1 == null) ? -1 : ((o2 == null) ? 1 : ((Comparable<Object>) o1).compareTo(o2));
        }

    }*/
}

   /*
    public HashMap<String, String[]> AdaptedMap;
    public ArrayList<HashMap<String, String[]>> AdaptedList;
    public String[] list_of_number = {"1", "2"};
    public String[] list_of_personName = {"Juergen", "Thomas"};
    public String[] list_of_time = {"2.991", "14.988"};
    public String[] list_of_date = {"01.Mai.2017", "01.01.1900"};
    public String[] list_of_eventName = {"Ostern", "Mutz"};

   private HashMap<String,String[]> buildAdaptedMap() {
        HashMap<String, String[]> myMap = new HashMap<>();
        myMap.put("number", list_of_number);
        myMap.put("personName", list_of_personName);
        myMap.put("time", list_of_time);
        myMap.put("date", list_of_date);
        myMap.put("eventName", list_of_eventName);
        return myMap;
    }

    public ArrayList<HashMap<String, String[]>> buildListViewArrayList() {
        ArrayList<HashMap<String, String[]>> list = new ArrayList<>();
        AdaptedMap = buildAdaptedMap();
        list.add(AdaptedMap);
        return list;
    }
*/


/*
public class Scoreboard_Activity extends AppCompatActivity {

    public LinkedList<TrichterPerson> GlobalData;
    public HashMap<String, String[]> AdaptedMap;
    public ArrayList<HashMap<String, String[]>> AdaptedList;
    public String[] list_of_number = {"1","2"};
    public String[] list_of_personName = {"Juergen","Thomas"};
    public String[] list_of_time = {"2.991","14.988"};
    public String[] list_of_date = {"01.Mai.2017","01.01.1900"};
    public String[] list_of_eventName= {"Ostern","Mutz"};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoreboard_layout);

        ListView listview = (ListView) findViewById(R.id.listview_frame);

        buildGlobalDataList();
        buildInitialAdaptedLists();
        buildListViewArrayList();

        ArrayAdapter<ArrayList> adapter = new CustomArrayAdapter(this, AdaptedList);
        listview.setAdapter(adapter);

    }


    public void buildGlobalDataList() {
        //check ob GlobalData geladen werden kann
        //to do

        //wenn kein GlobalData vorhanden, erstelle eine neue
        TrichterPerson tp1 = new TrichterPerson("test1");
        tp1.create_event();
        TrichterPerson tp2 = new TrichterPerson("test2");
        tp2.create_event();

        GlobalData.add(tp1);
        GlobalData.add(tp2);
    }

    public void buildInitialAdaptedLists(){
        String[] list_of_number = {"1","2"};
        String[] list_of_personName = {"Juergen","Thomas"};
        String[] list_of_time = {"2.991","14.988"};
        String[] list_of_date = {"01.Mai.2017","01.01.1900"};
        String[] list_of_eventName = {"Ostern","Mutz"};
    }

    private HashMap<String,String[]> buildAdaptedMap() {
        HashMap<String, String[]> myMap = new HashMap<>();
        myMap.put("number", list_of_number);
        myMap.put("personName", list_of_personName);
        myMap.put("time", list_of_time);
        myMap.put("date", list_of_date);
        myMap.put("eventName", list_of_eventName);
        return myMap;
    }

    public ArrayList<HashMap<String, String[]>> buildListViewArrayList() {
        ArrayList<HashMap<String, String[]>> list = new ArrayList<>();
        AdaptedMap = buildAdaptedMap();
        list.add(AdaptedMap);
        return list;
    }


}

 */




/*public class Scoreboard_Activity extends AppCompatActivity {

    //Time today = new Time(Time.getCurrentTimezone());
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoreboard_layout);

        //setup database
        DBHelper myDBH = new DBHelper(this);
        SQLiteDatabase db = myDBH.getWritableDatabase();


        // sample for Trichterperson
        TrichterPerson tp1 = new TrichterPerson();
        tp1.create_event();
        cupboard().withDatabase(db).put(tp1);

        TrichterPerson tp2 = new TrichterPerson();
        tp2.create_event();
        cupboard().withDatabase(db).put(tp2);

        TrichterPerson tp3 = new TrichterPerson();
        tp3.create_event();
        cupboard().withDatabase(db).put(tp3);

        populateListView();
    }

    private void populateListView() {
        Cursor cursor = cupboard().withDatabase(db).query(TrichterPerson.class).getCursor();
        TrichterPerson tr = cupboard().withDatabase(db).query(TrichterPerson.class).get();
        String[] fromDB = new String[] {tr.personName};
        int[] toViewIDs = new int[] {R.id.listView_personName};
        SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(getBaseContext(),R.layout.listview_layout,cursor,fromDB,toViewIDs,0);
        ListView myList = (ListView) findViewById(R.id.listview_frame);
        myList.setAdapter(myCursorAdapter);
    }

}



*/

