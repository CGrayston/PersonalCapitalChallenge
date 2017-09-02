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
        // load the layout programmatically

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
        // i'm going to use a helper class to convert the dp into pixels
        toolbar.setElevation(getResources().getDimension(R.dimen.toolbar_elevation));
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        toolbar.setPopupTheme(R.style.PopupOverlay);
        linearLayout.addView(toolbar);

        // then add in the frame layout as the second view
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

        // we need to override the action bar to use the support toolbar provided above
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // This is where the app initializes the code that maintains the UI.

        // load the main fragment into the frame layout that was provided in the onCreate
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_main, new MainFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // build the menu programmatically
        menu.add(Menu.NONE, R.id.menu_refresh, Menu.NONE, R.string.menu_action_refresh)
                .setIcon(R.drawable.ic_action_refresh)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                // TODO: provide a more elegant solution to toggle the refresh action within the fragment
                // refreshing will reload the fragment and begin the content loading
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_main, new MainFragment()).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
