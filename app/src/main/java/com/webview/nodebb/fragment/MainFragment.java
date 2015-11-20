package com.webview.nodebb.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.webview.nodebb.R;
import com.webview.nodebb.WebViewConfig;
import com.webview.nodebb.utility.DownloadUtil;
import com.webview.nodebb.utility.MediaUtility;
import com.webview.nodebb.utility.NetworkInf;
import com.webview.nodebb.view.ViewState;

import java.io.File;


public class MainFragment extends TaskFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARGUMENT_URL = "url";
    private static final String ARGUMENT_SHARE = "share";
    private static final int REQUEST_FILE_PICKER = 1;

    private boolean mActionBarProgress = false;
    private ViewState mViewState = null;
    private View mRootView;
    private String mUrl = "about:blank";
    private String mShare;
    private boolean mLocal = false;
    private ValueCallback<Uri> mFilePathCallback4;
    private ValueCallback<Uri[]> mFilePathCallback5;


    public static MainFragment newInstance(String url) {
        MainFragment fragment = new MainFragment();

        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_URL, url);
        fragment.setArguments(arguments);

        return fragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        Bundle arguments = getArguments();
        if (arguments != null) {
            handleArguments(arguments);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);
        return mRootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            WebView webView = (WebView) mRootView.findViewById(R.id.fragment_main_webview);
            webView.restoreState(savedInstanceState);
        }

        // Setup webview
        renderView();

        // Pull to refresh
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.container_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Load and show data
        if (mViewState == null || mViewState == ViewState.OFFLINE) {
            loadData();
        } else if (mViewState == ViewState.CONTENT) {
            showContent();
        } else if (mViewState == ViewState.PROGRESS) {
            showProgress();
        } else if (mViewState == ViewState.EMPTY) {
            showEmpty();
        }

        // progress in action bar
        showActionBarProgress(mActionBarProgress);
    }


    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_FILE_PICKER) {
            if (mFilePathCallback4 != null) {
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
                if (result != null) {
                    String path = MediaUtility.getPath(getActivity(), result);
                    Uri uri = Uri.fromFile(new File(path));
                    mFilePathCallback4.onReceiveValue(uri);
                } else {
                    mFilePathCallback4.onReceiveValue(null);
                }
            }

            if (mFilePathCallback5 != null) {
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
                if (result != null) {
                    String path = MediaUtility.getPath(getActivity(), result);
                    Uri uri = Uri.fromFile(new File(path));
                    mFilePathCallback5.onReceiveValue(new Uri[]{uri});
                } else {
                    mFilePathCallback5.onReceiveValue(null);
                }
            }

            mFilePathCallback4 = null;
            mFilePathCallback5 = null;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save instance state
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);

        // Save WebView state
        WebView webView = (WebView) mRootView.findViewById(R.id.fragment_main_webview);
        webView.saveState(outState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Action Bar Menu
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onRefresh() {
        runTaskCallback(new Runnable() {
            @Override
            public void run() {
                refreshData();
            }
        });
    }


    private void handleArguments(Bundle arguments) {
        if (arguments.containsKey(ARGUMENT_URL)) {
            mUrl = arguments.getString(ARGUMENT_URL);
            mLocal = mUrl.contains("file://");
        }
    }

    // Load URL
    private void loadData() {
        if (NetworkInf.isOnline(getActivity()) || mLocal) {
            showProgress();

            WebView webView = (WebView) mRootView.findViewById(R.id.fragment_main_webview);
            webView.loadUrl(mUrl);
        } else {
            showOffline();
        }
    }


    // Reload
    public void refreshData() {
        if (NetworkInf.isOnline(getActivity()) || mLocal) {
            showActionBarProgress(true);

            WebView webView = (WebView) mRootView.findViewById(R.id.fragment_main_webview);
            webView.loadUrl(webView.getUrl());
        } else {
            showActionBarProgress(false);
            Toast.makeText(getActivity(), R.string.global_offline_toast, Toast.LENGTH_LONG).show();
        }
    }


    private void showActionBarProgress(boolean visible) {
	// Swipe to refresh
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.container_swipe_refresh);

        swipeRefreshLayout.setRefreshing(visible);
        swipeRefreshLayout.setEnabled(!visible);

        mActionBarProgress = visible;
    }


    private void showContent() {
        ViewGroup containerContent = (ViewGroup) mRootView.findViewById(R.id.container_content);
        ViewGroup containerProgress = (ViewGroup) mRootView.findViewById(R.id.container_progress);
        ViewGroup containerOffline = (ViewGroup) mRootView.findViewById(R.id.container_offline);
        ViewGroup containerEmpty = (ViewGroup) mRootView.findViewById(R.id.container_empty);
        containerContent.setVisibility(View.VISIBLE);
        containerProgress.setVisibility(View.GONE);
        containerOffline.setVisibility(View.GONE);
        containerEmpty.setVisibility(View.GONE);
        mViewState = ViewState.CONTENT;
    }


    private void showContent(final long delay) {
        final Handler timerHandler = new Handler();
        final Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                runTaskCallback(new Runnable() {
                    public void run() {
                        if (getActivity() != null && mRootView != null) {
                            showContent();
                        }
                    }
                });
            }
        };
        timerHandler.postDelayed(timerRunnable, delay);
    }

    // Progressbar
    private void showProgress() {
        ViewGroup containerContent = (ViewGroup) mRootView.findViewById(R.id.container_content);
        ViewGroup containerProgress = (ViewGroup) mRootView.findViewById(R.id.container_progress);
        ViewGroup containerOffline = (ViewGroup) mRootView.findViewById(R.id.container_offline);
        ViewGroup containerEmpty = (ViewGroup) mRootView.findViewById(R.id.container_empty);
        containerContent.setVisibility(View.GONE);
        containerProgress.setVisibility(View.VISIBLE);
        containerOffline.setVisibility(View.GONE);
        containerEmpty.setVisibility(View.GONE);
        mViewState = ViewState.PROGRESS;
    }

    // Offline
    private void showOffline() {
        ViewGroup containerContent = (ViewGroup) mRootView.findViewById(R.id.container_content);
        ViewGroup containerProgress = (ViewGroup) mRootView.findViewById(R.id.container_progress);
        ViewGroup containerOffline = (ViewGroup) mRootView.findViewById(R.id.container_offline);
        ViewGroup containerEmpty = (ViewGroup) mRootView.findViewById(R.id.container_empty);
        containerContent.setVisibility(View.GONE);
        containerProgress.setVisibility(View.GONE);
        containerOffline.setVisibility(View.VISIBLE);
        containerEmpty.setVisibility(View.GONE);
        mViewState = ViewState.OFFLINE;
    }

    // Not found/Empty
    private void showEmpty() {
        ViewGroup containerContent = (ViewGroup) mRootView.findViewById(R.id.container_content);
        ViewGroup containerProgress = (ViewGroup) mRootView.findViewById(R.id.container_progress);
        ViewGroup containerOffline = (ViewGroup) mRootView.findViewById(R.id.container_offline);
        ViewGroup containerEmpty = (ViewGroup) mRootView.findViewById(R.id.container_empty);
        containerContent.setVisibility(View.GONE);
        containerProgress.setVisibility(View.GONE);
        containerOffline.setVisibility(View.GONE);
        containerEmpty.setVisibility(View.VISIBLE);
        mViewState = ViewState.EMPTY;
    }


    private void renderView() {
        final WebView webView = (WebView) mRootView.findViewById(R.id.fragment_main_webview);
        final AdView adView = (AdView) mRootView.findViewById(R.id.fragment_main_adview);

        // WebView Settings
        webView.getSettings().setBuiltInZoomControls(false); // Hide built-in zoom
        webView.getSettings().setSupportZoom(true); // Support zoom
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); // Disable caching
        webView.getSettings().setJavaScriptEnabled(true); // Enable JavaScript Support
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setBackgroundColor(getResources().getColor(R.color.global_bg_front));
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); // Required for Froyo
        webView.setWebChromeClient(new WebChromeClient() {
	    // File Chooser
            public void openFileChooser(ValueCallback<Uri> filePathCallback) {
                mFilePathCallback4 = filePathCallback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "File Chooser"), REQUEST_FILE_PICKER);
            }

	    // File Chooser
            public void openFileChooser(ValueCallback filePathCallback, String acceptType) {
                mFilePathCallback4 = filePathCallback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "File Chooser"), REQUEST_FILE_PICKER);
            }

	    // File Chooser
            public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture) {
                mFilePathCallback4 = filePathCallback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "File Chooser"), REQUEST_FILE_PICKER);
            }

	    // File Chooser
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                mFilePathCallback5 = filePathCallback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "File Chooser"), REQUEST_FILE_PICKER);
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            private boolean mSuccess = true;


            @Override
            public void onPageFinished(final WebView view, final String url) {
                runTaskCallback(new Runnable() {
                    public void run() {
                        if (getActivity() != null && mSuccess) {
                            showContent(500); // "Animation"
                            showActionBarProgress(false);
                        }
                    }
                });
            }


	    // Error behaviour - returns empty viewstate
            @Override
            public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
                runTaskCallback(new Runnable() {
                    public void run() {
                        if (getActivity() != null) {
                            mSuccess = false;
                            showEmpty();
                            showActionBarProgress(false);
                        }
                    }
                });
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (DownloadUtil.isDownloadableFile(url)) {
                    Toast.makeText(getActivity(), R.string.fragment_main_downloading, Toast.LENGTH_LONG).show();
                    DownloadUtil.downloadFile(getActivity(), url, DownloadUtil.getFileName(url));
                    return true;
                } else if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                    boolean external = isLinkExternal(url);
                    boolean internal = isLinkInternal(url);
                    if (!external && !internal) {
                        external = WebViewConfig.OPEN_LINKS_IN_EXTERNAL_BROWSER;
                    }

                    if (external) {
                        startWebActivity(url);
                        return true;
                    } else {
                        showActionBarProgress(true);
                        return false;
                    }
                } else if (url != null && url.startsWith("mailto:")) {
                    MailTo mailTo = MailTo.parse(url);
                    startEmailActivity(mailTo.getTo(), mailTo.getSubject(), mailTo.getBody());
                    return true;
                } else if (url != null && url.startsWith("tel:")) {
                    startCallActivity(url);
                    return true;
                } else if (url != null && url.startsWith("sms:")) {
                    startSmsActivity(url);
                    return true;
                } else if (url != null && url.startsWith("geo:")) {
                    startMapSearchActivity(url);
                    return true;
                } else {
                    return false;
                }
            }
        });
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    WebView webView = (WebView) v;

                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()) {
                                webView.goBack();
                                return true;
                            }
                            break;
                    }
                }

                return false;
            }
        });
        webView.requestFocus(View.FOCUS_DOWN);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }

                return false;
            }
        });

        // AdMob
        if (WebViewConfig.ADMOB && NetworkInf.isOnline(getActivity())) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(getString(R.string.admob_test_device_id))
                    .build();
            adView.loadAd(adRequest);
            adView.setVisibility(View.VISIBLE);
        } else {
            adView.setVisibility(View.GONE);
        }
    }


    private void controlBack() {
        final WebView webView = (WebView) mRootView.findViewById(R.id.fragment_main_webview);
        if (webView.canGoBack()) webView.goBack();
    }


    private void controlForward() {
        final WebView webView = (WebView) mRootView.findViewById(R.id.fragment_main_webview);
        if (webView.canGoForward()) webView.goForward();
    }


    private void controlStop() {
        final WebView webView = (WebView) mRootView.findViewById(R.id.fragment_main_webview);
        webView.stopLoading();
    }


    private void controlReload() {
        final WebView webView = (WebView) mRootView.findViewById(R.id.fragment_main_webview);
        webView.reload();
    }


    private void startWebActivity(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
        }
    }

    // Email
    private void startEmailActivity(String email, String subject, String text) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("mailto:");
            builder.append(email);

            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(builder.toString()));
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
        }
    }

    // Phone
    private void startCallActivity(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
        }
    }

    // SMS
    private void startSmsActivity(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
        }
    }

    // Maps
    private void startMapSearchActivity(String url) {
        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
        }
    }

    // External link
    private boolean isLinkExternal(String url) {
        for (String rule : WebViewConfig.LINKS_OPENED_IN_EXTERNAL_BROWSER) {
            if (url.contains(rule)) return true;
        }
        return false;
    }

    // Internal link
    private boolean isLinkInternal(String url) {
        for (String rule : WebViewConfig.LINKS_OPENED_IN_INTERNAL_WEBVIEW) {
            if (url.contains(rule)) return true;
        }
        return false;
    }
}
