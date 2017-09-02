package brandoncalabro.personalcapitalchallenge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @SuppressWarnings("FieldCanBeLocal")
    private final String LOG_TAG = "RecyclerViewAdapter";

    private Context context;
    private List<Feed> feedList;

    private static final int VIEW_TYPE_PRIMARY = 1;
    private static final int VIEW_TYPE_STANDARD = 2;

    private OnItemClickListener onItemClickListener;

    /**
     * this is my interface for the on click listener so that when the user touches a view
     * or card they will be able to transition to the webview.
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * this empty constructor is used to create an empty recycler view adapter
     */
    CustomRecyclerViewAdapter() {
        this.context = null;
        this.feedList = new ArrayList<>();

        this.onItemClickListener = null;
    }

    /**
     * the main recycler view adapter constructor will take in several parameters to build the dynamic
     * recycler view with all the data from the rss feed list
     *
     * @param context             activity context
     * @param feedList            list of feed items from the rss feed
     * @param onItemClickListener onclick listener to handle webview transitions
     */
    CustomRecyclerViewAdapter(Context context, List<Feed> feedList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.feedList = feedList;

        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        // if the position is the first card then we'll return the primary extended view type
        // otherwise return the standard reduced view type
        if (position == 0) {
            return VIEW_TYPE_PRIMARY;
        } else {
            return VIEW_TYPE_STANDARD;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // depending on the view type we wll return the appropriate view holder
        switch (viewType) {
            case VIEW_TYPE_PRIMARY:
                return new PrimaryViewHolder(getPrimaryView());
            case VIEW_TYPE_STANDARD:
                return new StandardViewHolder(getStandardView());
            default:
                return new StandardViewHolder(getStandardView());
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // first get the data from the feed list at the item position
        Feed feed = feedList.get(position);

        // initialize the view holders
        PrimaryViewHolder primaryViewHolder;
        StandardViewHolder standardViewHolder;

        // after determining which view holder we are going to use we can bind the data appropriately
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_PRIMARY:
                primaryViewHolder = (PrimaryViewHolder) holder;

                primaryViewHolder.tv_title.setText(CustomViewHelper.fromHtml(feed.getTitle()));

                String fullDescription = CustomViewHelper.formatDateTime(feed.getPub_date())
                        + CustomViewHelper.fromHtml("&mdash;")
                        + CustomViewHelper.fromHtml(feed.getDescription());

                primaryViewHolder.tv_description.setText(fullDescription.trim());

                new MediaContentLoader(primaryViewHolder.iv_media_content, feed.getImage_url()).execute();

                primaryViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(v, holder.getAdapterPosition());
                    }
                });
                break;
            case VIEW_TYPE_STANDARD:
                standardViewHolder = (StandardViewHolder) holder;

                standardViewHolder.tv_title.setText(CustomViewHelper.fromHtml(feed.getTitle()));

                new MediaContentLoader(standardViewHolder.iv_media_content, feed.getImage_url()).execute();

                standardViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(v, holder.getAdapterPosition());
                    }
                });
                break;
            default:
                standardViewHolder = (StandardViewHolder) holder;

                standardViewHolder.tv_title.setText(CustomViewHelper.fromHtml(feed.getTitle()));

                new MediaContentLoader(standardViewHolder.iv_media_content, feed.getImage_url()).execute();

                standardViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(v, holder.getAdapterPosition());
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (feedList != null) {
            return feedList.size();
        } else {
            return 0;
        }
    }

    /**
     * get the primary view holder for the card view
     *
     * @return card view of the initial main view for the recycler view
     */
    private CardView getPrimaryView() {
        // create the root view a a card view to host the entire view holder
        CardView cardView = new CardView(context);
        cardView.setForeground(CustomViewHelper.getSelectedItemBackground(context));
        cardView.setClickable(true);
        cardView.setElevation(context.getResources().getDimension(R.dimen.card_view_elevation));
        cardView.setRadius(context.getResources().getDimension(R.dimen.card_view_corner_radius));
        cardView.setContentPadding(
                (int) context.getResources().getDimension(R.dimen.card_view_padding),
                (int) context.getResources().getDimension(R.dimen.card_view_padding),
                (int) context.getResources().getDimension(R.dimen.card_view_padding),
                (int) context.getResources().getDimension(R.dimen.card_view_padding));
        CardView.LayoutParams cardLayoutParams = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT);
        cardLayoutParams.setMargins(
                (int) context.getResources().getDimension(R.dimen.card_view_margin),
                (int) context.getResources().getDimension(R.dimen.card_view_margin),
                (int) context.getResources().getDimension(R.dimen.card_view_margin),
                (int) context.getResources().getDimension(R.dimen.card_view_margin));
        cardView.setLayoutParams(cardLayoutParams);

        // create the linear layout view to hold the text view
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // add the media content image view to the linear layout view
        ImageView iv_media_content = new ImageView(context);
        iv_media_content.setId(R.id.iv_media_content);
        iv_media_content.setContentDescription(context.getResources().getString(R.string.iv_content_description));
        iv_media_content.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv_media_content.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(iv_media_content);

        // add the title text view to the linear layout view
        TextView tv_title = new TextView(context);
        tv_title.setId(R.id.tv_title);
        tv_title.setTypeface(null, Typeface.BOLD);
        tv_title.setLines(1);
        tv_title.setEllipsize(TextUtils.TruncateAt.END);
        tv_title.setMaxLines(1);

        tv_title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        tv_title.setPadding(
                (int) context.getResources().getDimension(R.dimen.view_padding),
                (int) context.getResources().getDimension(R.dimen.view_padding),
                (int) context.getResources().getDimension(R.dimen.view_padding),
                (int) context.getResources().getDimension(R.dimen.view_padding));
        linearLayout.addView(tv_title);

        // under the title will be the publish date and description in one view as they will be
        // concatenated with a long dash separator
        // add the link text view to the linear layout view
        TextView tv_description = new TextView(context);
        tv_description.setId(R.id.tv_description);
        tv_description.setEllipsize(TextUtils.TruncateAt.END);
        tv_description.setLines(2);
        tv_description.setMaxLines(2);
        tv_description.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        tv_description.setPadding(
                (int) context.getResources().getDimension(R.dimen.view_padding),
                (int) context.getResources().getDimension(R.dimen.view_padding),
                (int) context.getResources().getDimension(R.dimen.view_padding),
                (int) context.getResources().getDimension(R.dimen.view_padding));
        linearLayout.addView(tv_description);

        cardView.addView(linearLayout);

        return cardView;
    }

    /**
     * get the standard or simple view for the card
     *
     * @return card view of the simplified or standard view
     */
    private CardView getStandardView() {
        // create the root view a a card view to host the entire view holder
        CardView cardView = new CardView(context);
        cardView.setForeground(CustomViewHelper.getSelectedItemBackground(context));
        cardView.setClickable(true);
        cardView.setElevation(context.getResources().getDimension(R.dimen.card_view_elevation));
        cardView.setRadius(context.getResources().getDimension(R.dimen.card_view_corner_radius));
        cardView.setContentPadding(
                (int) context.getResources().getDimension(R.dimen.card_view_padding),
                (int) context.getResources().getDimension(R.dimen.card_view_padding),
                (int) context.getResources().getDimension(R.dimen.card_view_padding),
                (int) context.getResources().getDimension(R.dimen.card_view_padding));
        CardView.LayoutParams cardLayoutParams = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT);
        cardLayoutParams.setMargins(
                (int) context.getResources().getDimension(R.dimen.card_view_margin),
                (int) context.getResources().getDimension(R.dimen.card_view_margin),
                (int) context.getResources().getDimension(R.dimen.card_view_margin),
                (int) context.getResources().getDimension(R.dimen.card_view_margin));
        cardView.setLayoutParams(cardLayoutParams);

        // create the linear layout view to hold the text view
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // add the media content image view to the linear layout view
        ImageView iv_media_content = new ImageView(context);
        iv_media_content.setId(R.id.iv_media_content);
        iv_media_content.setContentDescription(context.getResources().getString(R.string.iv_content_description));
        iv_media_content.setScaleType(ImageView.ScaleType.FIT_XY);
        iv_media_content.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(iv_media_content);

        // add the title text view to the linear layout view
        TextView tv_title = new TextView(context);
        tv_title.setId(R.id.tv_title);
        tv_title.setTypeface(null, Typeface.BOLD);
        tv_title.setLines(2);
        tv_title.setEllipsize(TextUtils.TruncateAt.END);
        tv_title.setMaxLines(2);
        tv_title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        tv_title.setPadding(
                (int) context.getResources().getDimension(R.dimen.view_padding),
                (int) context.getResources().getDimension(R.dimen.view_padding),
                (int) context.getResources().getDimension(R.dimen.view_padding),
                (int) context.getResources().getDimension(R.dimen.view_padding));
        linearLayout.addView(tv_title);

        cardView.addView(linearLayout);

        return cardView;
    }

    /**
     * using the asynctask we can download the image contents in the background and then set them
     * to the image view as they are loaded in
     */
    public class MediaContentLoader extends AsyncTask<Void, Void, Bitmap> {
        private ImageView imageView;
        private String url;

        MediaContentLoader(ImageView imageView, String url) {
            this.imageView = imageView;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // we should initialize every image view to the default image asset for an image placeholder
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_image_placeholder, context.getTheme()));
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            // we are going to attempt to access the internet so we need to again check if we have a connection
            // available so that we don't throw an IO exception
            if (Connectivity.isConnectedWifi(context) || Connectivity.isConnectedMobile(context)) {
                // finally we have a connection so we can attempt to load and parse the feed data
                return getImageBitmap(url);
            } else if (!Connectivity.isConnectedWifi(context)) {
                return null;
            } else if (!Connectivity.isConnectedMobile(context)) {
                return null;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // finally we have a bitmap and we can set the image view
            // if the bitmap is null, however, we should let the user know that something
            // went wrong, perhaps with a broken image asset in the image view
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_broken_image, context.getTheme()));
            }
        }

        /**
         * we can pull the image from the url provided and load it into a bitmap
         *
         * @param url url of the image to be loaded
         * @return the bitmap representation of the image
         */
        private Bitmap getImageBitmap(String url) {
            Bitmap bm = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed to load the image from the url: " + url, e);
            }
            return bm;
        }
    }

    /**
     * the primary view holder will hold the extended view which will have a column
     * span of 2 and give just a bit more information than the standard view holder
     */
    public class PrimaryViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_title;
        public ImageView iv_media_content;
        public TextView tv_description;

        public View view;

        public PrimaryViewHolder(View v) {
            super(v);
            tv_title = v.findViewById(R.id.tv_title);
            iv_media_content = v.findViewById(R.id.iv_media_content);
            tv_description = v.findViewById(R.id.tv_description);
            view = v;
        }
    }

    /**
     * the standard view holder will give only the image and the title underneath it this is the
     * general view that will be in a grid view of 2 columns per row
     */
    public class StandardViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_title;
        public ImageView iv_media_content;

        public View view;

        public StandardViewHolder(View v) {
            super(v);
            tv_title = v.findViewById(R.id.tv_title);
            iv_media_content = v.findViewById(R.id.iv_media_content);
            view = v;
        }
    }
}
