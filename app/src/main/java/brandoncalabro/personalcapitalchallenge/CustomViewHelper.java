package brandoncalabro.personalcapitalchallenge;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * this custom view helper class will help centralize specific methods relating to view customization
 */
@SuppressWarnings("WeakerAccess")
public class CustomViewHelper {
    /**
     * format the html text from the xml input
     *
     * @param html html formatted text
     * @return returns an the string with the text properly formatted for the text view
     */
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    /**
     * this method will allow us to see the android selected or touch animation by setting
     * the item background correctly
     *
     * @return drawable of the selected item background animation
     */
    public static Drawable getSelectedItemBackground(Context context) {
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        Drawable selectedItemDrawable = ta.getDrawable(0);
        ta.recycle();
        return selectedItemDrawable;
    }

    /**
     * In order to display the date time as requested i've had to properly format it according to the
     * input given.  once formatted I can output the suggested format
     * http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
     *
     * @param dateString unformatted date string
     * @return returns properly formatted date string
     */
    public static String formatDateTime(String dateString) {
        // Fri, 01 Sep 2017 22:00:30 +0000 --> EEE, dd MMM yyyy HH:mm:ss Z
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        Date date;
        try {
            date = simpleDateFormat.parse(dateString);

            // September 01, 2017 --> MMMM dd, yyyy
            simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            dateString = simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }
}
