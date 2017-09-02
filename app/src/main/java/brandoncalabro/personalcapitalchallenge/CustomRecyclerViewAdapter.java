package brandoncalabro.personalcapitalchallenge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.CustomViewHolder> {
    @SuppressWarnings("FieldCanBeLocal")
    private final String LOG_TAG = "RecyclerViewAdapter";

    private Context context;
    private List<Feed> feedList;

    CustomRecyclerViewAdapter(Context context, List<Feed> feedList) {
        this.context = context;
        this.feedList = feedList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create the linear layout view to hold the text view
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.START & Gravity.TOP); // top left gravity
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        // add the title text view to the linear layout view
        TextView tv_title = new TextView(context);
        tv_title.setId(R.id.tv_title);
        tv_title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(tv_title);

        // add the media content image view to the linear layout view
        ImageView iv_media_content = new ImageView(context);
        iv_media_content.setId(R.id.iv_media_content);
        iv_media_content.setContentDescription(context.getResources().getString(R.string.iv_content_description));
        iv_media_content.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv_media_content.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(iv_media_content);

        // add the description text view to the linear layout view
        TextView tv_description = new TextView(context);
        tv_description.setId(R.id.tv_description);
        tv_description.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(tv_description);

        // add the publish date text view to the linear layout view
        TextView tv_pub_date = new TextView(context);
        tv_pub_date.setId(R.id.tv_pub_date);
        tv_pub_date.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(tv_pub_date);

        // add the link text view to the linear layout view
        TextView tv_link = new TextView(context);
        tv_link.setId(R.id.tv_link);
        tv_link.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(tv_link);

        return new CustomViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Feed feed = feedList.get(position);

        holder.tv_title.setText(CustomViewHelper.fromHtml(feed.getTitle()));
        holder.tv_description.setText(CustomViewHelper.fromHtml(feed.getDescription()));
        holder.tv_pub_date.setText(feed.getPub_date());
        holder.tv_link.setText(feed.getArticle_url());

        new MediaContentLoader(holder.iv_media_content, feed.getImage_url()).execute();
    }

    @Override
    public int getItemCount() {
        if (feedList != null) {
            return feedList.size();
        } else {
            return 0;
        }
    }

    public class MediaContentLoader extends AsyncTask<Void, Void, Bitmap> {
        private ImageView imageView;
        private String url;

        public MediaContentLoader(ImageView imageView, String url) {
            this.imageView = imageView;
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return getImageBitmap(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }

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

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_title;
        public ImageView iv_media_content;
        public TextView tv_description;
        public TextView tv_pub_date;
        public TextView tv_link;

        public CustomViewHolder(View v) {
            super(v);
            tv_title = v.findViewById(R.id.tv_title);
            iv_media_content = v.findViewById(R.id.iv_media_content);
            tv_description = v.findViewById(R.id.tv_description);
            tv_pub_date = v.findViewById(R.id.tv_pub_date);
            tv_link = v.findViewById(R.id.tv_link);
        }
    }
}
