package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Place;
import com.twd.SpringSecurityJWT.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepo extends JpaRepository<Reservation, Integer> {



}
