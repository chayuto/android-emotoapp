package eMotoLogic;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import emotovate.com.emotoapp.R;


/**
 * Created by chayut on 17/02/15.
 */
public class eMotoCellArrayAdapter extends ArrayAdapter<eMotoCell> {

    // declaring our ArrayList of items
    private ArrayList<eMotoCell> objects;
    private LayoutInflater inflater;

    /* here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Item> objects,
    * because it is the list of objects we want to display.
    */
    public eMotoCellArrayAdapter(Context context, int textViewResourceId, ArrayList<eMotoCell> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;

        inflater = ((Activity)context).getLayoutInflater();
    }


    private static class ViewHolder {
        TextView cellID;
        TextView cellName;
        TextView cellSerialNumber;
        eMotoCell mCell;
    }


    /*
     * we are overriding the getView method here - this is what defines how each
     * list item will look.
     */
    public View getView(int position, View convertView, ViewGroup parent){


        ViewHolder viewholder = null;

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.emotocell_item_row,null);
            viewholder = new ViewHolder();

            viewholder.cellID =  (TextView) convertView.findViewById(R.id.cellID);
            viewholder.cellName = (TextView) convertView.findViewById(R.id.cellName);
            viewholder.cellSerialNumber = (TextView) convertView.findViewById(R.id.cellSerialNumber);

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
        eMotoCell i = objects.get(position);

        viewholder.cellID.setText(i.deviceID);
        viewholder.cellName.setText(i.deviceName);
        viewholder.cellSerialNumber.setText(i.eMotocellSerialNo);

        // the view must be returned to our activity
        return convertView;

    }


}

