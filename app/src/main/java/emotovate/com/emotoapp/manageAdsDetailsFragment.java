package emotovate.com.emotoapp;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import eMotoLogic.eMotoAdsApprovalItem;


/**
 * create an instance of this fragment.
 */
public class manageAdsDetailsFragment extends Fragment implements View.OnClickListener{


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "eMotoAdsApprovalItem";
    //debug
    private static String TAG = "manageAdsDetailsFrag";
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
            .cacheOnDisc(true).resetViewBeforeLoading(false).build();
    private eMotoAdsApprovalItem mAds;
    private OnAdsApproveSelectListener mListener;
    private ImageView ivAdsImage;


    public manageAdsDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Create new instance of manageAdsDetailsFragment
     *
     * @param ads
     * @return A new instance of fragment manageAdsDetailsFragment.
     */
    public static manageAdsDetailsFragment newInstance(eMotoAdsApprovalItem ads) {
        manageAdsDetailsFragment fragment = new manageAdsDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1,ads);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAds = getArguments().getParcelable(ARG_PARAM1);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_ads_details, container, false);

        Button btnAccept = (Button) view.findViewById(R.id.btnApproveAds);
        btnAccept.setOnClickListener(this);
        TextView tvAdsID = (TextView)  view.findViewById(R.id.adsIDTextview);
        TextView tvAdsDescription = (TextView)  view.findViewById(R.id.adsDescriptionTextview);
        ivAdsImage = (ImageView)  view.findViewById(R.id.AdsImageView);

        tvAdsID.setText(mAds.id());
        tvAdsDescription.setText(mAds.description());

        Log.d(TAG, mAds.id());
        Log.d(TAG, mAds.description());
        Log.d(TAG, "Is Approved:" + mAds.isApprovedStr());

        //if(mAds.isApproved()){
        if(mAds.isApproved()){
            btnAccept.setText("Unapprove");
        }
        else
        {
            btnAccept.setText("Approve");
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated( view,savedInstanceState);
        //download and display image from url
        imageLoader.displayImage(mAds.getAdsThumbnailURLstr(), ivAdsImage, options);
        imageLoader.displayImage(mAds.getAdsImageURLstr(), ivAdsImage, options);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAdsApproveSelectListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick()");

        switch (v.getId()) {
            case R.id.btnApproveAds:
                Log.d(TAG,"btnApproveAds Clicked");
                if(mAds.isApproved()){
                    Log.d(TAG,"Request unapprove:" + mAds.id());
                    mListener.onAdsUnapproveSelect(mAds);
                }
                else
                {
                    Log.d(TAG, "Request approve:" + mAds.id());
                    mListener.onAdsApproveSelect(mAds);
                }
                break;
        }
    }


    public interface OnAdsApproveSelectListener {
        void onAdsApproveSelect(eMotoAdsApprovalItem Ads);
        void onAdsUnapproveSelect(eMotoAdsApprovalItem  Ads);
    }


}
