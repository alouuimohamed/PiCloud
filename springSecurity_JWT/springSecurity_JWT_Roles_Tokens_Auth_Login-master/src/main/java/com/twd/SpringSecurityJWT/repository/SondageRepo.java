package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Feedback;
import com.twd.SpringSecurityJWT.entity.Sondage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SondageRepo extends JpaRepository<Sondage, Integer> {
    int countByIsActive(boolean isActive);
    List<Sondage> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    @Query("SELECT s FROM Sondage s LEFT JOIN FETCH s.questions q LEFT JOIN FETCH q.repons")
    List<Sondage> findAllWithQuestionsAndReponses();

}
