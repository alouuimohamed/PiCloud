package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Feedback;
import com.twd.SpringSecurityJWT.entity.Sondage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SondageRepo extends JpaRepository<Sondage, Integer> {

}
