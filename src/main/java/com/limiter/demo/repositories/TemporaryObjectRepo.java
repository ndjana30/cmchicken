package com.limiter.demo.repositories;

import com.limiter.demo.models.TemporaryObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporaryObjectRepo extends JpaRepository<TemporaryObject,Long> {
}
