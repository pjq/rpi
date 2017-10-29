package me.pjq.rpicar

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import me.pjq.rpicar.models.CarAction
import me.pjq.rpicar.realm.Settings
import me.pjq.rpicar.utils.Logger
import me.pjq.rpicar.utils.SnackbarUtil

import android.content.ContentValues.TAG


class SettingsFragment : Fragment() {
    internal lateinit var url: EditText
    internal lateinit var durationSeekBar: SeekBar
    internal lateinit var seekbarValue: TextView
    internal lateinit var speed: SeekBar
    internal lateinit var speedValue: TextView
    internal lateinit var apiService: CarControllerApiService

    private val duration: Long
        get() = (durationSeekBar.progress.toFloat() / 100.toFloat() * MAX_DURATION.toFloat() * 1000f).toLong()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        apiService = CarControllerApiService.instance
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        initView(view)

        return view
    }

    private fun initView(view: View) {
        url = view.findViewById(R.id.url) as EditText
        durationSeekBar = view.findViewById(R.id.seekbar) as SeekBar
        seekbarValue = view.findViewById(R.id.seekbarValue) as TextView
        speed = view.findViewById(R.id.speed) as SeekBar
        speedValue = view.findViewById(R.id.speedValue) as TextView

        durationSeekBar.max = (MAX_DURATION * 1000).toInt()
        durationSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                seekbarValue.text = seekBar.progress.toString() + " ms"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        speed.max = 100
        speed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                speedValue.text = getSpeed().toString() + " %"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                speedValue.text = getSpeed().toString() + " %"

                val carAction = CarAction()
                //set the default duration to 1 second.
                carAction.duration = duration
                carAction.action = "speed"
                carAction.speed = getSpeed()

                sendCommand(carAction)
            }
        })

        initSettings()
    }

    private fun initSettings() {
        DataManager.init(seekbarValue.context)
        val settings = DataManager.realm.where(Settings::class.java).findFirst()
        if (null != settings && !TextUtils.isEmpty(settings.getHost())) {
            durationSeekBar.progress = settings.getDuration()
            speed.progress = settings.getSpeed()
            url.setText(settings.getHost())
            CarControllerApiService.Config.HOST = settings.getHost()
        } else {
            durationSeekBar.progress = 10
            speed.progress = 50
            url.setText(CarControllerApiService.Config.HOST())

            DataManager.realm.executeTransaction {
                val newSetting = DataManager.realm.createObject(Settings::class.java)
                newSetting.setDuration(durationSeekBar.progress)
                newSetting.setSpeed(speed.progress)
                newSetting.setHost(url.text.toString())
            }
        }
    }

    private fun sendCommand(carAction: CarAction) {
        apiService.api.sendCommand(carAction)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ }) { }
    }


    private fun getSpeed(): Int {
        return speed.progress
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        activity.title = "Settings"
    }

    override fun onDetach() {
        super.onDetach()

        val settings = DataManager.realm.where(Settings::class.java).findFirst()
        val isHostChanged = !settings!!.getHost().equals(url.text.toString(), ignoreCase = true)

        DataManager.realm.executeTransaction {
            if (isHostChanged) {
                settings.setHost(url.text.toString())
            }
            settings.setDuration(durationSeekBar.progress)
            settings.setSpeed(speed.progress)
            Logger.log(TAG, settings.toString())
        }

        if (isHostChanged) {
            //reinit the api
            CarControllerApiService.instance.init()
        }

        SnackbarUtil.makeText(activity, "Save settings", Snackbar.LENGTH_LONG).show()
    }

    companion object {

        private val DEFAULT_DURATION: Long = 500
        private val MAX_DURATION: Long = 4

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment AboutFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
