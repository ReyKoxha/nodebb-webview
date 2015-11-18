package com.webview.nodebb;


public class WebViewConfig {
    // Enable or disable Analaytics
    public static final boolean ANALYTICS = false;

    // Enable or disable AdMob
    public static final boolean ADMOB = false;

    // Open links externally?
    public static final boolean OPEN_LINKS_IN_EXTERNAL_BROWSER = false;

    // External browser rules
    public static final String[] LINKS_OPENED_IN_EXTERNAL_BROWSER = {
            "target=blank",
            "target=external",
            "play.google.com/store",
            "youtube.com/watch"
    };

    // Internal link rules
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
