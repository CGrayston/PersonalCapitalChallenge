package brandoncalabro.personalcapitalchallenge;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class FeedParser {
    // Constants indicting XML element names that we're interested in
    private static final int TAG_TITLE = 1;
    private static final int TAG_MEDIA_CONTENT = 2;
    private static final int TAG_DESCRIPTION = 3;
    private static final int TAG_PUB_DATE = 4;
    private static final int TAG_LINK = 5;

    // don't use XML namespaces
    private static final String ns = null;

    /**
     * Parse an Atom feed, returning a collection of Entry objects.
     *
     * @param inputStream Atom feed, as a stream.
     * @throws org.xmlpull.v1.XmlPullParserException on error parsing feed.
     * @throws java.io.IOException                   on I/O error.
     */
    @SuppressWarnings("WeakerAccess")
    public List<Feed> parse(InputStream inputStream) throws XmlPullParserException, IOException, ParseException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            inputStream.close();
        }
    }

    /**
     * Decode a feed attached to an XmlPullParser.
     *
     * @param parser Incoming XMl
     * @throws org.xmlpull.v1.XmlPullParserException on error parsing feed.
     * @throws java.io.IOException                   on I/O error.
     */
    private List<Feed> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        List<Feed> items = new ArrayList<>();

        // Search for <rss> tags. These wrap the beginning/end of an Atom document.
        parser.require(XmlPullParser.START_TAG, ns, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("channel")) {
                items = readChannel(parser);
            } else {
                skip(parser);
            }
        }

        return items;
    }

    /**
     * Decode a feed attached to an XmlPullParser.
     *
     * @param parser Incoming XMl
     * @throws org.xmlpull.v1.XmlPullParserException on error parsing feed.
     * @throws java.io.IOException                   on I/O error.
     */
    private List<Feed> readChannel(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        List<Feed> items = new ArrayList<>();

        // Search for <channel> tags. These wrap the beginning/end of an Atom document.
        parser.require(XmlPullParser.START_TAG, ns, "channel");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            // Starts by looking for the <item> tag. This tag repeats inside of <rss> for each article in the feed.
            //
            // Example:
            // <rss>
            // -- <channel>
            // -- -- <item>
            // -- -- -- <title>
            // -- -- -- <media:content url>
            // -- -- -- <description>
            // -- -- -- <pubDate>
            // -- -- -- <link>
            // -- -- </item>
            // -- -- <item>
            // -- -- -- ...
            // -- -- </item>
            // -- </channel>
            // </rss>
            if (name.equals("item")) {
                items.add(readItem(parser));
            } else {
                skip(parser);
            }
        }

        return items;
    }

    /**
     * Parses the contents of an item. If it encounters a title, media:content, description, pubDate, or link tag,
     * hands them off to their respective "read" methods for processing. Otherwise, skips the tag.
     */
    private Feed readItem(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        parser.require(XmlPullParser.START_TAG, ns, "item");

        String title = null;
        String media_content = null;
        String description = null;
        String pubDate = null;
        String link = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            switch (parser.getName()) {
                case "title":
                    title = readTag(parser, TAG_TITLE);
                    break;
                case "media:content":
                    // Example: <media:content url="http://example.com/" />
                    //
                    // Multiple link types can be included. readUrlFromMediaContent() will only return
                    // non-null when reading an "alternate"-type link. Ignore other responses.
                    String url = readTag(parser, TAG_MEDIA_CONTENT);
                    if (url != null) {
                        media_content = url;
                    }
                    break;
                case "description":
                    description = readTag(parser, TAG_DESCRIPTION);
                    break;
                case "pubDate":
                    pubDate = readTag(parser, TAG_PUB_DATE);
                    break;
                case "link":
                    link = readTag(parser, TAG_LINK);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        return new Feed(title, media_content, description, pubDate, link);
    }

    /**
     * Process an incoming tag and read the selected value from it.
     */
    private String readTag(XmlPullParser parser, int tagType) throws IOException, XmlPullParserException {
        switch (tagType) {
            case TAG_TITLE:
                return readBasicTag(parser, "title");
            case TAG_MEDIA_CONTENT:
                return readUrlFromMediaContent(parser);
            case TAG_DESCRIPTION:
                return readBasicTag(parser, "description");
            case TAG_PUB_DATE:
                return readBasicTag(parser, "pubDate");
            case TAG_LINK:
                return readBasicTag(parser, "link");
            default:
                throw new IllegalArgumentException("Unknown tag type: " + tagType);
        }
    }

    /**
     * Reads the body of a basic XML tag, which is guaranteed not to contain any nested elements.
     * <p>
     * <p>You probably want to call readTag().
     *
     * @param parser Current parser object
     * @param tag    XML element tag name to parse
     * @return Body of the specified tag
     * @throws java.io.IOException                   an IO error
     * @throws org.xmlpull.v1.XmlPullParserException an error parsing the feed
     */
    private String readBasicTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return result;
    }

    /**
     * read the url from the media:content tag
     */
    private String readUrlFromMediaContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "media:content");

        String url = parser.getAttributeValue(null, "url");

        while (true) {
            if (parser.nextTag() == XmlPullParser.END_TAG) {
                // Intentionally break; consumes any remaining sub-tags.
                break;
            }
        }

        return url;
    }

    /**
     * For all other tags, extracts the text values.
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
     * if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
     * finds the matching END_TAG (as indicated by the value of "depth" being 0).
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
