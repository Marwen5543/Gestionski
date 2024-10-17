package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Registration;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.services.IRegistrationServices;

import java.util.List;

@Tag(name = "\uD83D\uDDD3️Registration Management")
@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationRestController {
    private final IRegistrationServices registrationServices;

    @Operation(description = "Add Registration and Assign to Skier")
    @PutMapping(value = "/addAndAssignToSkier/{numSkieur}", consumes = "application/json") // Added consumes attribute
    public Registration addAndAssignToSkier(@RequestBody Registration registration,
                                            @PathVariable("numSkieur") Long numSkieur) {
        return registrationServices.addRegistrationAndAssignToSkier(registration, numSkieur);
    }

    @Operation(description = "Assign Registration to Course")
    @PutMapping(value = "/assignToCourse/{numRegis}/{numSkieur}", consumes = "application/json") // Added consumes attribute
    public Registration assignToCourse(@PathVariable("numRegis") Long numRegistration,
                                       @PathVariable("numSkieur") Long numSkieur) {
        return registrationServices.assignRegistrationToCourse(numRegistration, numSkieur);
    }

    @Operation(description = "Add Registration and Assign to Skier and Course")
    @PutMapping(value = "/registration/addAndAssignToSkierAndCourse/{skierId}/{courseId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Registration addAndAssignToSkierAndCourse(@RequestBody Registration registration,
                                                     @PathVariable Long skierId,
                                                     @PathVariable Long courseId) {
        return registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, skierId, courseId);
    }

    @Operation(description = "Numbers of the weeks when an instructor has given lessons in a given support")
    @GetMapping("/numWeeks/{numInstructor}/{support}")
    public List<Integer> numWeeksCourseOfInstructorBySupport(@PathVariable("numInstructor") Long numInstructor,
                                                             @PathVariable("support") Support support) {
        return registrationServices.numWeeksCourseOfInstructorBySupport(numInstructor, support);
    }
}
