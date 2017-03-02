package io.swagger.api;

import io.swagger.model.ErrorModel;
import io.swagger.model.NewPet;
import io.swagger.model.Pet;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-02-23T07:57:56.431Z")

@Api(value = "pets", description = "the pets API")
public interface PetsApi {

    @ApiOperation(value = "", notes = "Creates a new pet in the store.  Duplicates are allowed", response = Pet.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "pet response", response = Pet.class),
        @ApiResponse(code = 200, message = "unexpected error", response = Pet.class) })
    @RequestMapping(value = "/pets",
        produces = { "application/json" }, 
        method = RequestMethod.POST)
    ResponseEntity<Pet> addPet(@ApiParam(value = "Pet to add to the store" ,required=true ) @RequestBody NewPet pet);


    @ApiOperation(value = "", notes = "deletes a single pet based on the ID supplied", response = Void.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "pet deleted", response = Void.class),
        @ApiResponse(code = 200, message = "unexpected error", response = Void.class) })
    @RequestMapping(value = "/pets/{id}",
        produces = { "application/json" }, 
        method = RequestMethod.DELETE)
    ResponseEntity<Void> deletePet(@ApiParam(value = "ID of pet to delete",required=true ) @PathVariable("id") Long id);


    @ApiOperation(value = "", notes = "Returns a user based on a single ID, if the user does not have access to the pet", response = Pet.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "pet response", response = Pet.class),
        @ApiResponse(code = 200, message = "unexpected error", response = Pet.class) })
    @RequestMapping(value = "/pets/{id}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<Pet> findPetById(@ApiParam(value = "ID of pet to fetch",required=true ) @PathVariable("id") Long id);


    @ApiOperation(value = "", notes = "Returns all pets from the system that the user has access to", response = Pet.class, responseContainer = "List", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "pet response", response = Pet.class),
        @ApiResponse(code = 200, message = "unexpected error", response = Pet.class) })
    @RequestMapping(value = "/pets",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<Pet>> findPets(@ApiParam(value = "tags to filter by") @RequestParam(value = "tags", required = false) List<String> tags,
        @ApiParam(value = "maximum number of results to return") @RequestParam(value = "limit", required = false) Integer limit);

}
