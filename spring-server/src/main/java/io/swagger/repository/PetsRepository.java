package io.swagger.repository;

import io.swagger.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by i329817 on 23/02/2017.
 */
@Repository
public interface PetsRepository extends JpaRepository<Pet, Long>
{
    @Query("select pet from Pet pet where pet.name = ?1")
    Pet findByName(String name);
}