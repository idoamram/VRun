package com.drukido.vrun.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drukido.vrun.R;
import com.drukido.vrun.entities.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    User mCurrUser;
    CircleImageView mCircleImageView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mCurrUser = (User) User.getCurrentUser();
        mCircleImageView =
                (CircleImageView) rootView.findViewById(R.id.profile_circularImageView_profilePicture);

        initializeProfilePicture();

        return rootView;
    }

    private void initializeProfilePicture(){
        try {
            if (mCurrUser.getProfilePhoto() != null) {
                mCurrUser.getPicassoProfilePhoto(mCircleImageView, getActivity());
            } else {
                mCurrUser.fetchInBackground(new GetCallback<User>() {
                    @Override
                    public void done(User user, ParseException e) {
                        if (e == null) {
                            mCurrUser = user;
                            mCurrUser.getPicassoProfilePhoto(mCircleImageView, getActivity());
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
