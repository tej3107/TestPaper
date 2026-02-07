package com.testpaper.demo.repository;

import com.testpaper.demo.model.TestUserResponse;
import com.testpaper.demo.model.TestUserResponseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestUserResponseRepository extends JpaRepository<TestUserResponse, TestUserResponseId> {

    @Query("SELECT tq FROM TestUserResponse tq WHERE tq.id.userId = :userId")
    List<TestUserResponse> findByUserId(String userId);

    @Query("SELECT tq FROM TestUserResponse tq WHERE tq.id.userId = :userId AND tq.id.questionPaperId = :questionPaperId")
    Optional<TestUserResponse> findByUserIdAndQuestionPaperId(String userId, String questionPaperId);
}
