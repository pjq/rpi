package me.pjq.rpicar;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Arrays;
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
import me.pjq.rpicar.models.CarAction;
import me.pjq.rpicar.models.SensorStatus;
import me.pjq.rpicar.models.WeatherItem;
import me.pjq.rpicar.realm.Settings;
import me.pjq.rpicar.utils.Logger;

public class CameraControllerFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {
    private static final String TAG = "CameraController";
    ImageView left;
    ImageView right;
    ImageView up;
    ImageView down;
    View auto;
    View stop;
    WebView webView;
    TextView cameraOn;

    TextView weatherStatus;

    CarControllerApiService apiService;

    public static CameraControllerFragment newInstance() {
        CameraControllerFragment fragment = new CameraControllerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        getActivity().setTitle("Camera");

    }

    private void initView(View view) {
        stop = view.findViewById(R.id.stop);
        left = (ImageView) view.findViewById(R.id.left);
        right = (ImageView) view.findViewById(R.id.right);
        up = (ImageView) view.findViewById(R.id.up);
        auto = view.findViewById(R.id.auto);
        down = (ImageView) view.findViewById(R.id.down);
        webView = (WebView) view.findViewById(R.id.webview);
        cameraOn = (TextView) view.findViewById(R.id.cameraOn);
        weatherStatus = (TextView) view.findViewById(R.id.weatherStatus);

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

        apiService = CarControllerApiService.getInstance();
        webView.loadUrl(CarControllerApiService.Config.STREAM_URL());
//        hideSoftKeyboard();

        initWeatherStatus();
        getSensorStatus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera_controller, container, false);
        initView(view);

        return view;
    }

    Disposable disposable;
    Disposable disposable2;
    SensorStatus sensorStatus;
    WeatherItem weatherItem;

    private void getSensorStatus() {
        Scheduler scheduler = Schedulers.from(Executors.newSingleThreadExecutor());
        disposable2 = Observable.interval(0, 400, TimeUnit.MILLISECONDS)
                .flatMap(new Function<Long, ObservableSource<SensorStatus>>() {
                    @Override
                    public ObservableSource<SensorStatus> apply(Long aLong) throws Exception {
                        return apiService.getApi().getSensorStatus();
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.log(TAG, throwable.toString());
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
                .subscribe(new Consumer<SensorStatus>() {
                    @Override
                    public void accept(final SensorStatus status) throws Exception {

                        Logger.log(TAG, status.toString());
                        sensorStatus = status;
                        updateStatus();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.log(TAG, throwable.toString());
                    }
                });
    }

    private void updateStatus() {
        if (null != weatherItem) {
            String value = weatherItem.getDate() + " PM2.5 " + weatherItem.getPm25() + " " + weatherItem.getTemperature() + "°C " + weatherItem.getHumidity() + "%";
            Logger.log(TAG, value);
            weatherStatus.setText(value);
        }

        if (null != sensorStatus) {
            weatherStatus.append("\nDistance(cm) " + sensorStatus.distance + "\n" + sensorStatus.obstacles.toString());
        }
    }

    private void initWeatherStatus() {
        weatherStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), TemperatureChartTimeActivity.class);
                getActivity().startActivity(intent);
            }
        });

        Scheduler scheduler = Schedulers.from(Executors.newSingleThreadExecutor());

        final Settings settings = getSettings();
        final String weatherJson = settings.getWeatherJson();

        if (weatherJson != null) {
            List weatherItems = Arrays.asList(new Gson().fromJson(weatherJson, WeatherItem[].class));
            WeatherItem item = (WeatherItem) weatherItems.get(0);
            String value = item.getDate() + " PM2.5 " + item.getPm25() + " " + item.getTemperature() + "°C " + item.getHumidity() + "%";
            Logger.log(TAG, value);

            weatherStatus.setText(value);
        }

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
                        Logger.log(TAG, throwable.toString());
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
                        weatherItem = weatherItems.get(0);
                        updateStatus();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.log(TAG, throwable.toString());
                    }
                });
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        disposable.dispose();
        disposable2.dispose();
        CarAction carAction = new CarAction();
        carAction.action = "stop";
        sendCommand(carAction);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Settings getSettings() {
        DataManager.init(getActivity().getApplicationContext());
        Settings settings = DataManager.getRealm().where(Settings.class).findFirst();
        if (null == settings) {
            DataManager.getRealm().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Settings newSetting = DataManager.getRealm().createObject(Settings.class);
                    newSetting.setDuration(100);
                    newSetting.setSpeed(10);
                }
            });

            return settings;
        } else {
            return settings;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

//        CarControllerApiService.API_URL = url.getText().toString();

        final CarAction carAction = new CarAction();
        //set the default duration to 1 second.
        carAction.duration = getSettings().duration;
        carAction.speed = getSettings().speed;

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

        Logger.log(TAG, carAction.action);

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

        return false;
    }

}
