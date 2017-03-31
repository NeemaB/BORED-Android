package cpen391.team6.bored.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import cpen391.team6.bored.Fragments.CreateNoteFragment;
import cpen391.team6.bored.Fragments.CourseNotesFragment;
import cpen391.team6.bored.Fragments.MyNotesFragment;
import cpen391.team6.bored.R;
import cpen391.team6.bored.Utility.UI_Util;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "MainActivity";

    private Fragment mCurrentFragment;
    private String[] mDrawerTitles;
    private int mCurrentPosition;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private Menu mMenu;

    private static final int COURSE_NOTES_POSITION = 0;
    private static final int MY_NOTES_POSITION = 1;
    private static final int CREATE_NOTE_POSITION = 2;
    private static final int SETTINGS_POSITION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        UI_Util.setStatusBarColor(getWindow(), R.color.colorPrimary);

        mDrawerTitles = getResources().getStringArray(R.array.drawer_list_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTitle = getString(R.string.app_name);
        setTitle(mTitle);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        /* Only set the current fragment here on the first onCreate, fragment transactions
         * should only be handled by the drawer menu from that point onwards otherwise we
         * get weird UI bugs where the view notes fragment UI is placed on top of the
         * Create Notes fragment UI
         */
        if (savedInstanceState == null) {
            CourseNotesFragment fragment = new CourseNotesFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.content_frame,
                    fragment,
                    getString(R.string.course_notes_fragment_tag));

            fragmentTransaction.commit();
            mCurrentFragment = fragment;

            mDrawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mDrawerLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mCurrentPosition = 0;
                    updateDrawerList();
                }
            });
         /* Retrieve the previously selected position in the drawer and assign it to the newly created instance
          * of this activity, then update the drawer list UI
          */
        } else {
            mCurrentPosition = savedInstanceState.getInt("current_position");
            mDrawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mDrawerLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    updateDrawerList();
                }
            });
        }


    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>" + mTitle + "</font>"));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_note_fragment_menu, menu);

        /* Assign the menu */
        this.mMenu = menu;

        UI_Util.setOverflowButtonColor(this, getResources().getColor(R.color.colorSecondary));

        /* Hide all the options menu items, these will only be accessible when the create note
         * fragment is visible
         */
        menu.getItem(0).setVisible(false);
        menu.getItem(0).setEnabled(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(1).setEnabled(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(2).setEnabled(false);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (menu != null) {
            //Hides MenuItem action_edit

            /* Hide all the options menu items, these will only be accessible when the create note
             * fragment is visible
             */
            menu.getItem(0).setVisible(false);
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(1).setEnabled(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(2).setEnabled(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {

            /* If the create note fragment is active, we have to manually open the drawer as
             * swipe gesture activation would have been disabled
             */
            Fragment fragment = getFragmentManager()
                    .findFragmentByTag(getString(R.string.create_note_fragment_tag));

            if (fragment != null)
                if (fragment.isVisible()) {
                    if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                    } else {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    }
                }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateMenu(int menuIconId, int drawableId) {
        MenuItem item = mMenu.findItem(menuIconId);
        item.setIcon(drawableId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void selectItem(final int position) {

        /* Create a new fragment and specify the view to show depending on which option is chosen */


        switch (position) {

            case COURSE_NOTES_POSITION:

                /* If we are on the create note page, inform the user that their unsaved changes will be lost,
                *  if the user selects yes then we will transition to the new fragment, otherwise we will stay where
                *  we are
                */
                if (mCurrentPosition == CREATE_NOTE_POSITION) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            new ContextThemeWrapper(this, R.style.DialogTheme));

                    builder.setTitle(getString(R.string.leave_fragment_title))
                            .setMessage(R.string.leave_fragment_message)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    loadCourseNotesFragment();
                                    mCurrentPosition = position;
                                    updateDrawerList();
                                    // Highlight the selected item, update the title, and close the drawer
                                    mDrawerList.setItemChecked(position, true);
                                    //setTitle(mDrawerTitles[position]);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog confirmationDialog = builder.create();
                    confirmationDialog.setCanceledOnTouchOutside(true);
                    confirmationDialog.show();

                    UI_Util.setDialogStyle(confirmationDialog, this);

                    mDrawerLayout.closeDrawer(mDrawerList);
                    return;

                }

                loadCourseNotesFragment();


                break;


            case CREATE_NOTE_POSITION:

                loadCreateNoteFragment();

                break;

            case MY_NOTES_POSITION:

                /* If we are on the create note page, inform the user that their unsaved changes will be lost,
                *  if the user selects yes then we will transition to the new fragment, otherwise we will stay where
                *  we are
                */
                if (mCurrentPosition == CREATE_NOTE_POSITION) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            new ContextThemeWrapper(this, R.style.DialogTheme));

                    builder.setTitle(getString(R.string.leave_fragment_title))
                            .setMessage(R.string.leave_fragment_message)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    loadMyNotesFragment();
                                    mCurrentPosition = position;
                                    updateDrawerList();
                                    // Highlight the selected item, update the title, and close the drawer
                                    mDrawerList.setItemChecked(position, true);
                                    //setTitle(mDrawerTitles[position]);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog confirmationDialog = builder.create();
                    confirmationDialog.setCanceledOnTouchOutside(true);
                    confirmationDialog.show();

                    UI_Util.setDialogStyle(confirmationDialog, this);

                    mDrawerLayout.closeDrawer(mDrawerList);
                    return;
                }

                loadMyNotesFragment();

                //TODO: Implement this

                break;

        }

        mCurrentPosition = position;
        updateDrawerList();
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        //setTitle(mDrawerTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);


    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("current_position", mCurrentPosition);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* This does nothing, we only want to call the corresponding function in our fragment
         * in other words the fragment handles the activity result not MainActivity
         */
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setTitle(mCurrentFragment.getTag());
    }

    private void lockDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void unLockDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void updateDrawerList() {

        //ArrayAdapter<String> drawerAdapter = (ArrayAdapter<String>) mDrawerList.getAdapter();

        for (int i = 0; i < mDrawerList.getCount(); i++) {
            TextView view = (TextView) mDrawerList.getChildAt(i);
            view.setBackgroundColor(getResources().getColor(R.color.white));
            view.setTextColor(getResources().getColor(R.color.dark_gray));
        }
        Log.d(LOG_TAG, "First visible position in drawer list " + mDrawerList.getFirstVisiblePosition());
        TextView view = (TextView) mDrawerList.getChildAt(mCurrentPosition);

        view.setBackgroundColor(getResources()
                .getColor(R.color.light_gray));

        //view.setTextColor(getResources().getColor(R.color.white));

    }

    private void loadCourseNotesFragment() {

        Fragment fragment = null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        /* First check to see if a fragment exists before we create a new one */
        fragment = getFragmentManager()
                .findFragmentByTag(getString(R.string.course_notes_fragment_tag));

        if (fragment == null)
            fragment = new CourseNotesFragment();

                /* Replace the current fragment that is being displayed, provide it with a tag so we can
                 * locate it in the future
                 */

        transaction.replace(R.id.content_frame,
                fragment,
                getString(R.string.course_notes_fragment_tag));

                /* Ensure that the fragment is displayed in portrait mode */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                /* Actually make the transition */
        transaction.commit();

        mCurrentFragment = fragment;


                /* Allow swipe activation of drawer
                */
        unLockDrawer();
    }


    private void loadMyNotesFragment(){

        Fragment fragment = null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        /* First check to see if a fragment exists before we create a new one */
        fragment = getFragmentManager()
                .findFragmentByTag(getString(R.string.my_notes_fragment_tag));

        if (fragment == null)
            fragment = new MyNotesFragment();

                /* Replace the current fragment that is being displayed, provide it with a tag so we can
                 * locate it in the future
                 */

        transaction.replace(R.id.content_frame,
                fragment,
                getString(R.string.my_notes_fragment_tag));

                /* Ensure that the fragment is displayed in portrait mode */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                /* Actually make the transition */
        transaction.commit();

        mCurrentFragment = fragment;


                /* Allow swipe activation of drawer
                */
        unLockDrawer();

    }

    private void loadCreateNoteFragment() {

        Fragment fragment = null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        /* First check to see if a fragment exists before we create a new one */
        fragment = getFragmentManager()
                .findFragmentByTag(getString(R.string.create_note_fragment_tag));

        if (fragment == null)
            fragment = new CreateNoteFragment();

        /* Replace the current fragment that is being displayed, provide it with a tag so we can
         * locate it in the future
         */
        transaction.replace(R.id.content_frame,
                fragment,
                getString(R.string.create_note_fragment_tag));


        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        /* Actually make the transition */
        transaction.commit();

        mCurrentFragment = fragment;

        /* We don't want to interfere with the drawing space so disable gesture activation of the
         * drawer layout
         */
        lockDrawer();

        /* Ensure that the fragment is displayed in landscape mode */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }

    private void loadSettingsFragment() {

    }

    public void closeDrawer(){
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }


}
