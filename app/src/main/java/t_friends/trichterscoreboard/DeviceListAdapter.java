package t_friends.trichterscoreboard;

        import android.bluetooth.BluetoothDevice;
        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.TextView;

        import java.util.ArrayList;


public class DeviceListAdapter extends ArrayAdapter<String> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<String> mDevices;
    private int  mViewResourceId;

    public DeviceListAdapter(Context context, int tvResourceId, ArrayList<String> devices){
        super(context, tvResourceId,devices);
        this.mDevices = devices;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        String device = mDevices.get(position);

        if (device != null) {
            TextView deviceName = (TextView) convertView.findViewById(R.id.tvDeviceName);
            //TextView deviceAdress = (TextView) convertView.findViewById(R.id.tvDeviceAddress);

            if (deviceName != null) {
                deviceName.setText(device);
            }
        }

        return convertView;
    }

}