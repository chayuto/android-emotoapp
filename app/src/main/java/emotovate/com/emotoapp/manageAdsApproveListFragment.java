package emotovate.com.emotoapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import eMotoLogic.eMotoCell;

//TODO: setup ads view fragments
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link manageAdsApproveListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link manageAdsApproveListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class manageAdsApproveListFragment extends Fragment {

    private static final String ARG_PARAM1 = "eMotoCell";

    private eMotoCell mCell;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *

     * @return A new instance of fragment manageAdsApproveListFragment.
     */
    public static manageAdsApproveListFragment newInstance(eMotoCell myCell) {
        manageAdsApproveListFragment fragment = new manageAdsApproveListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1,myCell);
        fragment.setArguments(args);
        return fragment;
    }

    public manageAdsApproveListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCell = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_ads_approve_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
