package com.testpaper.demo.resources;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @GetMapping("/create-user")
    public String createUserForm() {
        return "user_create";
    }

    @GetMapping("/add-question")
    public String addQuestionForm() {
        return "question_add";
    }

    @GetMapping("/add-tag")
    public String addTagForm() {
        return "tag_add";
    }

    @GetMapping("/create-questionpaper")
    public String createQuestionPaperForm() {
        return "questionpaper_create";
    }
}
