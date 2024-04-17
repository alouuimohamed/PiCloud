package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.entity.Feedback;
import com.twd.SpringSecurityJWT.entity.Sondage;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.FeedbackRepo;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.service.IFeedBackService;
import com.twd.SpringSecurityJWT.service.JWTUtils;
import io.jsonwebtoken.MalformedJwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("FeedbackController")
public class FeedbackController {
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    IFeedBackService feedBackService;
    private OurUserRepo ourUserRepo;
    @GetMapping("/retrieve-All-Feedback")
    public List<Feedback> getAllFeedback(){
        return feedBackService.retrieveAllFeedback();
    }
    @PostMapping("/add-feedback")
    public ResponseEntity<?> addFeedback(@RequestHeader("Authorization") String token,
                                         @RequestBody Feedback feedback) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String jwtToken = token.substring(7);
            String userEmail = jwtUtils.extractUsername(jwtToken);

            Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Users user = userOptional.get();
            feedback.setCreatedByFb(user);

            Feedback createdFeedback = feedBackService.addFeedback(feedback);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFeedback);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid JWT token
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Internal server error
        }
    }
    @PutMapping("/update-feedback")
    public Feedback updateFeedback(@RequestBody Feedback feedback){return feedBackService.updateFeedback(feedback);}

    @PostMapping("/add-list-Feedback")
    public List<Feedback> addListFeedback(@RequestBody List<Feedback> feedbacks) {return feedBackService.addListFeedback(feedbacks);}
    @DeleteMapping("/{id-feedback}/delete-feedback")
    public void removeFeedback(@PathVariable("id-feedback") Integer idFeedback){
        feedBackService.removeFeedback(idFeedback);
    }
    @GetMapping("/search")
    public List<Feedback> searchFeedbacks(@RequestParam(required = false) Users createdBy,
                                          @RequestParam(required = false) String contenu,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date submissionDate) {
        return feedBackService.searchFeedbacks(createdBy, contenu, submissionDate);
    }

}
