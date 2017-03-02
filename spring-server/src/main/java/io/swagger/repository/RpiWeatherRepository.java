package io.swagger.repository;

import io.swagger.model.RpiWeatherItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by i329817 on 23/02/2017.
 */
@Repository
public interface RpiWeatherRepository extends JpaRepository<RpiWeatherItem, Long>
{
}