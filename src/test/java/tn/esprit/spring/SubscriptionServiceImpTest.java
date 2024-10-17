package tn.esprit.spring;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import tn.esprit.spring.controllers.SubscriptionRestController;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.services.ISubscriptionServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@WebMvcTest(SubscriptionRestController.class)
public class SubscriptionServiceImpTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockBean
    private ISubscriptionServices subscriptionServices;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    /** Test to verify that a subscription can be added successfully. */
    @Test
    public void testAddSubscriptionSuccess() throws Exception {
        // Setup a valid subscription JSON
        LocalDate startDate = LocalDate.now();
        Subscription mockSubscription = new Subscription(1L, startDate, null, 99.99f, TypeSubscription.MONTHLY);

        // Mock the service to return the Subscription object
        when(subscriptionServices.addSubscription(any(Subscription.class))).thenReturn(mockSubscription);

        // Perform the request and validate the response
        mockMvc.perform(post("/subscription/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockSubscription)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numSub").value(1L)) // Validate the subscription ID
                .andExpect(jsonPath("$.price").value(99.99f)) // Validate the price
                .andExpect(jsonPath("$.typeSub").value("MONTHLY")); // Validate the subscription type
    }

    /** Test to verify retrieving a subscription by its ID. */
    @Test
    public void testGetById() throws Exception {
        // Create a mock Subscription object
        Subscription subscription = new Subscription(1L, LocalDate.now(), null, 99.99F, TypeSubscription.MONTHLY);

        // Create a ResponseEntity to mock the service response
        ResponseEntity<Subscription> responseEntity = ResponseEntity.ok(subscription);

        // Mock the service call
        when(subscriptionServices.retrieveSubscriptionById(1L)).thenReturn(responseEntity);

        // Perform the request and validate the response
        mockMvc.perform(get("/subscription/get/{id-subscription}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSub").value(1L))
                .andExpect(jsonPath("$.typeSub").value("MONTHLY"));
    }


    /** Test to verify retrieving subscriptions by type. */
    @Test
    public void testGetSubscriptionsByType() throws Exception {
        Set<Subscription> subscriptions = new HashSet<>();
        Subscription subscription = new Subscription(1L, LocalDate.now(), null, 99.99F, TypeSubscription.MONTHLY);
        subscriptions.add(subscription);

        when(subscriptionServices.getSubscriptionByType(TypeSubscription.MONTHLY)).thenReturn(subscriptions);

        mockMvc.perform(get("/subscription/all/{typeSub}", TypeSubscription.MONTHLY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numSub").value(1L))
                .andExpect(jsonPath("$[0].typeSub").value("MONTHLY"));
    }

    /** Test to verify that an exception is thrown when adding a subscription fails. */
    @Test
    public void testAddSubscriptionThrowsException() throws Exception {
        // Setup a subscription JSON
        Subscription subscription = new Subscription();
        subscription.setStartDate(LocalDate.now());
        subscription.setTypeSub(TypeSubscription.MONTHLY);
        subscription.setPrice(99.99F);

        // Mock the service to throw an exception
        when(subscriptionServices.addSubscription(any(Subscription.class)))
                .thenThrow(new RuntimeException("Error adding subscription"));

        // Perform the request and validate the response
        mockMvc.perform(post("/subscription/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscription)))
                .andExpect(status().isInternalServerError());
    }

    /** Test to validate input constraints when adding a subscription. */
    @Test
    public void testAddSubscriptionInvalidInput() throws Exception {
        String invalidSubscriptionJson = "{ \"startDate\": \"\", \"typeSub\": \"INVALID_TYPE\", \"price\": -10 }"; // Invalid data

        mockMvc.perform(post("/subscription/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidSubscriptionJson))
                .andExpect(status().isBadRequest());
    }

    /** Test to verify that retrieving subscriptions by type returns an empty list when no subscriptions exist. */
    @Test
    public void testGetSubscriptionsByTypeEmptyList() throws Exception {
        when(subscriptionServices.getSubscriptionByType(TypeSubscription.MONTHLY)).thenReturn(new HashSet<>());

        mockMvc.perform(get("/subscription/all/{typeSub}", TypeSubscription.MONTHLY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    /** Test to verify that adding a subscription with a price out of range is handled correctly. */
    @Test
    public void testAddSubscriptionPriceOutOfRange() throws Exception {
        Subscription subscription = new Subscription();
        subscription.setPrice(9999.99f); // Assuming this is out of range

        mockMvc.perform(post("/subscription/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscription)))
                .andExpect(status().isBadRequest());
    }

    /** Test to verify the end date calculation for a monthly subscription. */
    @Test
    public void testSubscriptionEndDateCalculation() throws Exception {
        // Create a subscription
        LocalDate startDate = LocalDate.now();
        Subscription subscription = new Subscription();
        subscription.setStartDate(startDate);
        subscription.setTypeSub(TypeSubscription.MONTHLY);
        subscription.setPrice(99.99F);

        // Calculate expected end date
        LocalDate expectedEndDate = startDate.plusMonths(1);
        Subscription mockSubscription = new Subscription(1L, startDate, expectedEndDate, 99.99F, TypeSubscription.MONTHLY);

        // Mock the service to return the expected subscription
        when(subscriptionServices.addSubscription(any(Subscription.class))).thenReturn(mockSubscription);

        // Perform the request
        mockMvc.perform(post("/subscription/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscription)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.endDate").value(expectedEndDate.toString())); // Check the end date
    }
}
