package me.pjq.rpicar;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private static final String TAG = "MainActivity";
    Button left;
    Button right;
    Button up;
    Button down;
    Button auto;
    View stop;
    WebView webView;
    EditText url;
    TextView cameraOn;
    SeekBar durationSeekBar;
    TextView seekbarValue;

    SeekBar speed;
    TextView speedValue;
    TextView weatherStatus;

    private static final long DEFAULT_DURATION = 500;
    private static final long MAX_DURATION = 4;

    CarControllerApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate appCompatDelegate = getDelegate();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        url = (EditText) findViewById(R.id.url);
        stop = findViewById(R.id.stop);
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);
        up = (Button) findViewById(R.id.up);
        auto = (Button) findViewById(R.id.auto);
        down = (Button) findViewById(R.id.down);
        webView = (WebView) findViewById(R.id.webview);
        cameraOn = (TextView) findViewById(R.id.cameraOn);
        durationSeekBar = (SeekBar) findViewById(R.id.seekbar);
        seekbarValue = (TextView) findViewById(R.id.seekbarValue);
        speed = (SeekBar) findViewById(R.id.speed);
        speedValue = (TextView) findViewById(R.id.speedValue);
        weatherStatus = (TextView) findViewById(R.id.weatherStatus);

        stop.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        up.setOnClickListener(this);
        auto.setOnClickListener(this);
        down.setOnClickListener(this);
        cameraOn.setOnClickListener(this);

//        left.setOnTouchListener(this);
//        right.setOnTouchListener(this);
//        up.setOnTouchListener(this);
//        down.setOnTouchListener(this);
//        stop.setOnTouchListener(this);

        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        durationSeekBar.setMax(100);
        durationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbarValue.setText(getDuration() + " ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        speed.setMax(100);
        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedValue.setText(getSpeed() + " %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                speedValue.setText(getSpeed() + " %");

                final CarAction carAction = new CarAction();
                //set the default duration to 1 second.
                carAction.duration = getDuration();
                carAction.action = "speed";
                carAction.speed = getSpeed();

                sendCommand(carAction);
            }
        });

        initSettings();
        apiService = new CarControllerApiService();
        webView.loadUrl(CarControllerApiService.Config.STREAM_URL());
//        hideSoftKeyboard();

        initWeatherStatus();
    }

    Disposable disposable;

    private void initWeatherStatus() {
        Scheduler scheduler = Schedulers.from(Executors.newSingleThreadExecutor());
        disposable = Observable.interval(0, 2, TimeUnit.SECONDS)
                .flatMap(new Function<Long, ObservableSource<List<WeatherItem>>>() {
                    @Override
                    public ObservableSource<List<WeatherItem>> apply(Long aLong) throws Exception {
                        return apiService.getApi().getWeatherItems(0, 2);
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
                    public void accept(List<WeatherItem> weatherItems) throws Exception {
                        WeatherItem item = weatherItems.get(0);
                        String value = item.getDate() + "\nPM2.5 " + item.getPm25() + " " + item.getTemperature() + "°C " + item.getHumidity() + "%";
                        log(value);

                        weatherStatus.setText(value);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        log(throwable.toString());
                    }
                });
    }

    private void initSettings() {
        DataManager.init(seekbarValue.getContext());
        Settings settings = DataManager.getRealm().where(Settings.class).findFirst();
        if (null != settings && !TextUtils.isEmpty(settings.getHost())) {
            durationSeekBar.setProgress(settings.getDuration());
            speed.setProgress(settings.getSpeed());
            url.setText(settings.getHost());
            CarControllerApiService.Config.HOST = settings.getHost();
        } else {
            durationSeekBar.setProgress(10);
            speed.setProgress(50);
            url.setText(CarControllerApiService.Config.HOST());

            DataManager.getRealm().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Settings newSetting = DataManager.getRealm().createObject(Settings.class);
                    newSetting.setDuration(durationSeekBar.getProgress());
                    newSetting.setSpeed(speed.getProgress());
                    newSetting.setHost(url.getText().toString());
                }
            });
        }
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
    }

    private int getSpeed() {
        return speed.getProgress();
    }

    private long getDuration() {
        return (long) ((float) durationSeekBar.getProgress() / (float) 100 * MAX_DURATION * 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DataManager.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Settings settings = DataManager.getRealm().where(Settings.class).findFirst();
                settings.setHost(url.getText().toString());
                settings.setDuration(durationSeekBar.getProgress());
                settings.setSpeed(speed.getProgress());
            }
        });

        disposable.dispose();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

//        CarControllerApiService.API_URL = url.getText().toString();

        final CarAction carAction = new CarAction();
        //set the default duration to 1 second.
        carAction.duration = getDuration();
        carAction.speed = getSpeed();

        switch (id) {
            case R.id.stop:
                carAction.action = "stop";
                break;

            case R.id.left:
                carAction.action = "left";
                break;

            case R.id.right:
                carAction.action = "right";
                break;

            case R.id.up:
                carAction.action = "up";
                break;

            case R.id.down:
                carAction.action = "down";
                break;

            case R.id.auto:
                if (carAction.speed > 40) {
                    carAction.speed = 40;
                }

                carAction.action = "auto_drive";
                break;

            case R.id.cameraOn:
                webView.reload();
                break;
        }

        sendCommand(carAction);
    }

    private void sendCommand(CarAction carAction) {
        apiService.getApi().sendCommand(carAction)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CarAction>() {
                    @Override
                    public void accept(CarAction action) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();

        final CarAction carAction = new CarAction();
        //set the default duration to 1 second.
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            carAction.duration = 1000 * 5;

            switch (id) {
                case R.id.stop:
                    carAction.action = "stop";
                    break;

                case R.id.left:
                    carAction.action = "left";
                    break;

                case R.id.right:
                    carAction.action = "right";
                    break;

                case R.id.up:
                    carAction.action = "up";
                    break;

                case R.id.down:
                    carAction.action = "down";
                    break;
            }
        } else {
            carAction.action = "stop";
        }

        log(carAction.action);

        apiService.getApi().sendCommand(carAction)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CarAction>() {
                    @Override
                    public void accept(CarAction action) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Call<CarAction> response = apiService.getApi().sendCommand(carAction);
//                try {
//                    response.execute();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

        return false;
    }

    void log(String log) {
        Log.i(TAG, log);
    }
}
