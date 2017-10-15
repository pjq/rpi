package me.pjq.rpicar;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class CaptureVideoFragment extends Fragment {
    WebView webView;
    private static final String username = "pjq";
    private static final String passwd = "pjq";

    public CaptureVideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CaptureVideoFragment newInstance() {
        CaptureVideoFragment fragment = new CaptureVideoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_capture_video, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        webView = (WebView) view.findViewById(R.id.webview);
        webView.loadUrl(CarControllerApiService.Config.CAPTURE_VIDEO_URL());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request.getUrl().getEncodedPath().endsWith("avi")) {
                    String url = request.getUrl().toString();
                    url =url.replace("http://", "http://" + username + ":" + passwd + "@");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "video/*");
                    startActivity(intent);

                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, request);
                }
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                handler.proceed(username, passwd);
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle(R.string.capturevideo);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
