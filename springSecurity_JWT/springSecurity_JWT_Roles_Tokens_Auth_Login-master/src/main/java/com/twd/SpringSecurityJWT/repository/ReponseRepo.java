package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Question;
import com.twd.SpringSecurityJWT.entity.ReponseSondage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReponseRepo extends JpaRepository<ReponseSondage, Integer> {
    List<ReponseSondage> findReponseSondageByQuestion_Id(Integer idQuestion);


}
