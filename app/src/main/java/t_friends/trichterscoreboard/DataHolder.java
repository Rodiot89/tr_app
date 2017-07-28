package t_friends.trichterscoreboard;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Basti on 28.07.2017.
 */

public class DataHolder {
    private static ArrayList<TrichterPerson> APP_Data;
    private long date = Calendar.getInstance().getTimeInMillis();
    public static ArrayList<TrichterPerson> getData() {return APP_Data;}
    public static void setData(ArrayList<TrichterPerson> APP_Data) {
        DataHolder.APP_Data = APP_Data;}



}
