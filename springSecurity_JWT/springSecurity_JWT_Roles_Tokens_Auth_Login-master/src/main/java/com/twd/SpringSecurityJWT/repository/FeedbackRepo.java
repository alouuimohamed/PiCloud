package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Feedback;
import com.twd.SpringSecurityJWT.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface FeedbackRepo extends JpaRepository<Feedback, Integer> {
}
