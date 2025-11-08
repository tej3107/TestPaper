package com.testpaper.demo.repository;

import com.testpaper.demo.model.TestUserResponse;
import com.testpaper.demo.model.TestUserResponseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestUserResponseRepository extends JpaRepository<TestUserResponse, TestUserResponseId> {
}
