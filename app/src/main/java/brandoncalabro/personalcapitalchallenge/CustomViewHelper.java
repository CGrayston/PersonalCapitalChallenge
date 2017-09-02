package brandoncalabro.personalcapitalchallenge;

import android.text.Html;
import android.text.Spanned;

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
}
