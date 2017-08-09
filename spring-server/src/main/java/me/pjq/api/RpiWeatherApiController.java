package me.pjq.api;

import io.swagger.annotations.ApiParam;
import me.pjq.car.CarController;
import me.pjq.model.CarAction;
import me.pjq.model.RpiWeatherItem;
import me.pjq.repository.CarActionRepository;
import me.pjq.repository.RpiWeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-02-23T07:57:56.431Z")

@Controller
public class RpiWeatherApiController implements RpiWeatherApi {

    @Autowired
    private RpiWeatherRepository rpiWeatherRepository;
    @Autowired
    private CarActionRepository carActionRepository;

    public ResponseEntity<RpiWeatherItem> addWeatherItem(@ApiParam(value = "WeatherItem to add to the store" ,required=true ) @RequestBody RpiWeatherItem WeatherItem) {
        // do some magic!
        RpiWeatherItem addWeatherItem = new RpiWeatherItem();
        addWeatherItem.setId(WeatherItem.getId());
        addWeatherItem.setPm25(WeatherItem.getPm25());
        addWeatherItem.setTemperature(WeatherItem.getTemperature());
        addWeatherItem.setHumidity(WeatherItem.getHumidity());

        rpiWeatherRepository.saveAndFlush(addWeatherItem);

        return new ResponseEntity<RpiWeatherItem>(HttpStatus.OK);
    }

    public ResponseEntity<Void> deleteWeatherItem(@ApiParam(value = "ID of WeatherItem to delete",required=true ) @PathVariable("id") Long id) {
        // do some magic!
        RpiWeatherItem WeatherItem = new RpiWeatherItem();
        WeatherItem.setId(id);
        rpiWeatherRepository.delete(WeatherItem);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<List<RpiWeatherItem>> findWeatherItems(@ApiParam(value = "pm25 to filter by") @RequestParam(value = "pm25", required = false) String pm25,
                                              @ApiParam(value = "maximum number of results to return") @RequestParam(value = "limit", required = false) Integer limit) {
        // do some magic!
        return new ResponseEntity<List<RpiWeatherItem>>(rpiWeatherRepository.findAll(), HttpStatus.OK);
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
        } else if (act.isSpeed()) {
            carController.speed(carAction);
        }

        return new ResponseEntity<CarAction>(HttpStatus.OK);
    }
}
