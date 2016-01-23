package com.drukido.vrun.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.drukido.vrun.AsyncTasks.SubscribeGroupPushes;
import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Group;
import com.drukido.vrun.entities.User;
import com.drukido.vrun.interfaces.OnAsyncTaskFinishedListener;
import com.drukido.vrun.ui.fragments.GeneralFragment;
import com.drukido.vrun.ui.fragments.GroupFragment;
import com.drukido.vrun.ui.fragments.ProfileFragment;
import com.drukido.vrun.ui.fragments.RunFragment;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends AppCompatActivity
        implements RunFragment.OnFragmentInteractionListener,
        GeneralFragment.OnFragmentInteractionListener{

    private final int FRAGMENTS_COUNT = 3;
    private int[] tabIcons = {
            R.drawable.selector_tab_icon_group,
            R.drawable.selector_tab_icon_run,
            R.drawable.selector_tab_icon_user
    };

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private CoordinatorLayout mCoordinatorLayout;
    private Boolean mIsSubscribed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!mIsSubscribed){
            new SubscribeGroupPushes(this, new OnAsyncTaskFinishedListener() {
                @Override
                public void onSuccess(Object result) {
                    mIsSubscribed = true;
                }

                @Override
                public void onError(String errorMessage) {
                    mIsSubscribed = false;
                }
            }).execute();
        }

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainActivity_coordinateLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(FRAGMENTS_COUNT);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                User currUser = ((User) User.getCurrentUser());

                switch (position) {
                    case 0:
                        Group group = currUser.getGroup();
                        if (group != null) {
                            if (group.getName() != null) {
                                mToolbar.setTitle(group.getName());
                            } else {
                                mToolbar.setTitle("My group");
                                group.fetchInBackground(new GetCallback<Group>() {
                                    @Override
                                    public void done(Group fetchedGroup, ParseException e) {
                                        if (e == null) {
                                            mToolbar.setTitle(fetchedGroup.getName());
                                        }
                                    }
                                });
                            }
                        } else {
                            mToolbar.setTitle("My group");
                            currUser.fetchInBackground(new GetCallback<User>() {
                                @Override
                                public void done(User user, ParseException e) {
                                    if (e == null) {
                                        Group group = user.getGroup();
                                        if (group.getName() != null) {
                                            mToolbar.setTitle(group.getName());
                                        } else {
                                            mToolbar.setTitle("My group");
                                            group.fetchInBackground(new GetCallback<Group>() {
                                                @Override
                                                public void done(Group fetchedGroup, ParseException e) {
                                                    if (e == null) {
                                                        mToolbar.setTitle(fetchedGroup.getName());
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }

                        break;
                    case 1:
                        mToolbar.setTitle("Runs");
                        break;
                    case 2:
                        String userName = currUser.getName();
                        if (userName != null) {
                            mToolbar.setTitle(userName);
                        } else {
                            mToolbar.setTitle("Me");
                            User.getCurrentUser().fetchInBackground(new GetCallback<User>() {
                                @Override
                                public void done(User user, ParseException e) {
                                    if (e == null) {
                                        mToolbar.setTitle(user.getName());
                                    }
                                }
                            });
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupTabIcons();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        findViewById(R.id.mainActivity_btnSendFeedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SendFeedbackActivity.class);
                startActivity(i);
            }
        });
    }

    private void setupTabIcons() {
        try {
            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                mTabLayout.getTabAt(i).setIcon(tabIcons[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logout(){
        final ParseUser user = ParseUser.getCurrentUser();
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(MainActivity.this,
                            "Bye Bye " + user.getString("firstName") + " " +
                                    user.getString("lastName") + "!", Toast.LENGTH_LONG).show();

                    SharedPreferences prefs =
                            getSharedPreferences(Constants.VRUN_PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = prefs.edit();
                    prefsEditor.putBoolean(Constants.PREF_IS_USER_LOGGED_IN, false);
                    prefsEditor.apply();

                    MainActivity.this.finish();
                } else {
                    Snackbar.make(mCoordinatorLayout, "Sorry, something went wrong...",
                            Snackbar.LENGTH_LONG);
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onBackPressed() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Logout");
//        builder.setMessage("Are you sure ?");
//        builder.setCancelable(true);
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // if this button is clicked, close
//                // current activity
//                logout();
//            }
//        })
//                .setNegativeButton("No",new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog,int id) {
//                        // if this button is clicked, just close
//                        // the dialog box and do nothing
//                        dialog.cancel();
//                    }
//                });
//
//        // create alert dialog
//        AlertDialog alertDialog = builder.create();
//
//        // show it
//        alertDialog.show();
//    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new GroupFragment();
                case 1:
                    return new RunFragment();
                case 2:
                    return new ProfileFragment();
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return FRAGMENTS_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "Group";
//                case 1:
//                    return "Runs";
//                case 2:
//                    return "Me";
//            }
            return null;
        }


    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
}