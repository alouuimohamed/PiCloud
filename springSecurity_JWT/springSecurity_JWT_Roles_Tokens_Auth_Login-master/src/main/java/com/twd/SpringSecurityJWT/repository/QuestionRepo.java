package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepo extends JpaRepository<Question, Integer> {
}
