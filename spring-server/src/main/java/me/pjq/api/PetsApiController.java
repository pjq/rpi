package me.pjq.api;

import me.pjq.model.NewPet;
import me.pjq.model.Pet;
import me.pjq.repository.PetsRepository;

import io.swagger.annotations.*;

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
public class PetsApiController implements PetsApi {

    @Autowired
    private PetsRepository petsRepository;

    public ResponseEntity<Pet> addPet(@ApiParam(value = "Pet to add to the store" ,required=true ) @RequestBody NewPet pet) {
        // do some magic!
        Pet addPet = new Pet();
        addPet.setId(pet.getId());
        addPet.setName(pet.getName());
        addPet.setNickname(pet.getNickname());
        addPet.setTag(pet.getTag());

        petsRepository.saveAndFlush(addPet);

        return new ResponseEntity<Pet>(HttpStatus.OK);
    }

    public ResponseEntity<Void> deletePet(@ApiParam(value = "ID of pet to delete",required=true ) @PathVariable("id") Long id) {
        // do some magic!
        Pet pet = new Pet();
        pet.setId(id);
        petsRepository.delete(pet);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<Pet> findPetById(@ApiParam(value = "ID of pet to fetch",required=true ) @PathVariable("id") Long id) {
        // do some magic!
        Pet pet = null;

        for (Object o : petsRepository.findAll()) {
           if (((Pet)o).getId() == id) {
              pet = (Pet) o;
           }
        }

        return new ResponseEntity<Pet>(pet, HttpStatus.OK);
    }

    public ResponseEntity<List<Pet>> findPets(@ApiParam(value = "tags to filter by") @RequestParam(value = "tags", required = false) List<String> tags,
        @ApiParam(value = "maximum number of results to return") @RequestParam(value = "limit", required = false) Integer limit) {
        // do some magic!
        return new ResponseEntity<List<Pet>>(petsRepository.findAll(), HttpStatus.OK);
    }

}
