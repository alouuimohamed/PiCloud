package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.entity.Sondage;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.service.ISondageService;
import com.twd.SpringSecurityJWT.service.JWTUtils;
import io.jsonwebtoken.MalformedJwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("Sondagecontroller")
public class SondageController {
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
            private OurUserRepo ourUserRepo;

ISondageService sondageService;
    @GetMapping("/retrieve-All-Sondage")
    public List<Sondage> getAllSondage(){
        return sondageService.retrieveAllSondage();
    }
    @PostMapping("/add-sondage")
    public ResponseEntity<?> addSondage(@RequestHeader("Authorization") String token,
                                        @RequestBody Sondage sondage) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid or missing token
            }

            String jwtToken = token.substring(7);
            String userEmail = jwtUtils.extractUsername(jwtToken);

            Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // User not found or unauthorized
            }

            Users user = userOptional.get();
            sondage.setCreatedBy(user);

            Sondage createdSondage = sondageService.addSondage(sondage);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSondage);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PutMapping("/update-sondage")
    public ResponseEntity<?> updateSondage(@RequestBody Sondage sondage) {
        try {
            // Ensure the sondage has a valid id for updating
            if (sondage.getIdSondage() == null) {
                return ResponseEntity.badRequest().body("Sondage ID is required for update.");
            }
            Sondage sondage1 = new Sondage();


            Sondage updatedSondage = sondageService.updateSondage(sondage);

            if (updatedSondage == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(updatedSondage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/{id-sondage}/delete-sondage")
    public void removeSondage(@PathVariable("id-sondage") Integer idSondage){
        sondageService.removeSondage(idSondage);
    }
    @GetMapping("/sort/{sort-order}")
    public ResponseEntity<List<Sondage>> getSortedSondages(@PathVariable("sort-order") String sortOrder) {
        try {
            List<Sondage> sondages = sondageService.retrieveAllSondage();

            // Sort using Java Stream API
            List<Sondage> sortedSondages = sondages.stream()
                    .sorted((s1, s2) -> {
                        if ("asc".equalsIgnoreCase(sortOrder)) {
                            return s1.getEndDate().compareTo(s2.getEndDate());
                        } else if ("desc".equalsIgnoreCase(sortOrder)) {
                            return s2.getEndDate().compareTo(s1.getEndDate());
                        } else {
                            throw new IllegalArgumentException("Invalid sortOrder. Use 'asc' or 'desc'.");
                        }
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(sortedSondages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
