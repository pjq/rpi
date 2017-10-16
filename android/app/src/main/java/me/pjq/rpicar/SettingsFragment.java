package me.pjq.rpicar;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import me.pjq.rpicar.models.CarAction;
import me.pjq.rpicar.realm.Settings;
import me.pjq.rpicar.utils.Logger;
import me.pjq.rpicar.utils.SnackbarUtil;

import static android.content.ContentValues.TAG;


public class SettingsFragment extends Fragment {
    EditText url;
    SeekBar durationSeekBar;
    TextView seekbarValue;
    SeekBar speed;
    TextView speedValue;
    CarControllerApiService apiService;

    private static final long DEFAULT_DURATION = 500;
    private static final long MAX_DURATION = 4;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = new CarControllerApiService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        url = (EditText) view.findViewById(R.id.url);
        durationSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        seekbarValue = (TextView) view.findViewById(R.id.seekbarValue);
        speed = (SeekBar) view.findViewById(R.id.speed);
        speedValue = (TextView) view.findViewById(R.id.speedValue);

        durationSeekBar.setMax((int) (MAX_DURATION * 1000));
        durationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbarValue.setText(seekBar.getProgress() + " ms");
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


    private int getSpeed() {
        return speed.getProgress();
    }

    private long getDuration() {
        return (long) ((float) durationSeekBar.getProgress() / (float) 100 * MAX_DURATION * 1000);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        getActivity().setTitle("Settings");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        DataManager.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Settings settings = DataManager.getRealm().where(Settings.class).findFirst();
                settings.setHost(url.getText().toString());
                settings.setDuration(durationSeekBar.getProgress());
                settings.setSpeed(speed.getProgress());


                Logger.log(TAG, settings.toString());
            }
        });

        SnackbarUtil.makeText(getActivity(), "Save settings", Snackbar.LENGTH_LONG).show();
    }
}
