package brandoncalabro.personalcapitalchallenge;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

public class WebViewFragment extends Fragment {
    private static final String ARTICLE_URL = "ARTICLE_URL";

    private String articleUrl;

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
     * create new instance of webview fragment that will receive the article url
     *
     * @param articleUrl url of article to view in web view.
     * @return A new instance of fragment WebViewFragment.
     */
    public static WebViewFragment newInstance(String articleUrl) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARTICLE_URL, articleUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            articleUrl = getArguments().getString(ARTICLE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // the first thing we want to do is look for the toolbar layout that we built for the activity
        // if it is visible we want to make it invisible so that we can get a fullscreen view for the webview
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        if(toolbar.getVisibility() == View.VISIBLE) {
            toolbar.setVisibility(View.GONE);
        }

        // now we can build the webview to take up the entire fragment
        WebView webView = new WebView(getActivity());
        webView.setId(R.id.wv_web_view);
        webView.setPadding(
                (int) getActivity().getResources().getDimension(R.dimen.activity_horizontal_margin),
                (int) getActivity().getResources().getDimension(R.dimen.activity_vertical_margin),
                (int) getActivity().getResources().getDimension(R.dimen.activity_horizontal_margin),
                (int) getActivity().getResources().getDimension(R.dimen.activity_vertical_margin));
        webView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        // load the webview settings
        //webView.getSettings().setJavaScriptEnabled(true);

        String displayMode = "?displayMobileNavigation=0";
        // append other params with '&' character

        // create the modified web view url with the parameters and the article url
        String webViewUrl = articleUrl + displayMode;

        webView.loadUrl(webViewUrl);

        return webView;
    }

}
