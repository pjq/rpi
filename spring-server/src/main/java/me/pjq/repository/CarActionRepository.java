package me.pjq.repository;

import me.pjq.model.CarAction;
import me.pjq.model.RpiWeatherItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by i329817 on 07/29/2017.
 */
@Repository
public interface CarActionRepository extends JpaRepository<CarAction, Long>
{
}