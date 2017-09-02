# PersonalCapitalChallenge

###### Task List
- Parse the RSS feed https://blog.personalcapital.com/feed/?cat=3,891,890,68,284
    * The rss feed parser is done through the FeedParser.java class and loads all the feed items asynchronously.
    * A loading indicator, provided by the SwipeRefreshLayout.java class, is used as the main progress indication.
    * I use a Feed.java class to store each individual feed item in a List<Feed> array list which is then passed to the RecyclerView and displayed appropriately.
- Display the title of the feed
    * The title of the main feed is located within <rss><channel><title> ... </title> ... </channel></rss>
    * I use the FeedParser.java class to pull the main feed title from the feed and when the parsing is done I set the main title to the main activity.
- Display each article in a scrolling list that correctly utilizes the space of the device screen.
    * I use the RecyclerView to properly format the articles that are displayed
    * The RecyclerViewAdapter determines the correct device screen layout as well as the primary article view changes.
    * The first article takes prominence taking up the entire first row of the RecyclerView Grid Layout.  the title is set to display a single line max where the complete summary will take up 2 lines max and both will show ellipsis if necessary.
    * Each article under the main article will display the media:content image first and the title beneath it with at most 2 lines displayed and will show ellipsis if necessary.
- For handset, the articles should be in rows of 2.
- For tablet, the articles should be in rows of 3.
- HTML encoded content should be rendered correctly.
- The screen should contain a “refresh” button
    * The button has been added to the activity menu in the upper right corner and which will query the RSS feed and refresh the screen.
    * Another option for refreshing is added using the SwipeRefreshLayout class.
- Selecting an article will render the article’s link in an embedded webview
    * When an article is selected a new fragment is requested and the article shown in full.
    * The title of the article is set to the action bar.
    * When accessing the articles link I append “?displayMobileNavigation=0” to each article’s link.

###### User Experience Suggestions
- I initially allowed the WebViewFragment to load without the toolbar displaying.
    * I thought that with the content already displaying within the view there is no reason to load the title in the toolbar again.
    * Then when the user returned to the MainFragment we would re-display the toolbar.

###### Android Requirements
- Do not use XML Layouts
    * I did not any xml layout while completing this challenge, that is to say that I did not use any resources within /res/layouts/*
    * I did, however, use resources from the /res/values/*, /res/drawable/*, and /res/mipmap/* which included xml resources.