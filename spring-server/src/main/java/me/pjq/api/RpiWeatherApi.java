package me.pjq.api;

import io.swagger.annotations.*;
import me.pjq.model.RpiWeatherItem;
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
    ResponseEntity<List<RpiWeatherItem>> findWeatherItems(@ApiParam(value = "pm25 to filter by") @RequestParam(value = "pm25", required = false) String pm25, @ApiParam(value = "maximum number of results to return") @RequestParam(value = "limit", required = false) Integer limit);
}
