package tn.esprit.spring.entities;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Subscription implements Serializable {

	private static final long serialVersionUID = 1L; // Added serialVersionUID

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long numSub;

	@NotNull(message = "Start date cannot be null")
	LocalDate startDate;

	LocalDate endDate; // Optional field, no comment needed

	@Positive(message = "Price must be positive")
	Float price;

	@NotNull(message = "Type cannot be null")
	@Enumerated(EnumType.ORDINAL)

	TypeSubscription typeSub;

	// Constructor without endDate
	public Subscription(Long numSub, LocalDate startDate, Float price, TypeSubscription typeSub) {
		this.numSub = numSub;
		this.startDate = startDate;
		this.price = price;
		this.typeSub = typeSub;
	}

	// Constructor including endDate
	public Subscription(Long numSub, LocalDate startDate, LocalDate endDate, Float price, TypeSubscription typeSub) {
		this.numSub = numSub;
		this.startDate = startDate;
		this.endDate = endDate;
		this.price = price;
		this.typeSub = typeSub;
	}
}
