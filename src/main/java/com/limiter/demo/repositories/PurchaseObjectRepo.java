package com.limiter.demo.repositories;

import com.limiter.demo.models.Purchaseobject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseObjectRepo extends JpaRepository<Purchaseobject,Long> {
}
