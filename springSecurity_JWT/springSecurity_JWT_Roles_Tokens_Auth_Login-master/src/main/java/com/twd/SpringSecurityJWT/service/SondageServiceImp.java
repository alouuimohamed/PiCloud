package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Sondage;
import com.twd.SpringSecurityJWT.repository.SondageRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SondageServiceImp implements ISondageService{
SondageRepo sondageRepo;
    @Override
    public Sondage addSondage(Sondage sondage) {
        return sondageRepo.save(sondage);
    }

    @Override
    public Sondage updateSondage(Sondage updatedSondage) {
        Integer sondageId = updatedSondage.getIdSondage(); // Assuming there's an ID field

        // Retrieve existing Sondage entity from database
        Optional<Sondage> existingSondageOptional = sondageRepo.findById(sondageId);

        if (existingSondageOptional.isEmpty()) {
            throw new RuntimeException("Sondage not found with ID: " + sondageId);
        }

        Sondage existingSondage = existingSondageOptional.get();

        // Update specific fields (excluding createdBy)
        existingSondage.setTitle(updatedSondage.getTitle());
        existingSondage.setDescription(updatedSondage.getDescription());
        existingSondage.setStartDate(updatedSondage.getStartDate());
        existingSondage.setEndDate(updatedSondage.getEndDate());
        existingSondage.setActive(updatedSondage.isActive());

        // Save the updated Sondage
        return sondageRepo.save(existingSondage);
    }
    @Override
    public void removeSondage(Integer idSondage) {
        sondageRepo.deleteById(idSondage);

    }

    @Override
    public Sondage retrieveSondage(Integer idSondage) {
        return sondageRepo.findById(idSondage).orElse(null);
    }

    @Override
    public List<Sondage> retrieveAllSondage() {
        return sondageRepo.findAll();
    }

    @Override
    public Optional<Sondage> getSondagebyId(Integer idSondage) {
        return sondageRepo.findById(idSondage);
    }

    @Override
    public List<Sondage> addListSondage(List<Sondage> sondages) {
        return sondageRepo.saveAll(sondages);
    }
}
