package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Sondage;

import java.util.List;
import java.util.Optional;

public interface ISondageService {
    Sondage addSondage(Sondage sondage);
    Sondage updateSondage(Sondage sondage);

    void removeSondage(Integer idSondage);

    Sondage retrieveSondage(Integer idSondage);

    List<Sondage> retrieveAllSondage();
    Optional<Sondage> getSondagebyId(Integer idSondage);
    List<Sondage> addListSondage(List<Sondage> sondages);

}
