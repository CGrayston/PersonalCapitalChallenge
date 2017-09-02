package brandoncalabro.personalcapitalchallenge;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;

@SuppressWarnings("WeakerAccess")
public class CustomViewHelper {
    /**
     * convert the dp int value to a float elevation of pixels
     *
     * @param dp int dp value to be converted
     * @return returns the float elevation of pixels given the int dp
     */
    @SuppressWarnings("WeakerAccess")
    public static float convertDpToPixels(Context context, int dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

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
}
