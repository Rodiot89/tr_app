package t_friends.trichterscoreboard;

//import static nl.qbusict.cupboard.CupboardFactory.cupboard;

import android.media.Image;
import android.text.format.DateFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

public class TrichterPerson implements Serializable {

    public long _id;        // for Scoreboard
    public String personName; // name of participant
    public LinkedList<TrichterPerson.TrichterEvent> eventlist; //list of TrichterEvents for each person
    //public Image personImage; // optional image of participant



    // Constructors
    public TrichterPerson(String personName){
        this.personName = personName;
        this. eventlist = new LinkedList<>();
    }

    //Methods
    // **1**
    public void create_event(double time, long date, String eventName){
        TrichterPerson.TrichterEvent te = this.new TrichterEvent(time, date, eventName);
        eventlist.add(te);
    }
    // **2**
    public String getPersonName() {return personName;}
    // **3**
    public void setPersonName(String personName) {this.personName=personName;}
    // **4**
    public long get_id() {return _id;}
    // **5**
    public void set_id(long _id) {this._id=_id;}
    // **6**
    public int getNumberOfTrichterEvents(){
        return this.eventlist.size();
    }

    // Inner Classes
     public class TrichterEvent {

        public double time;     // measured time for a Trichter run
        private long date;       // date of Trichter run
        private String eventName;// name of event for clustering (optional)
        private String FullString;  // Full String of TrichterEvent


        // Constructors
       /* public TrichterEvent(){
            this.time = 0.0;
            this.date = Calendar.getInstance().getTimeInMillis();
            this.eventName = "noEvent";
        }*/
        public TrichterEvent( double time, long date, String eventName) {
            this.time = time;
            this.date = date;
            this.eventName = eventName;
        }

        // Methods
        // **1**
        public String getParentPersonName() {return TrichterPerson.this.getPersonName();}
        // **2**
        public double getTime() {return time;}
        // **3**
        public long getDate() { return date;}
        // **4**
        public String getEventName() {return eventName;}
        // **5**
        public String getFullTrichterEventAsString(){
            String name = getParentPersonName();
            double time = getTime();
            String date = DateFormat.format("yyyy-MM-dd hh:mm:ss a", getDate()).toString();
            //String date = DateFormat.format("dd.MM.yyyy", getDate()).toString();
            String event = getEventName();

            FullString = name + " trank in " + time + " s am " + date + " (" + event +").";
            return FullString;
        }

    }
}
