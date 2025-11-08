package com.testpaper.demo.resources;

import com.testpaper.demo.dto.OptionRequest;
import com.testpaper.demo.dto.QuestionRequest;
import com.testpaper.demo.dto.TagRequest;
import com.testpaper.demo.dto.UserRequest;
import com.testpaper.demo.dto.QuestionPaperRequest;
import com.testpaper.demo.service.QuestionService;
import com.testpaper.demo.service.TagService;
import com.testpaper.demo.service.UserService;
import com.testpaper.demo.service.QuestionPaperService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
public class UIController {

    private final UserService userService;
    private final QuestionService questionService;
    private final TagService tagService;
    private final QuestionPaperService questionPaperService;

    public UIController(UserService userService, QuestionService questionService, TagService tagService, QuestionPaperService questionPaperService) {
        this.userService = userService;
        this.questionService = questionService;
        this.tagService = tagService;
        this.questionPaperService = questionPaperService;
    }

    @GetMapping("/create-user")
    public String createUserForm(Model model) {
        model.addAttribute("userRequest", new UserRequest());
        return "user_create";
    }

    @PostMapping("/users1")
    public String createUser(@ModelAttribute UserRequest userRequest, RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(userRequest);
            redirectAttributes.addFlashAttribute("message", "User created successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/create-user";
    }

    @GetMapping("/add-question")
    public String addQuestionForm(Model model) {
        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setOptionRequests(new ArrayList<>()); // Corrected from setOptionRequests
        questionRequest.getOptionRequests().add(new OptionRequest());
        questionRequest.getOptionRequests().add(new OptionRequest());
        model.addAttribute("questionRequest", questionRequest);
        return "question_add";
    }

    @PostMapping("/questions1")
    public String addQuestion(@ModelAttribute QuestionRequest questionRequest, RedirectAttributes redirectAttributes) {
        try {
            questionService.createQuestion(questionRequest);
            redirectAttributes.addFlashAttribute("message", "Question added successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/add-question";
    }

    @GetMapping("/add-tag")
    public String addTagForm(Model model) {
        model.addAttribute("tagRequest", new TagRequest());
        return "tag_add";
    }

    @PostMapping("/tags1")
    public String addTag(@ModelAttribute TagRequest tagRequest, RedirectAttributes redirectAttributes) {
        try {
            tagService.createTag(tagRequest);
            redirectAttributes.addFlashAttribute("message", "Tag added successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/add-tag";
    }

    @GetMapping("/create-questionpaper")
    public String createQuestionPaperForm(Model model) {
        model.addAttribute("questionPaperRequest", new QuestionPaperRequest());
        return "questionpaper_create";
    }

    @PostMapping("/questionpapers1")
    public String createQuestionPaper(@ModelAttribute QuestionPaperRequest questionPaperRequest, RedirectAttributes redirectAttributes) {
        try {
            questionPaperService.createQuestionPaper(questionPaperRequest);
            redirectAttributes.addFlashAttribute("message", "Question Paper created successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/create-questionpaper";
    }
}
