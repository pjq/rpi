package me.pjq.api;

import io.swagger.annotations.ApiParam;
import me.pjq.car.CarController;
import me.pjq.model.CarAction;
import me.pjq.model.RpiWeatherItem;
import me.pjq.model.SensorStatus;
import me.pjq.model.ServerStatus;
import me.pjq.repository.CarActionRepository;
import me.pjq.repository.RpiWeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import sun.management.Sensor;

import java.nio.file.WatchEvent;
import java.util.Date;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-02-23T07:57:56.431Z")

@Controller
public class RpiWeatherApiController implements RpiWeatherApi {

    @Autowired
    private RpiWeatherRepository rpiWeatherRepository;
    @Autowired
    private CarActionRepository carActionRepository;

    public ResponseEntity<RpiWeatherItem> addWeatherItem(@ApiParam(value = "WeatherItem to add to the store", required = true) @RequestBody RpiWeatherItem WeatherItem) {
        WeatherItem.setTimestamp(System.currentTimeMillis());
        WeatherItem.setDate(new Date(System.currentTimeMillis()).toLocaleString());
        rpiWeatherRepository.saveAndFlush(WeatherItem);

        return new ResponseEntity<RpiWeatherItem>(HttpStatus.OK);
    }

    public ResponseEntity<Void> deleteWeatherItem(@ApiParam(value = "ID of WeatherItem to delete", required = true) @PathVariable("id") Long id) {
        // do some magic!
        RpiWeatherItem WeatherItem = new RpiWeatherItem();
        WeatherItem.setId(id);
        rpiWeatherRepository.delete(WeatherItem);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<List<RpiWeatherItem>> findWeatherItems(@ApiParam(value = "Page Number") @RequestParam(value = "page", required = true) final Integer page, @ApiParam(value = "Page Size") @RequestParam(value = "size", required = true) final Integer size) {
        final PageRequest page1 = new PageRequest(
                page, size, Sort.Direction.DESC, "id"
        );


        return new ResponseEntity<List<RpiWeatherItem>>(rpiWeatherRepository.findAll(page1).getContent(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CarAction> carController(@ApiParam(value = "CarAction action", required = true) @RequestBody CarAction carAction) {
        carAction.setTimestamp(System.currentTimeMillis());
        carActionRepository.saveAndFlush(carAction);

        String action = carAction.getAction();
        CarController carController = CarController.getInstance().init();
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
        }

        return new ResponseEntity<CarAction>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ServerStatus> serverStatus() {
        final PageRequest page1 = new PageRequest(
                0, 1, Sort.Direction.DESC, "id"
        );
        List<RpiWeatherItem> rpiWeatherItems = rpiWeatherRepository.findAll(page1).getContent();
        RpiWeatherItem rpiWeatherItem = null;
        if (rpiWeatherItems.size() == 1) {
            rpiWeatherItem = rpiWeatherItems.get(0);
        }

        ServerStatus serverStatus = new ServerStatus();


        return new ResponseEntity<ServerStatus>(serverStatus, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SensorStatus> sensorStatus() {
        CarController carController = CarController.getInstance().init();
        SensorStatus sensorStatus = carController.getSensorStatus();

        return new ResponseEntity<SensorStatus>(sensorStatus, HttpStatus.OK);
    }
}
