package com.limiter.demo.repositories;

import com.limiter.demo.models.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery,Long> {

}
