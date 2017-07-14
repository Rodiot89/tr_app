package t_friends.trichterscoreboard;

//import static nl.qbusict.cupboard.CupboardFactory.cupboard;

import java.util.Calendar;

public class TrichterPerson{

    public long _id;        // for Scoreboard
    public String personName; // name of participant



    // Constructors
    public TrichterPerson(){
        this.personName = "noName";
    }
    public TrichterPerson(String personName){
        this.personName = personName;
    }
    public TrichterPerson(String personName, long _id){
        this.personName = personName;
        this._id = _id;
    }

    //Methods
    public void create_event(double time, long date, String eventName){
        TrichterEvent te =new TrichterEvent(time, date, eventName);
    }

    public String getPersonName() {return personName;}

    public void setPersonName(String personName) {this.personName=personName;}

    public long get_id() {return _id;}

    public void set_id(long _id) {this._id=_id;}

    // Inner Classes
     public class TrichterEvent{

        public double time;     // measured time for a Trichter run
        public String eventName;// name of event for clustering (optional)
        public long date;       // date of Trichter run

        // Constructors
        public TrichterEvent(){
            this.time = 0.0;
            this.date = Calendar.getInstance().getTimeInMillis();
            this.eventName = "noEvent";
        }
        public TrichterEvent( double time, long date, String eventName) {
            this.time = time;
            this.date = date;
            this.eventName = eventName;
        }

        // Mehtods
        public double getTime() {return time;}

        public long getDate() { return date;}

        public String getEventName() {return eventName;}


    }


}

/*public class TrichterRun {

    public Long _id;        // for Scoreboard
    public String personName; // name of participant
    public Double time;     // measured time for a Trichter run
    public String eventName;// name of event for clustering (optional)
    public Long date;       // date of Trichter run

    public TrichterRun(){
        this.personName = "noName";
        this.time = 0.0;
        this.date = Calendar.getInstance().getTimeInMillis();
        this.eventName = "noEvent";
    }

    public TrichterRun(String personName, Double time, Long date, String eventName){
        this.personName = personName;
        this.time = time;
        this.date = date;
        this.eventName = eventName;
    }

    public String getPersonName() {return personName;}

    public Double getTime() {return time;}

    public Long getDate() { return date;}

    public String getEventName() {return eventName;}

    public Long get_id() {return _id;}

    public void set_id(Long _id) {this._id=_id;}

    //static {
    //    cupboard().register(TrichterRun.class);
    //}

}
*/
