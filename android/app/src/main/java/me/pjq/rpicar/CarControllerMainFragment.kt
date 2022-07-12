package me.pjq.rpicar

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
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.UiThread
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import me.pjq.rpicar.aliyun.IoT
import me.pjq.rpicar.databinding.FragmentCameraControllerBinding
import me.pjq.rpicar.models.CarAction
import me.pjq.rpicar.models.SensorStatus
import me.pjq.rpicar.models.WeatherItem
import me.pjq.rpicar.realm.Settings
import me.pjq.rpicar.utils.Log
import me.pjq.rpicar.utils.Logger
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * The car main controller UI, it can do the actions: up/down/left/right and also the camera angel rotate.
 */
class CarControllerMainFragment : Fragment(), View.OnClickListener, View.OnTouchListener {
    var angleValue: Int = 0
    var isRelayOn: Boolean = false

    lateinit var apiService: CarControllerApiService
    var disposable: Disposable? = null
    var disposable2: Disposable? = null
    var sensorStatus: SensorStatus? = null
    var weatherItem: WeatherItem? = null
    lateinit var monitor: Monitor
    val enableIoT: Boolean = false
    lateinit var iot: IoT

    private var _binding: FragmentCameraControllerBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    fun Fragment.isAlive(): Boolean {
        return null != activity && isAdded
    }

//    @UiThread
//    fun onUpdate(sensorStatus: SensorStatus) {
//        this.sensorStatus = sensorStatus;
//        if (!isAlive()) {
//            return
//        }
//
//        runOnUiThread { updateStatus() }
//    }
//
//    override fun onUpdate(weatherItem: WeatherItem) {
//        this.weatherItem = weatherItem;
//        if (!isAlive()) {
//            return
//        }
//
//        runOnUiThread { updateStatus() }
//    }

    private val settings: Settings?
        get() {
            DataManager.init(activity.applicationContext)
            val settings = DataManager.realm.where(Settings::class.java).findFirst()
            if (null == settings) {
                DataManager.realm.executeTransaction {
                    val newSetting = DataManager.realm.createObject(Settings::class.java)
                    newSetting.setDuration(500)
                    newSetting.setSpeed(30)
                }

                return settings
            } else {
                return settings
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iot = IoT.instance

        monitor = Monitor.instance
//        monitor.init(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        activity.title = "Camera"
    }

    private fun initView() {
        binding.stop.setOnClickListener(this)
        binding.left.setOnClickListener(this)
        binding.right.setOnClickListener(this)
        binding.up.setOnClickListener(this)
        binding.auto.setOnClickListener(this)
        binding.down.setOnClickListener(this)
        binding.cameraOn.setOnClickListener(this)
//        binding.angle_add.setOnClickListener(this)
//        binding.angle_add.setOnClickListener(this)
        binding.relayOn.setOnClickListener(this)

        binding.radarview.start()

        //        left.setOnTouchListener(this);
        //        right.setOnTouchListener(this);
        //        up.setOnTouchListener(this);
        //        down.setOnTouchListener(this);
        //        stop.setOnTouchListener(this);

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.useWideViewPort = true

        apiService = CarControllerApiService.instance
        binding.webView?.loadUrl(CarControllerApiService.Config.STREAM_URL())
        //        hideSoftKeyboard();

//        initWeatherStatus()
//        getSensorStatus()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_camera_controller, container, false)

        _binding = FragmentCameraControllerBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initView()
    }

    private fun getSensorStatus() {
        val scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
        disposable2 = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
            .flatMap(object : Function<Long, ObservableSource<SensorStatus>> {
                @Throws(Exception::class)
                override fun apply(t: Long): ObservableSource<SensorStatus>? {
                    return apiService.api.getSensorStatus()
                }
            })
            .doOnError { throwable -> Logger.log(TAG, throwable.toString()) }
            .retryWhen { throwablObservable ->
                throwablObservable.flatMap<Any> {
                    Observable.timer(
                        2,
                        TimeUnit.SECONDS
                    )
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler)
            .subscribe({ status ->
                Logger.log(TAG, status.toString())
                sensorStatus = status
                updateStatus()
            }) { throwable -> Logger.log(TAG, throwable.toString()) }
    }

    private fun updateStatus() {
        binding.weatherStatus?.text = ""
        if (null != weatherItem) {
            val value =
                weatherItem!!.date + "\nPM2.5 " + weatherItem!!.pm25 + " " + weatherItem!!.temperature + "°C " + weatherItem!!.humidity + "%"
            Logger.log(TAG, value)
            binding.weatherStatus?.text = value
        }

        if (null != sensorStatus) {
//            weatherStatus?.append("\nDistance(cm) " + sensorStatus!!.distance + "\n" + sensorStatus!!.obstacles!!.toString() + "\nPeople Detected " + sensorStatus!!.motion_detected)
            binding.weatherStatus?.append("\nUltra Sound(cm) " + sensorStatus!!.distance)
            updateRelayOnStatus(sensorStatus?.relay_on!!)
        }

        updateCarStatus()
    }

    private fun updateCarStatus() {
        if (null == sensorStatus) {
            return
        }
        if (sensorStatus?.relay_on!!) {
            binding.carBody.setImageResource(R.color.power_on);
            if (binding.radarview.visibility == View.GONE) {
                toast(context, "The car is going be active")
            }
            binding.radarview.visibility = View.VISIBLE
        } else {
            binding.carBody.setImageResource(R.color.power_off);
            if (binding.radarview.visibility == View.VISIBLE) {
                toast(context, "The car is going to sleep status")
            }
            binding.radarview.visibility = View.GONE
        }

//        if (sensorStatus?.motion_detected!!) {
//            binding.motion_detect.setImageResource(R.color.motion_detected_on);
//            binding.motion_detect.visibility = View.VISIBLE
//        } else {
//            binding.motion_detect.setImageResource(R.color.white);
//            binding.motion_detect.visibility = View.GONE
//        }

        if (sensorStatus?.obstacles?.obstacle1!!) {
            binding.obstacles1.setImageResource(R.color.obstacles_on);
        } else {
            binding.obstacles1.setImageResource(R.color.obstacles_off);
        }

        if (sensorStatus?.obstacles?.obstacle2!!) {
            binding.obstacles2.setImageResource(R.color.obstacles_on);
        } else {
            binding.obstacles2.setImageResource(R.color.obstacles_off);
        }

        if (sensorStatus?.obstacles?.obstacle3!!) {
            binding.obstacles3.setImageResource(R.color.obstacles_on);
        } else {
            binding.obstacles3.setImageResource(R.color.obstacles_off);
        }

        if (sensorStatus?.obstacles?.obstacle4!!) {
            binding.obstacles4?.setImageResource(R.color.obstacles_on);
        } else {
            binding.obstacles4?.setImageResource(R.color.obstacles_off);
        }

        var params: RelativeLayout.LayoutParams =
            binding.ultrasound?.layoutParams as RelativeLayout.LayoutParams;

        params.height = sensorStatus?.distance!!.toInt()
//        ultrasound?.layoutParams = params;
//        ultrasound?.scaleY = sensorStatus?.distance!!

        scaleView(binding.ultrasound, 1.0f, (sensorStatus?.distance!! / 200.0).toFloat())

    }

    private fun scaleView(v: View, startScale: Float, endScale: Float) {
        Log.log(TAG, "startScale: " + startScale + " endScale: " + endScale)
        var anim: Animation = ScaleAnimation(
            1f, 1f, // Start and end values for the X axis scaling
            startScale, endScale, // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, 1f
        ); // Pivot point of Y scaling
        anim.setFillAfter(false) // Needed to keep the result of the animation
        anim.setDuration(100)
        v.startAnimation(anim)
    }

    private fun initWeatherStatus() {
        binding.weatherStatus?.setOnClickListener {
            val intent = Intent()
            intent.setClass(activity, TemperatureChartTimeActivity::class.java)
            activity.startActivity(intent)
        }

        val scheduler = Schedulers.from(Executors.newSingleThreadExecutor())

        val settings = settings
        val weatherJson = settings?.getWeatherJson()

        if (weatherJson != null) {
            val weatherItems =
                Arrays.asList(*Gson().fromJson(weatherJson, Array<WeatherItem>::class.java))
            val item = weatherItems[0] as WeatherItem
            val value =
                item.date + " PM2.5 " + item.pm25 + " " + item.temperature + "°C " + item.humidity + "%"
            Logger.log(TAG, value)

            binding.weatherStatus?.text = value
        }

        disposable = Observable.interval(0, 60, TimeUnit.SECONDS)
            .flatMap(object : Function<Long, ObservableSource<List<WeatherItem>>> {
                @Throws(Exception::class)
                override fun apply(t: Long): ObservableSource<List<WeatherItem>>? {
                    return apiService.api.getWeatherItems(0, 2)
                }
            })
            .doOnError { throwable -> Logger.log(TAG, throwable.toString()) }
            .retryWhen { throwablObservable ->
                throwablObservable.flatMap<Any> {
                    Observable.timer(
                        2,
                        TimeUnit.SECONDS
                    )
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler)
            .subscribe({ weatherItems ->
                weatherItem = weatherItems[0]
                updateStatus()
            }) { throwable -> Logger.log(TAG, throwable.toString()) }
    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
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
        var carAction = CarAction()
        //set the default duration to 1 second.
        carAction.duration = settings?.duration!!.toLong()
        carAction.speed = settings?.speed!!
        when (v.id) {
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

            R.id.cameraOn -> binding.webView.reload()

            R.id.angle_add -> {
                carAction.action = "angle"
                angleValue += 10;
                if (angleValue > 180) {
                    angleValue = 0;
                }

//                binding.angle_add!!.setText("" + angleValue + " +");
//                binding.angle_sub!!.setText("" + angleValue + " -");
                carAction.angle = angleValue;
            }

            R.id.angle_sub -> {
                carAction.action = "angle"
                angleValue -= 10;
                if (angleValue < 0) {
                    angleValue = 180;
                }

//                binding.angle_add!!.setText("Angle: " + angleValue);
//                binding.angle_sub!!.setText("Angle: " + angleValue);
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
            binding.relayOn.setText("Power On")
        } else {
            binding.relayOn.setText("Power Off")
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

        apiService.api.sendCommand(carAction)
            .subscribeOn(Schedulers.io())
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

        apiService.api.sendCommand(carAction)
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ }) { }

        return false
    }

    companion object {
        private val TAG = "CameraController"

        fun newInstance(): CarControllerMainFragment {
            val fragment = CarControllerMainFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        fun toast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}

private fun SimpleClient4IOT.close() {
    TODO("Not yet implemented")
}
