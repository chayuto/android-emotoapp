package eMotoLogic;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import emotovate.com.emotoapp.R;

/**
 * Created by chayut on 18/08/15.
 */
public class ssidArrayAdapter  extends ArrayAdapter<ScanResult> {

    // declaring our ArrayList of items
    private ArrayList<ScanResult> objects;
    private LayoutInflater inflater;

    public ssidArrayAdapter(Context context, int textViewResourceId, ArrayList<ScanResult> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;

        inflater = ((Activity)context).getLayoutInflater();

    }

    private static class ViewHolder {
        TextView tvSSID;
        TextView tvSecurity;
    }

    /*
     * we are overriding the getView method here - this is what defines how each
     * list item will look.
     */
    public View getView(int position, View convertView, ViewGroup parent){



        ViewHolder viewholder = null;

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.wifi_ssid_cell,null);
            viewholder = new ViewHolder();

            viewholder.tvSSID = (TextView) convertView.findViewById(R.id.tvSSID);
            viewholder.tvSecurity = (TextView) convertView.findViewById(R.id.tvSecurity);

            convertView.setTag(viewholder);
        }

        viewholder = (ViewHolder)convertView.getTag();
        /*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        ScanResult i = objects.get(position);

        viewholder.tvSSID.setText(i.SSID);
        viewholder.tvSecurity.setText(i.capabilities);


        // the view must be returned to our activity
        return convertView;

    }

}
