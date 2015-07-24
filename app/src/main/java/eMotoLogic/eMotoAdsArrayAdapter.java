package eMotoLogic;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import emotovate.com.emotoapp.R;



/**
 * Created by chayut on 8/01/15.
 */


public class eMotoAdsArrayAdapter extends ArrayAdapter<eMotoAdsApprovalItem> {


    private final String TAG = "AdsArrayAdapter";

    // declaring our ArrayList of items
    private ArrayList<eMotoAdsApprovalItem> objects;
    private LayoutInflater inflater;
    ImageLoader imageLoader;
    DisplayImageOptions options;

    /* here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Item> objects,
    * because it is the list of objects we want to display.
    */
    public eMotoAdsArrayAdapter(Context context, int textViewResourceId, ArrayList<eMotoAdsApprovalItem> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;

        inflater = ((Activity)context).getLayoutInflater();

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
    }


    private static class ViewHolder {
        ImageView imageView;
        ImageView approveImageView;
        TextView textView;
        Bitmap bitmap;
        eMotoAdsApprovalItem ads;
    }


    /*
     * we are overriding the getView method here - this is what defines how each
     * list item will look.
     */
    public View getView(int position, View convertView, ViewGroup parent){



        ViewHolder viewholder = null;

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.adsview_item_row,null);
            viewholder = new ViewHolder();

            viewholder.imageView = (ImageView) convertView.findViewById(R.id.adsThumbnail);
            viewholder.textView = (TextView) convertView.findViewById(R.id.adsTitle);
            viewholder.approveImageView = (ImageView) convertView.findViewById(R.id.imageViewApprove);



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
        eMotoAdsApprovalItem i = objects.get(position);

        Log.d(TAG,"GetView: " + i.id());

        viewholder.imageView.setImageResource(R.drawable.em_logo_120);
        viewholder.textView.setText(i.description());
        viewholder.ads = i;

        viewholder.textView.setTextColor(0xff99cc33);
        viewholder.approveImageView.setImageResource(R.drawable.approved_icon);

        /*
        if(i.isApproved())
        {
            viewholder.textView.setTextColor(0xff99cc33);
            viewholder.approveImageView.setImageResource(R.drawable.approved_icon);
        }
        else
        {
            viewholder.textView.setTextColor(0xff000000);
            viewholder.approveImageView.setImageResource(R.drawable.unapproved_icon);
        }
        */

        //download and display image from url
        imageLoader.displayImage(i.getAdsThumbnailURLstr(), viewholder.imageView, options);



        //new ImageThumbnailDownloadTask().execute(viewholder);

        // the view must be returned to our activity
        return convertView;

    }

    /*
    private class ImageThumbnailDownloadTask extends AsyncTask<ViewHolder,Void,ViewHolder>
    {
        @Override
        protected ViewHolder doInBackground(ViewHolder... params) {

            ViewHolder viewHolder = params[0];

            //load image directly
            viewHolder.bitmap = mEMotoAssetLibrary.getThumbnail(viewHolder.ads);


            return viewHolder;
        }

        @Override
        protected void onPostExecute(ViewHolder result) {

            if (result.bitmap == null) {
                result.imageView.setImageResource(R.drawable.em_logo_120);
            } else {
                result.imageView.setImageBitmap(result.bitmap);
            }

            if(result.ads.isApproved())
            {
                result.textView.setTextColor(0xff99cc33);
                result.approveImageView.setImageResource(R.drawable.approved_icon);
            }
            else
            {
                result.textView.setTextColor(0xff000000);
                result.approveImageView.setImageResource(R.drawable.unapproved_icon);
            }
        }

    }
    */

}

