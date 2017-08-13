package me.pjq.car;

import com.pi4j.io.gpio.*;
import me.pjq.model.CarAction;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by i329817 on 30/07/2017.
 */
public class CarController {
    private static CarController instance;
    private Config config;
    GpioPinDigitalOutput pin1;
    GpioPinDigitalOutput pin2;
    GpioPinDigitalOutput pin3;
    GpioPinDigitalOutput pin4;

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    private boolean usePython = true;

    private static final long max_time = 10 * 1000;

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


    private void callPython(String pythonFile, long duration, int speed) {
        String path = "/home/pi/rpi/car";
        Runtime rt = Runtime.getRuntime();
        String command = "python " + path + "/" + pythonFile + " " + duration/1000.0f + " " + speed;
        log(command);
        try {
            Process pr = rt.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void callPythonSpeed(String pythonFile, int speed) {
        String path = "/home/pi/rpi/car";
        Runtime rt = Runtime.getRuntime();
        String command = "python " + path + "/" + pythonFile + " " + speed;
        log(command);
        try {
            Process pr = rt.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void up(final CarAction actionDuration) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                long duration =  actionDuration.getDuration()> 0 ? actionDuration.getDuration() : max_time;
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
        if (null == instance) {
            instance = new CarController();
        }

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
