package brandoncalabro.personalcapitalchallenge;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // the root layout will be the coordinator layout
        CoordinatorLayout coordinatorLayout = new CoordinatorLayout(this);
        coordinatorLayout.setId(R.id.cl_activity_main);
        // specify the layout params of the coordinator layout, this will be added to the view
        // when we set the entire content view at the very end
        CoordinatorLayout.LayoutParams coordinatorLayoutParams = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.MATCH_PARENT);

        // add a sub layout, LinearLayout, that will host the toolbar and the FrameLayout for
        // the fragment that will be attached to this activity
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        // within linear layout add in the toolbar layout as the first view
        Toolbar toolbar = new Toolbar(this);
        toolbar.setId(R.id.toolbar);
        toolbar.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT,
                Toolbar.LayoutParams.WRAP_CONTENT));
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        toolbar.setDrawingCacheBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        toolbar.setElevation(getResources().getDimension(R.dimen.toolbar_elevation));
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        toolbar.setPopupTheme(R.style.PopupOverlay);
        linearLayout.addView(toolbar);

        // then add in the frame layout as the second view.  this frame layout will hold our fragment view
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(R.id.fl_fragment_main);
        frameLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        linearLayout.addView(frameLayout);

        // finally add the linear layout to the coordinator layout view
        coordinatorLayout.addView(linearLayout);

        // and then set the root view with the layout params as the main content view
        setContentView(coordinatorLayout, coordinatorLayoutParams);

        // because we manually hid the default actionbar in the theme style we need to manually tell
        // the activity that the toolbar above will be our new action bar replacement
        setSupportActionBar(toolbar);

        // we should check for for a saved instance state so that when the orientation changes we
        // don't reinitialize the fragment and lose our state
        if (savedInstanceState == null) {
            // load the main fragment into the frame layout that was provided
            // we only need to do this for our initial view
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_main, new MainFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // create the menu item for the refresh action and attach an icon  to it
        menu.add(Menu.NONE, R.id.menu_refresh, Menu.NONE, R.string.menu_action_refresh)
                .setIcon(R.drawable.ic_action_refresh)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                // the user has selected to refresh the data on screen so we can access the fragment
                // using this method which will begin the rss loader async task
                refreshContent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * access the fragment that contains the feed and attempt to refresh it
     */
    private void refreshContent() {
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fl_fragment_main);

        // if the fragment we are requesting is attached then we can refresh the data
        if (mainFragment != null) {
            mainFragment.refreshContent();
        }
    }
}
