package me.pjq.rpicar

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.Comparator
import java.util.Date
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import me.pjq.rpicar.chart.DemoBase
import me.pjq.rpicar.models.WeatherItem
import me.pjq.rpicar.realm.Settings
import me.pjq.rpicar.utils.Logger

class TemperatureChartTimeActivity : DemoBase(), OnSeekBarChangeListener {

    private var mChart: LineChart? = null
    private var mSeekBarX: SeekBar? = null
    private var tvX: TextView? = null
    private var colorPM25: TextView? = null
    private var colorTemp: TextView? = null
    private var colorHumidity: TextView? = null


    internal var disposable: Disposable? = null

    internal var weatherItems: List<WeatherItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_temperature_chart)

        supportActionBar!!.setHomeButtonEnabled(true)

        setTitle(R.string.pm25title)

        colorPM25 = findViewById(R.id.colorPM25) as TextView
        colorTemp = findViewById(R.id.colorTemp) as TextView
        colorHumidity = findViewById(R.id.colorHumidity) as TextView

        tvX = findViewById(R.id.tvXMax) as TextView
        mSeekBarX = findViewById(R.id.seekBar1) as SeekBar
        mSeekBarX!!.progress = 100
        tvX!!.text = "100"

        mSeekBarX!!.setOnSeekBarChangeListener(this)

        mChart = findViewById(R.id.chart1) as LineChart

        // no description text
        mChart!!.description.isEnabled = false

        // enable touch gestures
        mChart!!.setTouchEnabled(true)

        mChart!!.dragDecelerationFrictionCoef = 0.9f

        // enable scaling and dragging
        mChart!!.isDragEnabled = true
        mChart!!.setScaleEnabled(true)
        mChart!!.setDrawGridBackground(false)
        mChart!!.isHighlightPerDragEnabled = true

        // set an alternative background color
        mChart!!.setBackgroundColor(Color.WHITE)
        mChart!!.setViewPortOffsets(0f, 0f, 0f, 0f)

        mChart!!.invalidate()

        // get the legend (only possible after setting data)
        val l = mChart!!.legend
        l.isEnabled = false

        val xAxis = mChart!!.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
        xAxis.typeface = mTfLight
        xAxis.textSize = 10f
        xAxis.textColor = Color.GREEN
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(true)
        //        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.textColor = Color.DKGRAY
        xAxis.setCenterAxisLabels(true)
        xAxis.granularity = 1f // one hour
        xAxis.valueFormatter = object : IAxisValueFormatter {

            private val mFormat = SimpleDateFormat(" MM/dd HH:mm")

            override fun getFormattedValue(value: Float, axis: AxisBase): String {
                if (value < 0 || value >= weatherItems!!.size) {
                    return ""
                }

                val item = weatherItems!![value.toInt()]
                return mFormat.format(Date(item.timestamp))
            }
        }

        val leftAxis = mChart!!.axisLeft
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        leftAxis.typeface = mTfLight
        leftAxis.textColor = ColorTemplate.getHoloBlue()
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 500f
        leftAxis.yOffset = -9f
        //        leftAxis.setTextColor(Color.rgb(255, 192, 56));
        leftAxis.textColor = Color.DKGRAY

        val rightAxis = mChart!!.axisRight
        rightAxis.isEnabled = false

        initWeatherStatus()
        readDemoJson()
    }

    private fun readDemoJson() {
        val settings = DataManager.realm.where(Settings::class.java).findFirst()
        if (settings != null) {
            val weatherJson = settings.getWeatherJson()
            if (null != weatherJson) {

                val weatherItems = Arrays.asList(*Gson().fromJson(weatherJson, Array<WeatherItem>::class.java))
                val item = weatherItems[0] as WeatherItem
                val value = item.date + " PM2.5 " + item.pm25 + " " + item.temperature + "°C " + item.humidity + "%"
                Logger.log(TAG, value)

                setData(weatherItems)
                mChart!!.invalidate()
            }

            return
        }

        try {
            val inputStream = assets.open("demo.json")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = bufferedReader.readLine()
            val stringBuffer = StringBuffer()
            while (line != null) {
                stringBuffer.append(line + '\n')
                line = bufferedReader.readLine()
            }
            val gson = Gson()
            val weatherItems = gson.fromJson(stringBuffer.toString(), Array<WeatherItem>::class.java)
            val list = ArrayList<WeatherItem>()
            for (weatherItem in weatherItems) {
                list.add(weatherItem)
            }

            setData(list)
            mChart!!.invalidate()

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun initWeatherStatus() {
        if (null != disposable) {
            disposable!!.dispose()
        }

        val apiService = CarControllerApiService.instance
        val scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
        disposable = Observable.interval(0, 2, TimeUnit.SECONDS)
                .flatMap(object : Function<Long, ObservableSource<List<WeatherItem>>> {
                    @Throws(Exception::class)
                    override fun apply(t: Long): ObservableSource<List<WeatherItem>>? {
                        return apiService.api.getWeatherItems(0, mSeekBarX!!.progress)
                    }
                })
                .doOnError { throwable -> log(throwable.toString()) }
                .retryWhen { throwablObservable -> throwablObservable.flatMap<Any> { Observable.timer(2, TimeUnit.SECONDS) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler)
                .subscribe({ weatherItems ->
                    val item = weatherItems[0]
                    val value = item.date + "\nPM2.5 " + item.pm25 + " " + item.temperature + "°C " + item.humidity + "%"
                    log(value)
                    setData(weatherItems)
                    mChart!!.invalidate()
                    DataManager.realm.executeTransaction { realm -> realm.where(Settings::class.java).findFirst()!!.setWeatherJson(Gson().toJson(weatherItems)) }
                }) { throwable -> log(throwable.toString()) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.line, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.actionToggleValues -> {
                val sets = mChart!!.data
                        .dataSets

                for (iSet in sets) {

                    val set = iSet as LineDataSet
                    set.setDrawValues(!set.isDrawValuesEnabled)
                }

                mChart!!.invalidate()
            }
            R.id.actionToggleHighlight -> {
                if (mChart!!.data != null) {
                    mChart!!.data.isHighlightEnabled = !mChart!!.data.isHighlightEnabled
                    mChart!!.invalidate()
                }
            }
            R.id.actionToggleFilled -> {

                val sets = mChart!!.data
                        .dataSets

                for (iSet in sets) {

                    val set = iSet as LineDataSet
                    if (set.isDrawFilledEnabled)
                        set.setDrawFilled(false)
                    else
                        set.setDrawFilled(true)
                }
                mChart!!.invalidate()
            }
            R.id.actionToggleCircles -> {
                val sets = mChart!!.data
                        .dataSets

                for (iSet in sets) {

                    val set = iSet as LineDataSet
                    if (set.isDrawCirclesEnabled)
                        set.setDrawCircles(false)
                    else
                        set.setDrawCircles(true)
                }
                mChart!!.invalidate()
            }
            R.id.actionToggleCubic -> {
                val sets = mChart!!.data
                        .dataSets

                for (iSet in sets) {

                    val set = iSet as LineDataSet
                    if (set.mode == LineDataSet.Mode.CUBIC_BEZIER)
                        set.mode = LineDataSet.Mode.LINEAR
                    else
                        set.mode = LineDataSet.Mode.CUBIC_BEZIER
                }
                mChart!!.invalidate()
            }
            R.id.actionToggleStepped -> {
                val sets = mChart!!.data
                        .dataSets

                for (iSet in sets) {

                    val set = iSet as LineDataSet
                    if (set.mode == LineDataSet.Mode.STEPPED)
                        set.mode = LineDataSet.Mode.LINEAR
                    else
                        set.mode = LineDataSet.Mode.STEPPED
                }
                mChart!!.invalidate()
            }
            R.id.actionTogglePinch -> {
                if (mChart!!.isPinchZoomEnabled)
                    mChart!!.setPinchZoom(false)
                else
                    mChart!!.setPinchZoom(true)

                mChart!!.invalidate()
            }
            R.id.actionToggleAutoScaleMinMax -> {
                mChart!!.isAutoScaleMinMaxEnabled = !mChart!!.isAutoScaleMinMaxEnabled
                mChart!!.notifyDataSetChanged()
            }
            R.id.animateX -> {
                mChart!!.animateX(3000)
            }
            R.id.animateY -> {
                mChart!!.animateY(3000)
            }
            R.id.animateXY -> {
                mChart!!.animateXY(3000, 3000)
            }

            R.id.actionSave -> {
                if (mChart!!.saveToPath("title" + System.currentTimeMillis(), "")) {
                    Toast.makeText(applicationContext, "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show()
                } else
                    Toast.makeText(applicationContext, "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show()
            }// mChart.saveToGallery("title"+System.currentTimeMillis())
        }
        return true
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        tvX!!.text = "" + mSeekBarX!!.progress
        initWeatherStatus()

        // redraw
        //        mChart.invalidate();
    }

    internal fun log(log: String) {
        Log.i(TAG, log)
    }

    private fun setData(weatherItemList: List<WeatherItem>) {
        //        weatherItemList.sort(new Comparator<WeatherItem>() {
        //            @Override
        //            public int compare(WeatherItem o1, WeatherItem o2) {
        //                return (int) (o1.getTimestamp()-o2.getTimestamp());
        //            }
        //        });

        Collections.sort(weatherItemList) { o1, o2 -> (o1.timestamp - o2.timestamp).toInt() }

        weatherItems = weatherItemList

        // now in hours
        val now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis())

        val values = ArrayList<Entry>()
        val values2 = ArrayList<Entry>()
        val values3 = ArrayList<Entry>()

        val from = now.toFloat()

        val count = weatherItemList.size
        val range = 30
        //         count = hours
        val to = (now + count).toFloat()

        // increment by 1 hour
        //        for (float x = from; x < to; x++) {
        //
        //            float y = getRandom(range, 50);
        //            values.add(new Entry(x, y)); // add one entry per hour
        //        }


        var i = 0
        for (item in weatherItemList) {
            //            values.add(new Entry(TimeUnit.MILLISECONDS.toMinutes(item.getTimestamp() / 1000), item.getPm25()));
            values.add(Entry(i.toFloat(), item.pm25.toFloat()))
            values2.add(Entry(i.toFloat(), item.temperature.toFloat()))
            values3.add(Entry(i.toFloat(), item.humidity.toFloat()))
            i++
        }

        // create a dataset and give it a type
        val set1 = LineDataSet(values, "PM 2.5")
        set1.axisDependency = AxisDependency.LEFT
        set1.color = ColorTemplate.getHoloBlue()
        set1.valueTextColor = ColorTemplate.getHoloBlue()
        set1.lineWidth = 1.5f
        set1.setDrawCircles(false)
        set1.setDrawValues(false)
        set1.fillAlpha = 65
        set1.fillColor = ColorTemplate.getHoloBlue()
        set1.highLightColor = Color.rgb(244, 117, 117)
        set1.setDrawCircleHole(false)
        colorPM25!!.setTextColor(ColorTemplate.getHoloBlue())

        val set2 = LineDataSet(values2, "Temperature")
        set2.axisDependency = AxisDependency.LEFT
        set2.color = ColorTemplate.JOYFUL_COLORS[0]
        set2.valueTextColor = ColorTemplate.JOYFUL_COLORS[0]
        set2.lineWidth = 1.5f
        set2.setDrawCircles(false)
        set2.setDrawValues(false)
        set2.fillAlpha = 65
        set2.fillColor = ColorTemplate.JOYFUL_COLORS[0]
        set2.highLightColor = Color.rgb(244, 117, 117)
        set2.setDrawCircleHole(false)
        colorTemp!!.setTextColor(ColorTemplate.JOYFUL_COLORS[0])

        val set3 = LineDataSet(values3, "Humidity")
        set3.axisDependency = AxisDependency.LEFT
        set3.color = ColorTemplate.JOYFUL_COLORS[1]
        set3.valueTextColor = ColorTemplate.JOYFUL_COLORS[1]
        set3.lineWidth = 1.5f
        set3.setDrawCircles(false)
        set3.setDrawValues(false)
        set3.fillAlpha = 65
        set3.fillColor = ColorTemplate.JOYFUL_COLORS[1]
        set3.highLightColor = Color.rgb(244, 117, 117)
        set3.setDrawCircleHole(false)
        colorHumidity!!.setTextColor(ColorTemplate.JOYFUL_COLORS[1])

        // create a data object with the datasets
        val data = LineData(set1, set2, set3)
        data.setValueTextColor(ColorTemplate.JOYFUL_COLORS[2])
        data.setValueTextSize(9f)

        // set data
        mChart!!.data = data
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        // TODO Auto-generated method stub

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        // TODO Auto-generated method stub

    }

    override fun onDestroy() {
        super.onDestroy()

        disposable!!.dispose()
    }

    companion object {
        private val TAG = "TemperatureChart"
    }
}