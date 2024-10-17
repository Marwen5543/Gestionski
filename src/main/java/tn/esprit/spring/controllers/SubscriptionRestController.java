package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.DTO.SubscriptionDTO;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.services.ISubscriptionServices;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;

@Tag(name = "\uD83D\uDC65 Subscription Management")
@RestController
@Validated
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionRestController {

    private final ISubscriptionServices subscriptionServices;
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionRestController.class); // Define the logger

    @Operation(description = "Add Subscription")
    @PostMapping("/add")
    public ResponseEntity<Subscription> addSubscription(@Valid @RequestBody SubscriptionDTO subscriptionDTO) {
        try {
            // Map DTO to entity
            Subscription subscription = new Subscription();
            subscription.setStartDate(subscriptionDTO.getStartDate() == null ? LocalDate.now() : subscriptionDTO.getStartDate());
            subscription.setPrice(subscriptionDTO.getPrice());
            subscription.setTypeSub(subscriptionDTO.getTypeSub());

            // Calculate end date based on subscription type or duration
            LocalDate endDate = subscription.getStartDate().plusMonths(1); // Example logic
            subscription.setEndDate(endDate);

            // Add the subscription
            Subscription addedSubscription = subscriptionServices.addSubscription(subscription);

            return ResponseEntity.status(HttpStatus.CREATED).body(addedSubscription);
        } catch (IllegalArgumentException ex) {
            logger.error("Validation error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException ex) {
            logger.error("Error adding subscription: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }






    @Operation(description = "Retrieve Subscription by Id")
    @GetMapping("/get/{id-subscription}")
    public ResponseEntity<Subscription> getById(@PathVariable("id-subscription") Long numSubscription) {
        return subscriptionServices.retrieveSubscriptionById(numSubscription);
    }



    @Operation(description = "Retrieve Subscriptions by Type")
    @GetMapping("/all/{typeSub}")
    public ResponseEntity<Set<Subscription>> getSubscriptionsByType(@PathVariable("typeSub") TypeSubscription typeSubscription) {
        Set<Subscription> subscriptions = subscriptionServices.getSubscriptionByType(typeSubscription);
        return ResponseEntity.ok(subscriptions);
    }



    @Operation(description = "Update Subscription")
    @PutMapping("/update")
    public ResponseEntity<Subscription> updateSubscription(@RequestBody SubscriptionDTO subscriptionDTO) {
        Subscription subscription = new Subscription();
        subscription.setNumSub(subscriptionDTO.getNumSub());
        subscription.setStartDate(subscriptionDTO.getStartDate());
        subscription.setEndDate(subscriptionDTO.getEndDate());
        subscription.setPrice(subscriptionDTO.getPrice());
        subscription.setTypeSub(subscriptionDTO.getTypeSub());

        Subscription updatedSubscription = subscriptionServices.updateSubscription(subscription);

        if (updatedSubscription == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 if not found
        }

        return ResponseEntity.ok(updatedSubscription);
    }

    @Operation(description = "Retrieve Subscriptions created between two dates")
    @GetMapping("/all/{date1}/{date2}")
    public ResponseEntity<List<Subscription>> getSubscriptionsByDates(@PathVariable("date1") LocalDate startDate,
                                                                      @PathVariable("date2") LocalDate endDate) {
        List<Subscription> subscriptions = subscriptionServices.retrieveSubscriptionsByDates(startDate, endDate);
        return ResponseEntity.ok(subscriptions);
    }
}
