
package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.repositories.ISubscriptionRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
public class SubscriptionServicesImpl implements ISubscriptionServices{

    private ISubscriptionRepository subscriptionRepository;

    private ISkierRepository skierRepository;

    @Override
    public Subscription addSubscription(Subscription subscription) {
        // Validate that the start date is not null
        if (subscription.getStartDate() == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }

        // Validate that the subscription type is not null
        if (subscription.getTypeSub() == null) {
            throw new IllegalArgumentException("Subscription type cannot be null");
        }

        // Calculate the end date based on the type of subscription
        switch (subscription.getTypeSub()) {
            case ANNUAL:
                subscription.setEndDate(subscription.getStartDate().plusYears(1));
                break;
            case SEMESTRIEL:
                subscription.setEndDate(subscription.getStartDate().plusMonths(6));
                break;
            case MONTHLY:
                subscription.setEndDate(subscription.getStartDate().plusMonths(1));
                break;
            default:
                throw new IllegalArgumentException("Invalid subscription type: " + subscription.getTypeSub());
        }

        // Save the subscription and handle potential exceptions
        try {
            return subscriptionRepository.save(subscription);
        } catch (Exception e) {
            log.error("Error saving subscription: {}", e.getMessage());
            throw new RuntimeException("Failed to save subscription", e);
        }
    }


    @Override
    public Subscription updateSubscription(Subscription subscription) {
        // Check if the subscription exists
        if (subscriptionRepository.existsById(subscription.getNumSub())) {
            return subscriptionRepository.save(subscription); // Proceed with the update
        }
        return null; // Return null if the subscription does not exist
    }



    @Override
    public ResponseEntity<Subscription> retrieveSubscriptionById(Long numSubscription) {
        return subscriptionRepository.findById(numSubscription)
                .map(ResponseEntity::ok) // Returns 200 OK with the found subscription
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)); // Returns 404 NOT FOUND if not found
    }



    @Override
    public Set<Subscription> getSubscriptionByType(TypeSubscription type) {
        return subscriptionRepository.findByTypeSubOrderByStartDateAsc(type);
    }

    @Override
    public List<Subscription> retrieveSubscriptionsByDates(LocalDate startDate, LocalDate endDate) {
        return subscriptionRepository.getSubscriptionsByStartDateBetween(startDate, endDate);
    }

    @Override
    @Scheduled(cron = "*/30 * * * * *") /* Cron expression to run a job every 30 secondes */
    public void retrieveSubscriptions() {
        for (Subscription sub: subscriptionRepository.findDistinctOrderByEndDateAsc()) {
            Skier   aSkier = skierRepository.findBySubscription(sub);
            log.info(sub.getNumSub().toString() + " | "+ sub.getEndDate().toString()
                    + " | "+ aSkier.getFirstName() + " " + aSkier.getLastName());
        }
    }

    // @Scheduled(cron = "* 0 9 1 * *") /* Cron expression to run a job every month at 9am */
    @Scheduled(cron = "*/30 * * * * *") // Cron expression to run a job every 30 seconds
    public void showMonthlyRecurringRevenue() {
        Float monthlyRevenue = subscriptionRepository.recurringRevenueByTypeSubEquals(TypeSubscription.MONTHLY);
        Float semestrielRevenue = subscriptionRepository.recurringRevenueByTypeSubEquals(TypeSubscription.SEMESTRIEL);
        Float annualRevenue = subscriptionRepository.recurringRevenueByTypeSubEquals(TypeSubscription.ANNUAL);

        // Use 0.0 as a default value if any revenue is null
        Float revenue = (monthlyRevenue != null ? monthlyRevenue : 0.0f)
                + (semestrielRevenue != null ? semestrielRevenue / 6 : 0.0f)
                + (annualRevenue != null ? annualRevenue / 12 : 0.0f);

        log.info("Monthly Revenue = " + revenue);
    }

}
