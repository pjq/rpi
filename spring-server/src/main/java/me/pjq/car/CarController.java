package me.pjq.car;

import com.google.gson.Gson;
import com.pi4j.io.gpio.*;
import me.pjq.model.CarAction;
import me.pjq.model.MotionDetect;
import me.pjq.model.SensorStatus;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.converter.json.GsonBuilderUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by i329817 on 30/07/2017.
 */
public enum CarController {
    instance;
    private Config config;
    GpioPinDigitalOutput pin1;
    GpioPinDigitalOutput pin2;
    GpioPinDigitalOutput pin3;
    GpioPinDigitalOutput pin4;

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    private boolean usePython = true;

    private static final long max_time = 10 * 1000;

    private CarController() {
        init();
    }

    public CarController init() {
        if (usePython) {
            return this;
        }

        if (null == this.config) {
            this.config = new Config(RaspiPin.GPIO_17, RaspiPin.GPIO_18, RaspiPin.GPIO_22, RaspiPin.GPIO_23);
            // create gpio controller
            final GpioController gpio = GpioFactory.getInstance();

            // provision gpio pin #01 as an output pin and turn on
            pin1 = gpio.provisionDigitalOutputPin(config.PIN1, "PIN1", PinState.LOW);
            pin2 = gpio.provisionDigitalOutputPin(config.PIN2, "PIN2", PinState.LOW);
            pin3 = gpio.provisionDigitalOutputPin(config.PIN3, "PIN3", PinState.LOW);
            pin4 = gpio.provisionDigitalOutputPin(config.PIN4, "PIN4", PinState.LOW);

            // set shutdown state for this pin
            pin1.setShutdownOptions(true, PinState.LOW);
        }

        return this;
    }


    public void control(CarAction carAction) {
        Monitor.instance.onCommand();

        String action = carAction.getAction();
        CarController carController = CarController.getInstance();
        CarAction.Action act = CarAction.Action.toAction(action);
        if (act.isUp()) {
            carController.up(carAction);
        } else if (act.isDown()) {
            carController.down(carAction);
        } else if (act.isLeft()) {
            carController.left(carAction);
        } else if (act.isRight()) {
            carController.right(carAction);
        } else if (act.isStop()) {
            carController.stop(carAction);
        } else if (act.isAutoDrive()) {
            carController.autoDrive(carAction);
        } else if (act.isSpeed()) {
            carController.speed(carAction);
        } else if (act.isAngle()) {
            carController.angle(carAction);
        } else if (act.isRelayOn()) {
            carController.relay(carAction, "on");
        } else if (act.isRelayOff()) {
            carController.relay(carAction, "off");
        }
    }

    private void callPython(String pythonFile, long duration, int speed) {
        String path = "/home/pi/rpi/car";
        String command = "python " + path + "/" + pythonFile + " " + duration / 1000.0f + " " + speed;
        log(command);

        runCommand(command);
    }

    public MotionDetect motionDetect() {
        String result = callPython("motion_detect.py", null);

        MotionDetect motionDetect = new Gson().fromJson(result, MotionDetect.class);
        return motionDetect;
    }

    private String callPython(String pythonFile, int value) {
        String path = "/home/pi/rpi/car";
        String command = "python " + path + "/" + pythonFile + " " + value;
        log(command);

        return runCommand(command);
    }

    private String callPython(String pythonFile, String value) {
        String path = "/home/pi/rpi/car";
        String command = "python " + path + "/" + pythonFile + " " + value;
        log(command);

        return runCommand(command);
    }

    private String runCommand(String command) {
        StringBuilder stringBuilder = new StringBuilder();
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec(command);
            BufferedReader input =
                    new BufferedReader
                            (new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
                stringBuilder.append(line + '\n');
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    private void callPythonSpeed(String pythonFile, int speed) {
        String path = "/home/pi/rpi/car";
        Runtime rt = Runtime.getRuntime();
        String command = "python " + path + "/" + pythonFile + " " + speed;
        log(command);
        runCommand(command);
    }

    public SensorStatus getSensorStatus() {
        String path = "/home/pi/rpi/car";
        String command = "python " + path + "/get_sensorstatus.py";
        log(command);
        String value = runCommand(command);
        SensorStatus sensorStatus = new Gson().fromJson(value, SensorStatus.class);
        return sensorStatus;
    }

    public void relay(final CarAction action, final String on) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (usePython) {
                    callPython("relay_control.py", on);

                    if ("on".equalsIgnoreCase(on)) {
                        Monitor.instance.relayOn = true;
                    } else {
                        Monitor.instance.relayOn = false;
                    }

                    return;
                }
            }
        });
    }

    public void angle(final CarAction actionDuration) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (usePython) {
                    callPython("angle.py", actionDuration.getAngle());

                    return;
                }
            }
        });
    }

    public void up(final CarAction actionDuration) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                long duration = actionDuration.getDuration() > 0 ? actionDuration.getDuration() : max_time;
                log("forward seconds: " + duration / 1000);

                if (usePython) {
                    callPython("up.py", actionDuration.getDuration(), actionDuration.getSpeed());

                    return;
                }

                pin1.low();
                pin2.high();
                pin3.low();
                pin4.high();

                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                stopCar();
            }
        });
    }

    public void down(final CarAction actionDuration) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                long duration = actionDuration.getDuration() > 0 ? actionDuration.getDuration() : max_time;
                log("backward seconds: " + duration / 1000);
                if (usePython) {
                    callPython("down.py", actionDuration.getDuration(), actionDuration.getSpeed());

                    return;
                }

                pin1.high();
                pin2.low();
                pin3.high();
                pin4.low();

                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                stopCar();
            }
        });
    }

    public void left(final CarAction actionDuration) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                long duration = actionDuration.getDuration() > 0 ? actionDuration.getDuration() : max_time;
                log("turn left seconds: " + duration / 1000);
                if (usePython) {
                    callPython("left.py", actionDuration.getDuration(), actionDuration.getSpeed());

                    return;
                }

                pin1.low();
                pin2.high();
                pin3.high();
                pin4.low();

                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                stopCar();
            }
        });
    }

    public void right(final CarAction actionDuration) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                long duration = actionDuration.getDuration() > 0 ? actionDuration.getDuration() : max_time;
                log("turn right seconds: " + duration / 1000);
                if (usePython) {
                    callPython("right.py", actionDuration.getDuration(), actionDuration.getSpeed());

                    return;
                }

                pin1.high();
                pin2.low();
                pin3.low();
                pin4.high();

                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                stopCar();
            }
        });
    }

    public void stop(final CarAction actionDuration) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (usePython) {
                    callPython("stop.py", actionDuration.getDuration(), actionDuration.getSpeed());

                    return;
                }

                stopCar();
            }
        });
    }

    public void autoDrive(final CarAction actionDuration) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (usePython) {
                    callPython("auto_car.py", actionDuration.getDuration(), actionDuration.getSpeed());

                    return;
                }

                stopCar();
            }
        });
    }

    public void speed(final CarAction speed) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (usePython) {
                    callPythonSpeed("speed.py", speed.getSpeed());

                    return;
                }

                stopCar();
            }
        });
    }


    public void stopCar() {
        log("stop");
        pin1.low();
        pin2.low();
        pin3.low();
        pin4.low();
    }

    private void log(String msg) {
        System.out.println(msg);
    }

    public static CarController getInstance() {
        return instance;
    }

    public static class Config {
        Pin PIN1;
        Pin PIN2;
        Pin PIN3;
        Pin PIN4;

        public Config(Pin PIN1, Pin PIN2, Pin PIN3, Pin PIN4) {
            this.PIN1 = PIN1;
            this.PIN2 = PIN2;
            this.PIN3 = PIN3;
            this.PIN4 = PIN4;
        }
    }
}
