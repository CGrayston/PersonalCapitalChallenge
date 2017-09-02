package brandoncalabro.personalcapitalchallenge;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;

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
}
