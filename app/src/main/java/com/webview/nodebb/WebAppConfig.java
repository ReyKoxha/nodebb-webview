package com.webview.nodebb;


public class WebAppConfig {
    // Enable or disable Analaytics
    public static final boolean ANALYTICS = false;

    // Enable or disable AdMob
    public static final boolean ADMOB = false;

    // Open links in browser, instead of WebView
    public static final boolean OPEN_LINKS_IN_EXTERNAL_BROWSER = false;

    // Open in external browser/app
    public static final String[] LINKS_OPENED_IN_EXTERNAL_BROWSER = {
            "target=blank",
            "target=external",
            "play.google.com/store",
            "youtube.com/watch"
    };

    // Internal rules
    public static final String[] LINKS_OPENED_IN_INTERNAL_WEBVIEW = {
            "target=webview",
            "target=internal"
    };

    // MIME types supported by download manager
    public static final String[] DOWNLOAD_FILE_TYPES = {
            ".zip", ".rar", ".pdf", ".doc", ".xls",
            ".mp3", ".wma", ".ogg", ".m4a", ".wav",
            ".avi", ".mov", ".mp4", ".mpg", ".3gp",
            ".bin", ".apk", ".gif", ".png", ".jpg",
            ".jpeg"
    };
}
