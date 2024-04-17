package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Feedback;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.FeedbackRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor

public class FeedbackServiceImp implements IFeedBackService {
    FeedbackRepo feedbackRepo;
    @Autowired
    private EntityManager entityManager;
    @Override
    public Feedback addFeedback(Feedback feedback) {
        return feedbackRepo.save(feedback);
    }

    @Override
    public Feedback updateFeedback(Feedback updatedFeedback) {
        Integer feedbackId = updatedFeedback.getIdFeedback(); // Assuming there's an ID field

        // Retrieve existing Feedback entity from the database
        Optional<Feedback> existingFeedbackOptional = feedbackRepo.findById(feedbackId);

        if (existingFeedbackOptional.isEmpty()) {
            throw new RuntimeException("Feedback not found with ID: " + feedbackId);
        }

        Feedback existingFeedback = existingFeedbackOptional.get();

        // Update specific fields (excluding createdByFb)
        existingFeedback.setContenu(updatedFeedback.getContenu());
        existingFeedback.setSubmissionDate(updatedFeedback.getSubmissionDate());

        // Save the updated Feedback
        return feedbackRepo.save(existingFeedback);
    }

    @Override
    public void removeFeedback(Integer idFeedback) {
        feedbackRepo.deleteById(idFeedback);

    }

    @Override
    public Optional<Feedback> retrieveFeedback(Integer idFeedback) {
        return feedbackRepo.findById(idFeedback);
    }

    @Override
    public List<Feedback> retrieveAllFeedback() {
        return feedbackRepo.findAll();
    }

    @Override
    public List<Feedback> addListFeedback(List<Feedback> feedbacks) {
        return feedbackRepo.saveAll(feedbacks);
    }

    @Override
    public Feedback getFeedbackById(Integer idFeedback) {
        return null;
    }

    @Override
    public List<Feedback> searchFeedbacks(Users createdByUser, String contenu, Date submissionDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Feedback> query = cb.createQuery(Feedback.class);
        Root<Feedback> root = query.from(Feedback.class);

        // Create predicates based on the provided criteria
        List<Predicate> predicates = new ArrayList<>();
        if (createdByUser != null) {
            predicates.add(cb.equal(root.get("createdBy"), createdByUser));
        }
        if (contenu != null && !contenu.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("contenu")), "%" + contenu.toLowerCase() + "%"));
        }
        if (submissionDate != null) {
            predicates.add(cb.equal(root.get("submissionDate"), submissionDate));
        }

        // Build the WHERE clause using the predicates
        query.where(predicates.toArray(new Predicate[0]));

        // Execute the query and return the results
        TypedQuery<Feedback> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

}
