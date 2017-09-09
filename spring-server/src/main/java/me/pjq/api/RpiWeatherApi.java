package me.pjq.api;

import me.pjq.model.CarAction;
import me.pjq.model.RpiWeatherItem;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-02-23T07:57:56.431Z")

@Api(value = "rpi", description = "the Raspberry Pi API")
public interface RpiWeatherApi {

    @ApiOperation(value = "", notes = "Creates a new WeatherItem in the store.  Duplicates are allowed", response = RpiWeatherItem.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "WeatherItem response", response = RpiWeatherItem.class),
        @ApiResponse(code = 200, message = "unexpected error", response = RpiWeatherItem.class) })
    @RequestMapping(value = "/weather",
        produces = { "application/json" }, 
        method = RequestMethod.POST)
    ResponseEntity<RpiWeatherItem> addWeatherItem(@ApiParam(value = "WeatherItem to add to the store", required = true) @RequestBody RpiWeatherItem WeatherItem);


    @ApiOperation(value = "", notes = "deletes a single WeatherItem based on the ID supplied", response = Void.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "WeatherItem deleted", response = Void.class),
        @ApiResponse(code = 200, message = "unexpected error", response = Void.class) })
    @RequestMapping(value = "/weather/{id}",
        produces = { "application/json" }, 
        method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteWeatherItem(@ApiParam(value = "ID of WeatherItem to delete", required = true) @PathVariable("id") Long id);

    @ApiOperation(value = "", notes = "Returns all WeatherItems from the system that the user has access to", response = RpiWeatherItem.class, responseContainer = "List", tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "WeatherItem response", response = RpiWeatherItem.class),
            @ApiResponse(code = 200, message = "unexpected error", response = RpiWeatherItem.class) })
    @RequestMapping(value = "/weathers",
            produces = { "application/json" },
            method = RequestMethod.GET)
    ResponseEntity<List<RpiWeatherItem>> findWeatherItems(@ApiParam(value = "Page Number") @RequestParam(value = "page", required = true) final Integer page, @ApiParam(value = "Page Size") @RequestParam(value = "size", required = true) final Integer size);

    @ApiOperation(value = "", notes = "CarAction controller", response = RpiWeatherItem.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "WeatherItem response", response = CarAction.class),
            @ApiResponse(code = 200, message = "unexpected error", response = CarAction.class) })
    @RequestMapping(value = "/car/controller",
            produces = { "application/json" },
            method = RequestMethod.POST)
    ResponseEntity<CarAction> carController(@ApiParam(value = "CarAction action", required = true) @RequestBody CarAction carAction);
}
