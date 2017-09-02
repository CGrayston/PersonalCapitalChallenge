package brandoncalabro.personalcapitalchallenge;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    private final String LOG_TAG = "MainFragment";

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    /**
     * All subclasses of Fragment must include a public empty constructor.
     * The framework will often re-instantiate a fragment class when needed, in particular
     * during state restore, and needs to be able to find this constructor to instantiate it.
     * If the empty constructor is not available, a runtime exception will occur in some cases
     * during state restore.
     */
    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // in order to access the menu from the fragment we must let the fragment know that
        // there is in fact a menu to handle
        setHasOptionsMenu(true);

        // just like in the main activity, we can create the layout for the fragment programmatically
        // the root view will be a swipe to refresh layout that will contain the recycler view.
        // we can declare the swipe refresh layout variable as global so we can start/stop the
        // refreshing animation as needed
        swipeRefreshLayout = new SwipeRefreshLayout(getActivity());
        swipeRefreshLayout.setId(R.id.srl_fragment_main);
        swipeRefreshLayout.setLayoutParams(new SwipeRefreshLayout.LayoutParams(SwipeRefreshLayout.LayoutParams.MATCH_PARENT,
                SwipeRefreshLayout.LayoutParams.MATCH_PARENT));
        // add an onRefreshListener to the swipe refresh layout!  without the onRefreshListener
        // the app will crash with a null pointer exception
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // this asynctask call will allow us to reload the data from the rss feed
                new RssLoader().execute();
            }
        });

        // add the recycler view within the swipe refresh view
        recyclerView = new RecyclerView(getActivity());
        recyclerView.setId(R.id.rv_recycler_view);
        recyclerView.setVerticalScrollBarEnabled(false);
        recyclerView.setHorizontalScrollBarEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT));

        // due to the requirement for the main (initial) rss item to be extended we need to customize
        // the grid layout manager, we an also handle orientation changes with this customization
        recyclerView.setLayoutManager(getGridLayoutManager());

        // no data is available right at this moment so we will call the empty constructor for the
        // adapter simply to initialize it
        recyclerView.setAdapter(new CustomRecyclerViewAdapter());
        swipeRefreshLayout.addView(recyclerView);

        // finally return the swipe refresh layout as our view.  because SwipeRefreshLayout is a
        // subclass of ViewGroup which is then a subclass of View, we can return this class as well!
        return swipeRefreshLayout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // ensure that the refresh menu item is visible in this fragment
        MenuItem menuItem = menu.findItem(R.id.menu_refresh);
        menuItem.setVisible(true);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();

        // in order to handle orientation changes properly we need to have this async task within the
        // onResume override method so that we don't lose our activity context
        new RssLoader().execute();
    }

    /**
     * Asynchronously load the contents of the RSS feed
     */
    @SuppressWarnings("WeakerAccess")
    public class RssLoader extends AsyncTask<Void, Boolean, List<Feed>> {
        private FeedParser feedParser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before we begin loading data we need to check if the animation is running, if not
            // then we should start it so that the user knows that data will begin loading
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }

            feedParser = new FeedParser();
        }

        @Override
        protected List<Feed> doInBackground(Void... voids) {
            // first we will get the rss feed url from the resources
            String rssUrl = getActivity().getResources().getString(R.string.rss_feed_url);

            // now we are going to first check for an active internet connection, if we can't find one
            // then we can't access any content over the internet so we should let the user know
            if (Connectivity.isConnectedWifi(getActivity()) || Connectivity.isConnectedMobile(getActivity())) {
                // finally we have a connection so we can attempt to load and parse the feed data
                try {
                    InputStream inputStream = new URL(rssUrl).openStream();
                    return feedParser.parse(inputStream);
                } catch (IOException | XmlPullParserException | ParseException e) {
                    e.printStackTrace();

                    return new ArrayList<>();
                }
            } else if (!Connectivity.isConnectedWifi(getActivity())) {
                Toast.makeText(getActivity(), "No Wifi connection detected", Toast.LENGTH_SHORT).show();
                return new ArrayList<>();
            } else if (!Connectivity.isConnectedMobile(getActivity())) {
                Toast.makeText(getActivity(), "No Mobile connection detected", Toast.LENGTH_SHORT).show();
                return new ArrayList<>();
            } else {
                Toast.makeText(getActivity(), "No Internet connection detected", Toast.LENGTH_SHORT).show();
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<Feed> feedList) {
            super.onPostExecute(feedList);

            // we should have an array of feed items or an empty array to load into the recycler view
            updateRecyclerView(feedList);

            // finally set the title of the toolbar with the main feed title
            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            toolbar.setTitle(CustomViewHelper.fromHtml(feedParser.getFeedTitle()));

            // finally we check if the animation is still running and if it is we should stop it here
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        /**
         * we should have the rss feed data when we update the recycler view, for now we'll keep the
         * recycler view settings fairly straight forward by fixing the size of each view and setting
         * the grid layout to have 2 columns per row.  once the adapter is built and set then we can stop
         * the refresh animation.
         */
        private void updateRecyclerView(final List<Feed> feedList) {
            recyclerView.setLayoutManager(getGridLayoutManager());

            // set the adapter
            recyclerView.setAdapter(new CustomRecyclerViewAdapter(
                    getActivity(),
                    feedList,
                    new CustomRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fl_fragment_main, WebViewFragment.newInstance(feedList.get(position)))
                                    .addToBackStack(LOG_TAG)
                                    .commit();
                        }
                    }));
        }
    }

    /**
     * for phone views we want to have a grid view of 2 article per row but for tablet view we want
     * to have a grid view of 3 articles per row.  in both cases the main header article will take up
     * the entire column span
     *
     * @return the grid layout manager
     */
    private GridLayoutManager getGridLayoutManager() {
        // the first task is to ensure that phone and tablet layouts are handled appropriately
        // and the second task is to make the first, primary, feed item a unique view that will contain
        // slightly more data than the standard grid views that follow it
        GridLayoutManager gridLayoutManager;

        // use the display metrics to determine if the device is a tablet or phone
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        float yInches = displayMetrics.heightPixels / displayMetrics.ydpi;
        float xInches = displayMetrics.widthPixels / displayMetrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches >= 6.5) {
            // 6.5inch device or bigger are my tablet definition
            // these layouts will use a 3 column grid view
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (position) {
                        case 0:
                            return 3;
                        default:
                            return 1;
                    }
                }
            });
        } else {
            // everything else is a phone display
            // these layouts will use a 2 column grid view
            gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (position) {
                        case 0:
                            return 2;
                        default:
                            return 1;
                    }
                }
            });
        }

        return gridLayoutManager;
    }

    /**
     * this public method will allow the activity to call it so that the activity refresh action
     * can start the refresh process of downloading the new data
     */
    public void refreshContent() {
        new RssLoader().execute();
    }
}
