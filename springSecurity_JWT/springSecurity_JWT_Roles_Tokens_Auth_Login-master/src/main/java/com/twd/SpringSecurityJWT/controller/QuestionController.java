package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.entity.Question;
import com.twd.SpringSecurityJWT.service.QuestionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("QuestionController")
public class QuestionController {
    QuestionService questionService;
    @GetMapping("/retrieve-All-Question")
    public List<Question> GetAllQuestion(){
        return questionService.retrieveAllQuestion();
    }
    @PostMapping("/add-question")
    public Question addQuestion(@RequestBody Question question) {return questionService.addQuestion(question);}
    @PutMapping("/update-question")
    public Question updateQuestion(@RequestBody Question question){return questionService.updateQuestion(question);}
    @DeleteMapping("/{id-question}/delete-Question")
    public void removeQuestion(@PathVariable("id-question") Integer idQuestion){
        questionService.removeQuestion(idQuestion);
    }
    @PostMapping("/add-list-Question")
    public List<Question> addListQuestion(@RequestBody List<Question> questions) {return questionService.addListQuestion(questions);}




}
