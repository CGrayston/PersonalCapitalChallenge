package brandoncalabro.personalcapitalchallenge;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

public class WebViewFragment extends Fragment {
    private static final String ARTICLE_URL = "ARTICLE_URL";
    private static final String ARTICLE_TITLE = "ARTICLE_TITLE";

    private String articleUrl;
    private String articleTitle;

    /**
     * All subclasses of Fragment must include a public empty constructor.
     * The framework will often re-instantiate a fragment class when needed, in particular
     * during state restore, and needs to be able to find this constructor to instantiate it.
     * If the empty constructor is not available, a runtime exception will occur in some cases
     * during state restore.
     */
    public WebViewFragment() {
    }

    /**
     * create new instance of webview fragment that will receive the feed item and pull two
     * pieces of data from it, the article url and article title
     *
     * @param feed the entire feed item
     * @return A new instance of fragment WebViewFragment.
     */
    public static WebViewFragment newInstance(Feed feed) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARTICLE_TITLE, feed.getTitle());
        args.putString(ARTICLE_URL, feed.getArticle_url());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            articleUrl = getArguments().getString(ARTICLE_URL);
            articleTitle = getArguments().getString(ARTICLE_TITLE);
        } else {
            articleUrl = "";
            articleTitle = "";
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // in order to access the menu from the fragment we must let the fragment know that
        // there is in fact a menu to handle
        setHasOptionsMenu(true);

        // set the title of the article to the toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(articleTitle);

        // now we can build the webview to take up the entire fragment
        WebView webView = new WebView(getActivity());
        webView.setId(R.id.wv_web_view);
        webView.setPadding(
                (int) getActivity().getResources().getDimension(R.dimen.activity_horizontal_margin),
                (int) getActivity().getResources().getDimension(R.dimen.activity_vertical_margin),
                (int) getActivity().getResources().getDimension(R.dimen.activity_horizontal_margin),
                (int) getActivity().getResources().getDimension(R.dimen.activity_vertical_margin));
        webView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        // load the webview settings and set javascript enabled to true so the user experience on the
        // web view will be interactive and enjoyable
        webView.getSettings().setJavaScriptEnabled(true);

        // we need to pass the display mode parameter as a requirement to viewing the article url
        String displayMode = getActivity().getResources().getString(R.string.web_view_display_mode);

        // create the modified web view url with the parameters and the article url
        if (articleUrl != null) {
            String webViewUrl = articleUrl + "?" + displayMode;
            // append other params with '&' character

            // attach the url to the webview so that when we start the fragment the view will display
            webView.loadUrl(webViewUrl);
        }

        return webView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // we want to hide the menu item for refreshing while in this fragment
        MenuItem menuItem = menu.findItem(R.id.menu_refresh);
        menuItem.setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }
}
