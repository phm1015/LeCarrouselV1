package com.lecarrousel.fragment;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.lecarrousel.R;
import com.lecarrousel.utils.SharedPrefs;


public class BaseFragment extends Fragment {
    private SharedPrefs mPrefs;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack) {

        mPrefs = new SharedPrefs(mContext);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        if(mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")){
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        }else{
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        }

        transaction.add(R.id.frgContainer, fragment);
        transaction.commit();
    }

    public void replaceFragmentReload(Fragment fragment, boolean addToBackStack) {

        mPrefs = new SharedPrefs(mContext);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        if(mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")){
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        }else{
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        }

        transaction.replace(R.id.frgContainer, fragment);
        transaction.commit();
    }

}