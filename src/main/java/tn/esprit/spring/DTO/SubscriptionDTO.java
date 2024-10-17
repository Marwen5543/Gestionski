package tn.esprit.spring.DTO;

import tn.esprit.spring.entities.TypeSubscription;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

public class SubscriptionDTO {

    private Long numSub;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    private LocalDate endDate; // Optional field

    @Positive(message = "Price must be positive")
    private Float price;

    @NotNull(message = "Type cannot be null")
    private TypeSubscription typeSub;

    // Getters and Setters
    public Long getNumSub() {
        return numSub;
    }

    public void setNumSub(Long numSub) {
        this.numSub = numSub;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public TypeSubscription getTypeSub() {
        return typeSub;
    }

    public void setTypeSub(TypeSubscription typeSub) {
        this.typeSub = typeSub;
    }
}
