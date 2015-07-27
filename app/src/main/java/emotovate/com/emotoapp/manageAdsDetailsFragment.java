package emotovate.com.emotoapp;


import android.app.Activity;
import android.content.Intent;
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

import eMotoLogic.eMotoAds;
import eMotoLogic.eMotoAdsApprovalItem;


/**
 * create an instance of this fragment.
 */
public class manageAdsDetailsFragment extends Fragment implements View.OnClickListener{


    //debug
    private static String TAG = "manageAdsDetailsFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "eMotoAdsApprovalItem";


    private eMotoAdsApprovalItem mAds;
    private OnAdsApproveSelectListener mListener;
    private ImageView ivAdsImage;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
            .cacheOnDisc(true).resetViewBeforeLoading(false).build();


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

    public manageAdsDetailsFragment() {
        // Required empty public constructor
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

        Button btnAccept = (Button) view.findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(this);
        TextView tvAdsID = (TextView)  view.findViewById(R.id.adsIDTextview);
        TextView tvAdsDescription = (TextView)  view.findViewById(R.id.adsDescriptionTextview);
        ivAdsImage = (ImageView)  view.findViewById(R.id.AdsImageView);

        tvAdsID.setText(mAds.id());
        tvAdsDescription.setText(mAds.description());

        //TODO:Update Logic
        //if(mAds.isApproved()){
        if(true){
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
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.btnAccept:

                //TODO:update date status
                if(false){
                    mListener.onAdsUnapproveSelect(mAds);
                }
                else
                {
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
