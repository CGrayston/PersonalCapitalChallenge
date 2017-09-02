package brandoncalabro.personalcapitalchallenge;


import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public MainFragment() {
        /*
        All subclasses of Fragment must include a public empty constructor.
        The framework will often re-instantiate a fragment class when needed, in particular
        during state restore, and needs to be able to find this constructor to instantiate it.
        If the empty constructor is not available, a runtime exception will occur in some cases
        during state restore.
         */
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // just like in the main activity, here i'll create the layout for the fragment pragmatically
        // the root view will be a swipe to refresh layout that will contain the recycler view.
        // declare the swipe refresh layout variable as global so we can start/stop the refreshing as needed
        swipeRefreshLayout = new SwipeRefreshLayout(getActivity());
        swipeRefreshLayout.setId(R.id.srl_fragment_main);
        swipeRefreshLayout.setLayoutParams(new SwipeRefreshLayout.LayoutParams(SwipeRefreshLayout.LayoutParams.MATCH_PARENT,
                SwipeRefreshLayout.LayoutParams.MATCH_PARENT));
        // add an on refresh listener to the swipe refresh layout!  without the on refresh listener
        // the app will crash with a null pointer exception.  ensure to stop the refresh animation as well.
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(LOG_TAG, "loading rss feed...");

                // update the recycler view adapter with the data from the rss feed
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

        recyclerView.setLayoutManager(getGridLayoutManager());

        // set an empty adapter
        recyclerView.setAdapter(new CustomRecyclerViewAdapter(getActivity(), new ArrayList<Feed>(), null));
        swipeRefreshLayout.addView(recyclerView);

        // finally return the swipe refresh layout as our view.  because SwipeRefreshLayout is a
        // subclass of ViewGroup which is then a subclass of View, we can return this class as well!
        return swipeRefreshLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        // update the recycler view adapter
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
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }

            feedParser = new FeedParser();
        }

        @Override
        protected List<Feed> doInBackground(Void... voids) {
            String rssUrl = "https://blog.personalcapital.com/feed/?cat=3,891,890,68,284";

            try {
                InputStream inputStream = new URL(rssUrl).openStream();
                return feedParser.parse(inputStream);
            } catch (IOException | XmlPullParserException | ParseException e) {
                e.printStackTrace();
            }

            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<Feed> feedList) {
            super.onPostExecute(feedList);

            updateRecyclerView(feedList);

            // if the animation is still refreshing then we should end it now
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
                            // get the article url
                            String article_url = feedList.get(position).getArticle_url();

                            // TODO start webview for the given article url
                            Log.d(LOG_TAG, article_url);
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
        GridLayoutManager gridLayoutManager;
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
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
        } else {
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
        }

        return gridLayoutManager;
    }
}
