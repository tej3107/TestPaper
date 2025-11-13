package com.testpaper.demo.resources;

import com.testpaper.demo.dto.*;
import com.testpaper.demo.model.TestUserResponse;
import com.testpaper.demo.service.*;
import com.testpaper.demo.repository.TestUserResponseRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import java.util.Map;
//import com.testpaper.demo.dto.TestUserResponse;

@Controller
public class UIController {

    private final UserService userService;
    private final QuestionService questionService;
    private final TagService tagService;
    private final QuestionPaperService questionPaperService;
    private final TestUserService testUserService;
    private final TestUserResponseRepository testUserResponseRepository;
    private final ResultService resultService;

    private static final String ATTEMPT_PROGRESS_SESSION_KEY = "attemptProgress";

    public UIController(UserService userService, QuestionService questionService, TagService tagService, QuestionPaperService questionPaperService, TestUserService testUserService, ResultService resultService, TestUserResponseRepository testUserResponseRepository) {
        this.userService = userService;
        this.questionService = questionService;
        this.tagService = tagService;
        this.questionPaperService = questionPaperService;
        this.testUserService = testUserService;
        this.resultService = resultService;
        this.testUserResponseRepository = testUserResponseRepository;
    }

    @GetMapping("/create-user")
    public String createUserForm(Model model) {
        model.addAttribute("userRequest", new UserRequest());
        return "user_create";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute UserRequest userRequest, RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(userRequest);
            redirectAttributes.addFlashAttribute("message", "User created successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/login";
    }

    @GetMapping("/add-question")
    public String addQuestionForm(Model model) {
        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setOptionRequests(new ArrayList<>());
        questionRequest.getOptionRequests().add(new OptionRequest());
        questionRequest.getOptionRequests().add(new OptionRequest());
        model.addAttribute("questionRequest", questionRequest);
        return "question_add";
    }

    @PostMapping("/questions")
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

    @PostMapping("/tags")
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

    @PostMapping("/questionpapers")
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

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            if (userService.getUserById(username) != null) {
                session.setAttribute("loggedInUsername", username);
                return "redirect:/user-dashboard";
            } else {
                redirectAttributes.addFlashAttribute("message", "User not found. Please create an account.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/login";
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/login";
        }
    }

    @GetMapping("/user-dashboard")
    public String userDashboard(HttpSession session, Model model) {
        String loggedInUsername = (String) session.getAttribute("loggedInUsername");
        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            return "redirect:/login";
        }
        try {
            UserResponse user = userService.getUserById(loggedInUsername);
            model.addAttribute("user", user);
            model.addAttribute("accessType", user.getAccessType());
            
            // Fetch completed test papers for the user
            List<TestUserResponse> completedTests = testUserResponseRepository.findByUserId(loggedInUsername);
            model.addAttribute("completedTests", completedTests);

            return "user_dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("message", "Error retrieving user details: " + e.getMessage());
            model.addAttribute("messageType", "error");
            return "redirect:/login";
        }
    }

    @GetMapping("/questionpapers-list")
    public String listQuestionPapers(
            @RequestParam(value = "start", defaultValue = "0") Integer start,
            @RequestParam(value = "count", defaultValue = "20") Integer count,
            Model model, HttpSession session) {
        String loggedInUsername = (String) session.getAttribute("loggedInUsername");
        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            return "redirect:/login";
        }
        try {
            List<QuestionPaperSummaryResponse> questionPapers = questionPaperService.getQuestionPaperSummaries(start, count);
            model.addAttribute("questionPapers", questionPapers);
            model.addAttribute("currentPage", start);
            model.addAttribute("pageSize", count);
            return "questionpapers_list";
        } catch (RuntimeException e) {
            model.addAttribute("message", "Error retrieving question papers: " + e.getMessage());
            model.addAttribute("messageType", "error");
            return "user_dashboard"; // Redirect back to dashboard on error
        }
    }

    @GetMapping("/questionpaper-attempt")
    public String attemptQuestionPaper(@RequestParam("paperId") String paperId,
                                       @RequestParam(value = "questionIndex", defaultValue = "0") int questionIndex,
                                       Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        String loggedInUsername = (String) session.getAttribute("loggedInUsername");
        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please log in to attempt a question paper.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/login";
        }

        AttemptProgress attemptProgress = (AttemptProgress) session.getAttribute(ATTEMPT_PROGRESS_SESSION_KEY);

        if (attemptProgress == null || !attemptProgress.getQuestionPaper().getId().equals(paperId)) {
            // New attempt or different paper, initialize session
            try {
                QuestionPaperResponse questionPaper = questionPaperService.getQuestionPaper(paperId);
                if (questionPaper.getQuestions() == null || questionPaper.getQuestions().isEmpty()) {
                    redirectAttributes.addFlashAttribute("message", "Question paper is empty or not found.");
                    redirectAttributes.addFlashAttribute("messageType", "error");
                    return "redirect:/questionpapers-list";
                }
                attemptProgress = new AttemptProgress(questionPaper, loggedInUsername);
                session.setAttribute(ATTEMPT_PROGRESS_SESSION_KEY, attemptProgress);
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("message", "Error attempting question paper: " + e.getMessage());
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/questionpapers-list";
            }
        }

        // Update current question index
        attemptProgress.setCurrentQuestionIndex(questionIndex);

        int totalQuestions = attemptProgress.getQuestionPaper().getQuestions().size();
        if (questionIndex < 0 || questionIndex >= totalQuestions) {
            // Invalid question index, redirect to the first question
            return "redirect:/questionpaper-attempt?paperId=" + paperId + "&questionIndex=0";
        }

        model.addAttribute("questionPaper", attemptProgress.getQuestionPaper());
        model.addAttribute("currentQuestion", attemptProgress.getQuestionPaper().getQuestions().get(questionIndex));
        model.addAttribute("currentQuestionIndex", questionIndex);
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("userAnswers", attemptProgress.getUserAnswers());
        model.addAttribute("loggedInUsername", loggedInUsername);

        return "questionpaper_attempt";
    }

    @PostMapping("/save-answer-and-navigate")
    public String saveAnswerAndNavigate(@RequestParam("paperId") String paperId,
                             @RequestParam("questionId") String questionId,
                             @RequestParam(value = "selectedOptionIds", required = false) List<String> selectedOptionIds,
                             @RequestParam("currentQuestionIndex") int currentQuestionIndex,
                             @RequestParam("targetQuestionIndex") int targetQuestionIndex,
                             @RequestParam("finalSubmit") String finalSubmit,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {

        String loggedInUsername = (String) session.getAttribute("loggedInUsername");
        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Session expired. Please log in again.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/login";
        }

        AttemptProgress attemptProgress = (AttemptProgress) session.getAttribute(ATTEMPT_PROGRESS_SESSION_KEY);

        if (attemptProgress == null || !attemptProgress.getQuestionPaper().getId().equals(paperId)) {
            redirectAttributes.addFlashAttribute("message", "Invalid session or question paper.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/questionpapers-list";
        }

        // Save the answer for the current question
        if (selectedOptionIds != null && !selectedOptionIds.isEmpty()) {
            attemptProgress.getUserAnswers().put(questionId, selectedOptionIds);
        } else {
            // If no option is selected, remove any previous answer for this question
            attemptProgress.getUserAnswers().remove(questionId);
        }

        if(finalSubmit!=null && finalSubmit.equals("true") ){
            // Redirect to submission if final submit is requested
            return "redirect:/submit-answers?paperId=" + paperId;
        }

        // Redirect to the target question index
        return "redirect:/questionpaper-attempt?paperId=" + paperId + "&questionIndex=" + targetQuestionIndex;
    }

    @GetMapping("/submit-answers")
    public String submitAnswers(@RequestParam("paperId") String paperId,
                                RedirectAttributes redirectAttributes, HttpSession session) {
        String loggedInUsername = (String) session.getAttribute("loggedInUsername");
        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Session expired. Please log in again.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/login";
        }

        AttemptProgress attemptProgress = (AttemptProgress) session.getAttribute(ATTEMPT_PROGRESS_SESSION_KEY);

        if (attemptProgress == null || !attemptProgress.getQuestionPaper().getId().equals(paperId)) {
            redirectAttributes.addFlashAttribute("message", "Invalid session or question paper.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/questionpapers-list";
        }

        try {
            TestUserAnswerRequest testUserAnswerRequest = new TestUserAnswerRequest();
            testUserAnswerRequest.setQuestionPaperId(paperId);
            testUserAnswerRequest.setUserId(loggedInUsername);

            List<QuestionAnswer> questionAnswers = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : attemptProgress.getUserAnswers().entrySet()) {
                QuestionAnswer qa = new QuestionAnswer();
                qa.setQuestionId(entry.getKey());
                qa.setSelectedOptionIds(entry.getValue());
                questionAnswers.add(qa);
            }
            testUserAnswerRequest.setAnswers(questionAnswers);

            testUserService.saveUserAnswers(testUserAnswerRequest);
            session.removeAttribute(ATTEMPT_PROGRESS_SESSION_KEY); // Clear session after submission
            redirectAttributes.addFlashAttribute("message", "Answers submitted successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/user-test-result?userId=" + loggedInUsername + "&paperId=" + paperId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", "Error submitting answers: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/questionpaper-attempt?paperId=" + paperId + "&questionIndex=" + attemptProgress.getCurrentQuestionIndex();
        }
    }

    @GetMapping("/user-test-result")
    public String getUserTestResult(@RequestParam("userId") String userId,
                                    @RequestParam("paperId") String paperId,
                                    Model model,
                                    RedirectAttributes redirectAttributes,
                                    HttpSession session) {
        String loggedInUsername = (String) session.getAttribute("loggedInUsername");
        if (loggedInUsername == null || loggedInUsername.isEmpty() || !loggedInUsername.equals(userId)) {
            redirectAttributes.addFlashAttribute("message", "Unauthorized access or session expired.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/login";
        }

        try {
            TestResultResponse testResult = resultService.getTestResults(userId, paperId);
            model.addAttribute("testResult", testResult);
            return "test_result";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", "Error retrieving test results: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/user-dashboard"; // Or a more appropriate error page
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
