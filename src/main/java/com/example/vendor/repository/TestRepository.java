
package com.example.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.vendor.entity.TestEntity;

public interface TestRepository extends JpaRepository<TestEntity, Long> {}
