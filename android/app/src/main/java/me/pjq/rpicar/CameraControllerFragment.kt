package me.pjq.rpicar

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.aliyun.iot.demo.iothub.SimpleClient4IOT
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import me.pjq.rpicar.aliyun.IoT
import me.pjq.rpicar.models.CarAction
import me.pjq.rpicar.models.SensorStatus
import me.pjq.rpicar.models.WeatherItem
import me.pjq.rpicar.realm.Settings
import me.pjq.rpicar.utils.Logger
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraControllerFragment : Fragment(), View.OnClickListener, View.OnTouchListener, SimpleClient4IOT.Listener {
    override fun onUpdate(sensorStatus: SensorStatus?) {
        this.sensorStatus = sensorStatus;
        if (null == activity || isDetached) {
            return
        }

        activity.runOnUiThread { updateStatus() }
    }

    override fun onUpdate(weatherItem: WeatherItem?) {
        this.weatherItem = weatherItem;
        if (null == activity || isDetached) {
            return
        }
        activity.runOnUiThread { updateStatus() }
    }

    internal var left: ImageView? = null
    internal var right: ImageView? = null
    internal var up: ImageView? = null
    internal var down: ImageView? = null
    internal var auto: View? = null
    internal var stop: View? = null
    internal var webView: WebView? = null
    internal var cameraOn: TextView? = null
    internal var angleAdd: Button? = null
    internal var angleSub: Button? = null


    internal var motion_detect: ImageView? = null
    internal var ultrasound: ImageView? = null
    internal var obstacles1: ImageView? = null
    internal var obstacles2: ImageView? = null
    internal var obstacles3: ImageView? = null
    internal var obstacles4: ImageView? = null
    internal var carBody: ImageView? = null

    var angleValue: Int = 0
    internal var relayOn: TextView? = null
    var isRelayOn: Boolean = false

    internal var weatherStatus: TextView? = null

    internal lateinit var apiService: CarControllerApiService

    internal var disposable: Disposable? = null
    internal var disposable2: Disposable? = null
    internal var sensorStatus: SensorStatus? = null
    internal var weatherItem: WeatherItem? = null
    internal var monitor: Monitor? = null

    val enableIoT: Boolean = true
    var iot: IoT? = null

    private val settings: Settings?
        get() {
            DataManager.init(activity.applicationContext)
            val settings = DataManager.realm.where(Settings::class.java).findFirst()
            if (null == settings) {
                DataManager.realm.executeTransaction {
                    val newSetting = DataManager.realm.createObject(Settings::class.java)
                    newSetting.setDuration(100)
                    newSetting.setSpeed(10)
                }

                return settings
            } else {
                return settings
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iot = IoT.instance;

        monitor = Monitor.instance
        monitor!!.init(this);
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        activity.title = "Camera"
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView(view: View) {
        stop = view.findViewById(R.id.stop)
        left = view.findViewById(R.id.left) as ImageView
        right = view.findViewById(R.id.right) as ImageView
        up = view.findViewById(R.id.up) as ImageView
        auto = view.findViewById(R.id.auto)
        down = view.findViewById(R.id.down) as ImageView
        webView = view.findViewById(R.id.webview) as WebView
        cameraOn = view.findViewById(R.id.cameraOn) as TextView
        weatherStatus = view.findViewById(R.id.weatherStatus) as TextView
        relayOn = view.findViewById(R.id.relayOn) as TextView
        angleAdd = view.findViewById(R.id.angle_add) as Button
        angleSub = view.findViewById(R.id.angle_sub) as Button

        motion_detect = view.findViewById(R.id.motion_detect) as ImageView
        ultrasound = view.findViewById(R.id.ultrasound) as ImageView
        obstacles1 = view.findViewById(R.id.obstacles1) as ImageView
        obstacles2 = view.findViewById(R.id.obstacles2) as ImageView
        obstacles3 = view.findViewById(R.id.obstacles3) as ImageView
        obstacles4 = view.findViewById(R.id.obstacles4) as ImageView
        carBody = view.findViewById(R.id.carBody) as ImageView

        stop?.setOnClickListener(this)
        left?.setOnClickListener(this)
        right?.setOnClickListener(this)
        up?.setOnClickListener(this)
        auto?.setOnClickListener(this)
        down?.setOnClickListener(this)
        cameraOn?.setOnClickListener(this)
        angleAdd?.setOnClickListener(this)
        angleSub?.setOnClickListener(this)
        relayOn?.setOnClickListener(this)

        //        left.setOnTouchListener(this);
        //        right.setOnTouchListener(this);
        //        up.setOnTouchListener(this);
        //        down.setOnTouchListener(this);
        //        stop.setOnTouchListener(this);

        webView?.settings!!.javaScriptEnabled = true
        //        webView.getSettings().setSupportZoom(true);
        webView?.settings!!.builtInZoomControls = true
        webView?.settings!!.loadWithOverviewMode = true
        webView?.settings!!.useWideViewPort = true

        apiService = CarControllerApiService.instance
        webView?.loadUrl(CarControllerApiService.Config.STREAM_URL())
        //        hideSoftKeyboard();

//        initWeatherStatus()
//        getSensorStatus()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_camera_controller, container, false)
        initView(view)

        return view
    }

    private fun getSensorStatus() {
        val scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
        disposable2 = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .flatMap(object : Function<Long, ObservableSource<SensorStatus>> {
                    @Throws(Exception::class)
                    override fun apply(t: Long): ObservableSource<SensorStatus>? {
                        return apiService?.api!!.getSensorStatus()
                    }
                })
                .doOnError { throwable -> Logger.log(TAG, throwable.toString()) }
                .retryWhen { throwablObservable -> throwablObservable.flatMap<Any> { Observable.timer(2, TimeUnit.SECONDS) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler)
                .subscribe({ status ->
                    Logger.log(TAG, status.toString())
                    sensorStatus = status
                    updateStatus()
                }) { throwable -> Logger.log(TAG, throwable.toString()) }
    }

    private fun updateStatus() {
        weatherStatus?.text = ""
        if (null != weatherItem) {
            val value = weatherItem!!.date + " PM2.5 " + weatherItem!!.pm25 + " " + weatherItem!!.temperature + "°C " + weatherItem!!.humidity + "%"
            Logger.log(TAG, value)
            weatherStatus?.text = value
        }

        if (null != sensorStatus) {
            weatherStatus?.append("\nDistance(cm) " + sensorStatus!!.distance + "\n" + sensorStatus!!.obstacles!!.toString() + "\nPeople Detected " + sensorStatus!!.motion_detected)
            updateRelayOnStatus(sensorStatus!!.relay_on)
        }

        updateCarStatus()
    }

    private fun updateCarStatus() {
        if (sensorStatus?.relay_on!!) {
            carBody?.setBackgroundColor(resources.getColor(R.color.power_on));
        } else {
            carBody?.setBackgroundColor(resources.getColor(R.color.power_off));
        }

        if (sensorStatus?.motion_detected!!) {
            motion_detect?.setBackgroundColor(resources.getColor(R.color.motion_detected_on));
        } else {
            motion_detect?.setBackgroundColor(resources.getColor(R.color.white));
        }

        if (sensorStatus?.obstacles?.obstacle1!!) {
            obstacles1?.setBackgroundColor(resources.getColor(R.color.obstacles_on))
        } else {
            obstacles1?.setBackgroundColor(resources.getColor(R.color.obstacles_off))
        }

        if (sensorStatus?.obstacles?.obstacle2!!) {
            obstacles2?.setBackgroundColor(resources.getColor(R.color.obstacles_on))
        } else {
            obstacles2?.setBackgroundColor(resources.getColor(R.color.obstacles_off))
        }

        if (sensorStatus?.obstacles?.obstacle3!!) {
            obstacles3?.setBackgroundColor(resources.getColor(R.color.obstacles_on))
        } else {
            obstacles3?.setBackgroundColor(resources.getColor(R.color.obstacles_off))
        }

        if (sensorStatus?.obstacles?.obstacle4!!) {
            obstacles4?.setBackgroundColor(resources.getColor(R.color.obstacles_on))
        } else {
            obstacles4?.setBackgroundColor(resources.getColor(R.color.obstacles_off))
        }

        var params: RelativeLayout.LayoutParams = ultrasound?.layoutParams as RelativeLayout.LayoutParams;

        params.height = sensorStatus?.distance!!.toInt()
//        ultrasound?.layoutParams = params;
//        ultrasound?.scaleY = sensorStatus?.distance!!

        scaleView(ultrasound!!, .0f, (sensorStatus?.distance!!/100.0).toFloat())
    }

    private fun scaleView(v: View, startScale: Float, endScale: Float) {
        var anim: Animation = ScaleAnimation(
                1f, 1f, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true) // Needed to keep the result of the animation
        anim.setDuration(200)
        v.startAnimation(anim)
    }

    private fun initWeatherStatus() {
        weatherStatus?.setOnClickListener {
            val intent = Intent()
            intent.setClass(activity, TemperatureChartTimeActivity::class.java)
            activity.startActivity(intent)
        }

        val scheduler = Schedulers.from(Executors.newSingleThreadExecutor())

        val settings = settings
        val weatherJson = settings!!.getWeatherJson()

        if (weatherJson != null) {
            val weatherItems = Arrays.asList(*Gson().fromJson(weatherJson, Array<WeatherItem>::class.java))
            val item = weatherItems[0] as WeatherItem
            val value = item.date + " PM2.5 " + item.pm25 + " " + item.temperature + "°C " + item.humidity + "%"
            Logger.log(TAG, value)

            weatherStatus?.text = value
        }

        disposable = Observable.interval(0, 60, TimeUnit.SECONDS)
                .flatMap(object : Function<Long, ObservableSource<List<WeatherItem>>> {
                    @Throws(Exception::class)
                    override fun apply(t: Long): ObservableSource<List<WeatherItem>>? {
                        return apiService?.api!!.getWeatherItems(0, 2)
                    }
                })
                .doOnError { throwable -> Logger.log(TAG, throwable.toString()) }
                .retryWhen { throwablObservable -> throwablObservable.flatMap<Any> { Observable.timer(2, TimeUnit.SECONDS) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler)
                .subscribe({ weatherItems ->
                    weatherItem = weatherItems[0]
                    updateStatus()
                }) { throwable -> Logger.log(TAG, throwable.toString()) }
    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        //        if (view != null) {
        //            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        //        }
    }

    override fun onDetach() {
        super.onDetach()

        val carAction = CarAction()
        carAction.action = "stop"
        sendCommand(carAction)
        Monitor.instance.home4IOT.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        disposable2?.dispose()
    }

    override fun onClick(v: View) {
        val id = v.id

        //        CarControllerApiService.API_URL = url.getText().toString();

        var carAction = CarAction()
        //set the default duration to 1 second.
        carAction.duration = settings!!.duration.toLong()
        carAction.speed = settings!!.speed

        when (id) {
            R.id.stop -> carAction.action = "stop"

            R.id.left -> carAction.action = "left"

            R.id.right -> carAction.action = "right"

            R.id.up -> carAction.action = "up"

            R.id.down -> carAction.action = "down"

            R.id.auto -> {
                if (carAction.speed > 40) {
                    carAction.speed = 40
                }

                carAction.action = "auto_drive"
            }

            R.id.cameraOn -> webView?.reload()

            R.id.angle_add -> {
                carAction.action = "angle"
                angleValue += 10;
                if (angleValue > 180) {
                    angleValue = 0;
                }

                angleAdd!!.setText("Angle: " + angleValue);
                angleSub!!.setText("Angle: " + angleValue);
                carAction.angle = angleValue;
            }

            R.id.angle_sub -> {
                carAction.action = "angle"
                angleValue -= 10;
                if (angleValue < 0) {
                    angleValue = 180;
                }

                angleAdd!!.setText("Angle: " + angleValue);
                angleSub!!.setText("Angle: " + angleValue);
                carAction.angle = angleValue;
            }

            R.id.relayOn -> {
                carAction = onRelayClick(carAction)
            }
        }

        sendCommand(carAction)
    }

    private fun onRelayClick(carAction: CarAction): CarAction {
        isRelayOn = !isRelayOn
        if (isRelayOn) {
            carAction.action = "relay_on"
        } else {
            carAction.action = "relay_off"
        }

        updateRelayOnStatus(isRelayOn)

        return carAction
    }

    private fun updateRelayOnStatus(on: Boolean) {
        if (on) {
            relayOn!!.setText("Power On")
        } else {
            relayOn!!.setText("Power Off")
        }
    }


    private fun sendCommand(carAction: CarAction) {
        var action: String = Gson().toJson(carAction)
        print(action)

        if (enableIoT) {
//            IoT.instance.send(action)
            Monitor.instance.home4IOT.sendMessage(action)

            return
        }

        apiService?.api.sendCommand(carAction)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ }) { }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val id = v.id

        val carAction = CarAction()
        //set the default duration to 1 second.
        if (event.action == MotionEvent.ACTION_DOWN) {
            carAction.duration = 1000 * 5

            when (id) {
                R.id.stop -> carAction.action = "stop"

                R.id.left -> carAction.action = "left"

                R.id.right -> carAction.action = "right"

                R.id.up -> carAction.action = "up"

                R.id.down -> carAction.action = "down"
            }
        } else {
            carAction.action = "stop"
        }

        Logger.log(TAG, carAction.action)

        apiService?.api!!.sendCommand(carAction)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ }) { }

        return false
    }

    companion object {
        private val TAG = "CameraController"

        fun newInstance(): CameraControllerFragment {
            val fragment = CameraControllerFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}
