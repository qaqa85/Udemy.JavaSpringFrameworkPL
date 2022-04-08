package com.webExample.demo.controller;

import com.webExample.demo.logic.ProjectService;
import com.webExample.demo.model.Project;
import com.webExample.demo.model.ProjectStep;
import com.webExample.demo.model.projection.ProjectWriteModel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/projects")
class ProjectController {
    private final ProjectService projectService;

    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    String showProjects(Model model) {
        model.addAttribute("project", new ProjectWriteModel());
        return "projects";
    }

    @PostMapping
    String addProject(
            @ModelAttribute("project") @Valid ProjectWriteModel current,
            BindingResult bindingResult, //Can tell us if the previous argument had any errors;
            Model model) {
        if(bindingResult.hasErrors()) {
            return "projects";
        }
        projectService.createProject(current);
        model.addAttribute("project", new ProjectWriteModel());
        model.addAttribute("projects", getProjects());
        model.addAttribute("message", "Dodano projekt!");
        return "projects";
    }

    @PostMapping(params = "addStep")
    String addProjectStep(@ModelAttribute("project") ProjectWriteModel current) {
        current.getSteps().add(new ProjectStep());
        return "projects";
    }

    @PostMapping("/{id}")
    String createGroup(
            @ModelAttribute("project") ProjectWriteModel current,
            Model model,
            @PathVariable int id,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime deadline
    ) {
        try {
            projectService.createGroup(id, deadline);
            model.addAttribute("message", "Dodano grupę!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            model.addAttribute("message", "Błąd podczas tworzenia grupy");
        }
        return "projects";
    }

    @ModelAttribute("projects")
    List<Project> getProjects() {
        return projectService.readAll();
    }
}