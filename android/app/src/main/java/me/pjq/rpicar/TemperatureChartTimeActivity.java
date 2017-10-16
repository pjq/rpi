
package me.pjq.rpicar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import me.pjq.rpicar.chart.DemoBase;
import me.pjq.rpicar.models.WeatherItem;
import me.pjq.rpicar.realm.Settings;
import me.pjq.rpicar.utils.Logger;

public class TemperatureChartTimeActivity extends DemoBase implements OnSeekBarChangeListener {
    private static final String TAG = "TemperatureChart";

    private LineChart mChart;
    private SeekBar mSeekBarX;
    private TextView tvX;
    private TextView colorPM25;
    private TextView colorTemp;
    private TextView colorHumidity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_temperature_chart);

        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(R.string.pm25title);

        colorPM25 = (TextView) findViewById(R.id.colorPM25);
        colorTemp = (TextView) findViewById(R.id.colorTemp);
        colorHumidity = (TextView) findViewById(R.id.colorHumidity);

        tvX = (TextView) findViewById(R.id.tvXMax);
        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarX.setProgress(100);
        tvX.setText("100");

        mSeekBarX.setOnSeekBarChangeListener(this);

        mChart = (LineChart) findViewById(R.id.chart1);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.GREEN);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
//        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat(" MM/dd HH:mm");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value < 0 || value >= weatherItems.size()) {
                    return "";
                }

                WeatherItem item = weatherItems.get((int) value);
                return mFormat.format(new Date(item.getTimestamp()));
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(170f);
        leftAxis.setYOffset(-9f);
//        leftAxis.setTextColor(Color.rgb(255, 192, 56));
        leftAxis.setTextColor(Color.DKGRAY);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        initWeatherStatus();
        readDemoJson();
    }

    private void readDemoJson() {
        final Settings settings = DataManager.getRealm().where(Settings.class).findFirst();
        if (settings != null) {
            final String weatherJson = settings.getWeatherJson();
            if (null != weatherJson) {

                List weatherItems = Arrays.asList(new Gson().fromJson(weatherJson, WeatherItem[].class));
                WeatherItem item = (WeatherItem) weatherItems.get(0);
                String value = item.getDate() + " PM2.5 " + item.getPm25() + " " + item.getTemperature() + "°C " + item.getHumidity() + "%";
                Logger.log(TAG, value);

                setData(weatherItems);
                mChart.invalidate();
            }

            return;
        }

        try {
            InputStream inputStream = getAssets().open("demo.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();
            StringBuffer stringBuffer = new StringBuffer();
            while (line != null) {
                stringBuffer.append(line + '\n');
                line = bufferedReader.readLine();
            }
            Gson gson = new Gson();
            WeatherItem[] weatherItems = gson.fromJson(stringBuffer.toString(), WeatherItem[].class);
            List<WeatherItem> list = new ArrayList<>();
            for (WeatherItem weatherItem : weatherItems) {
                list.add(weatherItem);
            }

            setData(list);
            mChart.invalidate();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    Disposable disposable;

    private void initWeatherStatus() {
        if (null != disposable) {
            disposable.dispose();
        }

        final CarControllerApiService apiService = new CarControllerApiService();
        Scheduler scheduler = Schedulers.from(Executors.newSingleThreadExecutor());
        disposable = Observable.interval(0, 2, TimeUnit.SECONDS)
                .flatMap(new Function<Long, ObservableSource<List<WeatherItem>>>() {
                    @Override
                    public ObservableSource<List<WeatherItem>> apply(Long aLong) throws Exception {
                        return apiService.getApi().getWeatherItems(0, mSeekBarX.getProgress());
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        log(throwable.toString());
                    }
                })
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwablObservable) throws Exception {
                        return throwablObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                return Observable.timer(2, TimeUnit.SECONDS);
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler)
                .subscribe(new Consumer<List<WeatherItem>>() {
                    @Override
                    public void accept(final List<WeatherItem> weatherItems) throws Exception {
                        WeatherItem item = weatherItems.get(0);
                        String value = item.getDate() + "\nPM2.5 " + item.getPm25() + " " + item.getTemperature() + "°C " + item.getHumidity() + "%";
                        log(value);
                        setData(weatherItems);
                        mChart.invalidate();
                        DataManager.getRealm().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.where(Settings.class).findFirst().setWeatherJson(new Gson().toJson(weatherItems));
                            }
                        });

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        log(throwable.toString());
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setDrawValues(!set.isDrawValuesEnabled());
                }

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (mChart.getData() != null) {
                    mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
                    mChart.invalidate();
                }
                break;
            }
            case R.id.actionToggleFilled: {

                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawFilledEnabled())
                        set.setDrawFilled(false);
                    else
                        set.setDrawFilled(true);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleCircles: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawCirclesEnabled())
                        set.setDrawCircles(false);
                    else
                        set.setDrawCircles(true);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleCubic: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.getMode() == LineDataSet.Mode.CUBIC_BEZIER)
                        set.setMode(LineDataSet.Mode.LINEAR);
                    else
                        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleStepped: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.getMode() == LineDataSet.Mode.STEPPED)
                        set.setMode(LineDataSet.Mode.LINEAR);
                    else
                        set.setMode(LineDataSet.Mode.STEPPED);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionTogglePinch: {
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                else
                    mChart.setPinchZoom(true);

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
                mChart.notifyDataSetChanged();
                break;
            }
            case R.id.animateX: {
                mChart.animateX(3000);
                break;
            }
            case R.id.animateY: {
                mChart.animateY(3000);
                break;
            }
            case R.id.animateXY: {
                mChart.animateXY(3000, 3000);
                break;
            }

            case R.id.actionSave: {
                if (mChart.saveToPath("title" + System.currentTimeMillis(), "")) {
                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show();

                // mChart.saveToGallery("title"+System.currentTimeMillis())
                break;
            }
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvX.setText("" + (mSeekBarX.getProgress()));
        initWeatherStatus();

        // redraw
//        mChart.invalidate();
    }

    void log(String log) {
        Log.i(TAG, log);
    }

    List<WeatherItem> weatherItems;

    private void setData(List<WeatherItem> weatherItemList) {
//        weatherItemList.sort(new Comparator<WeatherItem>() {
//            @Override
//            public int compare(WeatherItem o1, WeatherItem o2) {
//                return (int) (o1.getTimestamp()-o2.getTimestamp());
//            }
//        });

        Collections.sort(weatherItemList, new Comparator<WeatherItem>() {
            @Override
            public int compare(WeatherItem o1, WeatherItem o2) {
                return (int) (o1.getTimestamp() - o2.getTimestamp());
            }
        });

        weatherItems = weatherItemList;

        // now in hours
        long now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis());

        ArrayList<Entry> values = new ArrayList<Entry>();
        ArrayList<Entry> values2 = new ArrayList<Entry>();
        ArrayList<Entry> values3 = new ArrayList<Entry>();

        float from = now;

        int count = weatherItemList.size();
        int range = 30;
//         count = hours
        float to = now + count;

        // increment by 1 hour
//        for (float x = from; x < to; x++) {
//
//            float y = getRandom(range, 50);
//            values.add(new Entry(x, y)); // add one entry per hour
//        }


        int i = 0;
        for (WeatherItem item : weatherItemList) {
//            values.add(new Entry(TimeUnit.MILLISECONDS.toMinutes(item.getTimestamp() / 1000), item.getPm25()));
            values.add(new Entry(i, item.getPm25()));
            values2.add(new Entry(i, (float) item.getTemperature()));
            values3.add(new Entry(i, (float) item.getHumidity()));
            i++;
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "PM 2.5");
        set1.setAxisDependency(AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        colorPM25.setTextColor(ColorTemplate.getHoloBlue());

        LineDataSet set2 = new LineDataSet(values2, "Temperature");
        set2.setAxisDependency(AxisDependency.LEFT);
        set2.setColor(ColorTemplate.JOYFUL_COLORS[0]);
        set2.setValueTextColor(ColorTemplate.JOYFUL_COLORS[0]);
        set2.setLineWidth(1.5f);
        set2.setDrawCircles(false);
        set2.setDrawValues(false);
        set2.setFillAlpha(65);
        set2.setFillColor(ColorTemplate.JOYFUL_COLORS[0]);
        set2.setHighLightColor(Color.rgb(244, 117, 117));
        set2.setDrawCircleHole(false);
        colorTemp.setTextColor(ColorTemplate.JOYFUL_COLORS[0]);

        LineDataSet set3 = new LineDataSet(values3, "Humidity");
        set3.setAxisDependency(AxisDependency.LEFT);
        set3.setColor(ColorTemplate.JOYFUL_COLORS[1]);
        set3.setValueTextColor(ColorTemplate.JOYFUL_COLORS[1]);
        set3.setLineWidth(1.5f);
        set3.setDrawCircles(false);
        set3.setDrawValues(false);
        set3.setFillAlpha(65);
        set3.setFillColor(ColorTemplate.JOYFUL_COLORS[1]);
        set3.setHighLightColor(Color.rgb(244, 117, 117));
        set3.setDrawCircleHole(false);
        colorHumidity.setTextColor(ColorTemplate.JOYFUL_COLORS[1]);

        // create a data object with the datasets
        LineData data = new LineData(set1, set2, set3);
        data.setValueTextColor(ColorTemplate.JOYFUL_COLORS[2]);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        disposable.dispose();
    }
}