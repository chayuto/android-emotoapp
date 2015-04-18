package emotovate.com.emotoapp;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eMotoLogic.eMotoAds;



/**
 * create an instance of this fragment.
 */
public class manageAdsDetailsFragment extends Fragment {



    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "eMotoAds";


    private eMotoAds mAds;
    private OnAdsApproveSelectListener mListener;

    /**
     * Create new instance of manageAdsDetailsFragment
     *
     * @param ads
     * @return A new instance of fragment manageAdsDetailsFragment.
     */
    public static manageAdsDetailsFragment newInstance(eMotoAds ads) {
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
        return inflater.inflate(R.layout.fragment_manage_ads_details, container, false);
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


    public interface OnAdsApproveSelectListener {
        public void onAdsApproveSelect(eMotoAds Ads);
        public void onAdsUnapproveSelect(eMotoAds Ads);
    }


}
