package com.diff.provider.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diff.provider.R;

/**
 * Created by Tranxit Technologies Pvt Ltd, Chennai
 */

public class WelcomeScreen3 extends Fragment {

    View rootView;

    public WelcomeScreen3() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.welcome_slide3, container, false);
//        Animation a1= AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left);
//        rootView.setAnimation(a1);
        return rootView;
    }


}
