package me.pjq.api;

import io.swagger.annotations.ApiParam;
import me.pjq.model.RpiWeatherItem;
import me.pjq.repository.RpiWeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-02-23T07:57:56.431Z")

@Controller
public class RpiWeatherApiController implements RpiWeatherApi {
    private static final int MAXMIUM = 100;

    @Autowired
    private RpiWeatherRepository rpiWeatherRepository;

    public ResponseEntity<RpiWeatherItem> addWeatherItem(@ApiParam(value = "WeatherItem to add to the store", required = true) @RequestBody RpiWeatherItem WeatherItem) {
        // do some magic!
//        RpiWeatherItem addWeatherItem = new RpiWeatherItem();
//        addWeatherItem.setId(WeatherItem.getId());
//        addWeatherItem.setPm25(WeatherItem.getPm25());
//        addWeatherItem.setTemperature(WeatherItem.getTemperature());
//        addWeatherItem.setHumidity(WeatherItem.getHumidity());

        WeatherItem.setTimestamp(System.currentTimeMillis());
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

    public ResponseEntity<List<RpiWeatherItem>> findWeatherItems(@ApiParam(value = "pm25 to filter by") @RequestParam(value = "pm25", required = false) String pm25,
                                                                 @ApiParam(value = "maximum number of results to return") @RequestParam(value = "limit", required = false) Integer limit) {
        // do some magic!
        Comparator<RpiWeatherItem> comparator = new Comparator<RpiWeatherItem>() {
            @Override
            public int compare(RpiWeatherItem o1, RpiWeatherItem o2) {
                return -(int) (o1.getTimestamp() - o2.getTimestamp());
            }
        };
        List<RpiWeatherItem> rpiWeatherItems = rpiWeatherRepository.findAll();
        rpiWeatherItems.sort(comparator);

        int maxmium = MAXMIUM;
        try {
            maxmium = null == limit ? MAXMIUM : Integer.valueOf(limit);

        } catch (Exception e) {
           e.printStackTrace();
        }
        List<RpiWeatherItem> subList = rpiWeatherItems.subList(0, rpiWeatherItems.size() < maxmium ? rpiWeatherItems.size() : maxmium);

        return new ResponseEntity<List<RpiWeatherItem>>(subList, HttpStatus.OK);
    }
}
