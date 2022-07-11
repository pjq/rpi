package me.pjq.rpicar

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.HttpAuthHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import me.pjq.rpicar.databinding.FragmentCaptureVideoBinding
import me.pjq.rpicar.utils.Logger

class CaptureVideoFragment : Fragment(), MainNavigationActivity.OnBackKeyListener {
    val TAG: String = "Monitor"
    private var _binding: FragmentCaptureVideoBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_capture_video, container, false)
        _binding = FragmentCaptureVideoBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initView()
    }

    private fun initView() {
        var videoUrl = CarControllerApiService.Config.CAPTURE_VIDEO_URL()
        Logger.log(TAG, videoUrl)
        binding.webView.loadUrl(videoUrl)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.setSupportZoom(true)
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.useWideViewPort = true

        binding.webView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                if (request.url.encodedPath!!.endsWith("avi")) {
                    var url = request.url.toString()
                    url = url.replace("http://", "http://$username:$passwd@")
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(Uri.parse(url), "video/*")
                    startActivity(intent)

                    return true
                } else if (request.url.encodedPath!!.endsWith("disablejpg")) {
                    var url = request.url.toString()
                    url = url.replace("http://", "http://$username:$passwd@")
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(Uri.parse(url), "image/*")
                    startActivity(intent)

                    return true
                } else {
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }

            override fun onReceivedHttpAuthRequest(
                view: WebView,
                handler: HttpAuthHandler,
                host: String,
                realm: String
            ) {
                handler.proceed(username, passwd)
            }
        })
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity.setTitle(R.string.capturevideo)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onBackKeyDown(): Boolean {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
            return true
        }

        return false
    }

    companion object {
        private val username = "pjq"
        private val passwd = "pjq"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment AboutFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): CaptureVideoFragment {
            val fragment = CaptureVideoFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
