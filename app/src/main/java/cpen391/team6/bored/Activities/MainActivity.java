package cpen391.team6.bored.Activities;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cpen391.team6.bored.Fragments.CreateNoteFragment;
import cpen391.team6.bored.Fragments.ViewNotesFragment;
import cpen391.team6.bored.R;

public class MainActivity extends AppCompatActivity {

    private Fragment mCurrentFragment;
    private String[] mDrawerTitles;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mDrawerTitles = getResources().getStringArray(R.array.drawer_list_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTitle = getString(R.string.app_name);
        setTitle(mTitle);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
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

        ViewNotesFragment fragment = new ViewNotesFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_frame,
                fragment,
                getString(R.string.view_notes_fragment_tag));

        fragmentTransaction.commit();
        mCurrentFragment = fragment;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFF00'>" + mTitle + "</font>"));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void selectItem(int position) {

        /* Create a new fragment and specify the view to show depending on which option is chosen */
        Fragment fragment = null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        switch (position) {

            case 0:

                /* First check to see if a fragment exists before we create a new one */
                fragment = getFragmentManager()
                        .findFragmentByTag(getString(R.string.view_notes_fragment_tag));

                if (fragment == null)
                    fragment = new ViewNotesFragment();

                /* Replace the current fragment that is being displayed, provide it with a tag so we can
                 * locate it in the future
                 */
                transaction.remove(mCurrentFragment);

                transaction.replace(R.id.content_frame,
                        fragment,
                        getString(R.string.view_notes_fragment_tag));

                /* This call is necessary so we don't create a new fragment by default, not sure why */
                transaction.addToBackStack(null);

                /* Ensure that the fragment is displayed in portrait mode */
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                /* Actually make the transition */
                transaction.commit();

                /* Allow swipe activation of drawer
                */
                unLockDrawer();

                break;


            case 1:
            /* First check to see if a fragment exists before we create a new one */
                fragment = getFragmentManager()
                        .findFragmentByTag(getString(R.string.create_note_fragment_tag));

                if (fragment == null)
                    fragment = new CreateNoteFragment();

                /* Replace the current fragment that is being displayed, provide it with a tag so we can
                 * locate it in the future
                 */
                transaction.remove(mCurrentFragment);

                transaction.replace(R.id.content_frame,
                        fragment,
                        getString(R.string.create_note_fragment_tag));

                /* This call is necessary so we don't create a new fragment by default, not sure why */
                transaction.addToBackStack(null);

                /* Actually make the transition */
                transaction.commit();

                /* We don't want to interfere with the drawing space so disable gesture activation of the
                * drawer layout
                */
                lockDrawer();

                /* Ensure that the fragment is displayed in landscape mode */
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                break;
        }

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mDrawerTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);


    }

    private void lockDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void unLockDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

}
