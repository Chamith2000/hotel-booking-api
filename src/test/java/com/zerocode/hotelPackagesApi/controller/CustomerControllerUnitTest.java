package com.zerocode.hotelPackagesApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerocode.hotelPackagesApi.controller.dto.CustomerDTO;
import com.zerocode.hotelPackagesApi.controller.request.CreateCustomerRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateCustomerRequest;
import com.zerocode.hotelPackagesApi.exception.CustomerNotFoundException;
import com.zerocode.hotelPackagesApi.exception.GlobalExceptionHandler;
import com.zerocode.hotelPackagesApi.exception.UserAlreadyExistsException;
import com.zerocode.hotelPackagesApi.model.LoyaltyPoint;
import com.zerocode.hotelPackagesApi.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerUnitTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createCustomer_Success() throws Exception {
        CreateCustomerRequest req = new CreateCustomerRequest();
        req.setUserName("john");
        req.setPassword("pw");
        req.setEmail("john@example.com");
        req.setProfilePhotoUrl("url");

        mockMvc.perform(post("/customers")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(customerService, times(1)).addCustomer(any(CreateCustomerRequest.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createCustomer_AlreadyExists() throws Exception {
        CreateCustomerRequest req = new CreateCustomerRequest();
        req.setUserName("john");
        req.setPassword("pw");
        req.setEmail("john@example.com");
        req.setProfilePhotoUrl("url");

        doThrow(new UserAlreadyExistsException("exists")).when(customerService).addCustomer(any(CreateCustomerRequest.class));

        mockMvc.perform(post("/customers")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getCustomerById_Success() throws Exception {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(1L);
        dto.setUserName("john");
        dto.setEmail("john@example.com");
        dto.setProfilePhotoUrl("url");
        dto.setLoyaltyPoint(new LoyaltyPoint());
        when(customerService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/customers/{customer_id}", 1L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userName").value("john"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getCustomerById_NotFound() throws Exception {
        when(customerService.getById(99L)).thenThrow(new CustomerNotFoundException("not found"));

        mockMvc.perform(get("/customers/{customer_id}", 99L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getCustomerById_InvalidId() throws Exception {
        mockMvc.perform(get("/customers/{customer_id}", "invalid")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updateCustomer_Success() throws Exception {
        UpdateCustomerRequest req = new UpdateCustomerRequest();
        req.setUserName("john");
        req.setPassword("pw");
        req.setEmail("john@example.com");
        req.setProfilePhotoUrl("url");

        mockMvc.perform(put("/customers/{customer_id}", 1L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(customerService, times(1)).update(eq(1L), any(UpdateCustomerRequest.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updateCustomer_NotFound() throws Exception {
        UpdateCustomerRequest req = new UpdateCustomerRequest();
        req.setUserName("john");
        req.setPassword("pw");
        req.setEmail("john@example.com");
        req.setProfilePhotoUrl("url");

        doThrow(new CustomerNotFoundException("not found")).when(customerService).update(eq(99L), any(UpdateCustomerRequest.class));

        mockMvc.perform(put("/customers/{customer_id}", 99L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updateCustomer_InvalidId() throws Exception {
        UpdateCustomerRequest req = new UpdateCustomerRequest();
        req.setUserName("john");
        req.setPassword("pw");
        req.setEmail("john@example.com");
        req.setProfilePhotoUrl("url");

        mockMvc.perform(put("/customers/{customer_id}", "invalid")
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deleteCustomer_Success() throws Exception {
        mockMvc.perform(delete("/customers/{customer_id}", 1L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isOk());
        verify(customerService, times(1)).delete(1L);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deleteCustomer_NotFound() throws Exception {
        doThrow(new CustomerNotFoundException("not found")).when(customerService).delete(99L);
        mockMvc.perform(delete("/customers/{customer_id}", 99L)
                .header("X-Api-Version", "v1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deleteCustomer_InvalidId() throws Exception {
        mockMvc.perform(delete("/customers/{customer_id}", "invalid")
                .header("X-Api-Version", "v1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void addProfilePicture_Success() throws Exception {
        mockMvc.perform(put("/customers/{customer_id}/add-profile-picture", 1L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("http://example.com/pic.jpg")))
                .andExpect(status().isOk());
        verify(customerService, times(1)).addProfilePhoto(eq(1L), any(String.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void addProfilePicture_NotFound() throws Exception {
        doThrow(new CustomerNotFoundException("not found")).when(customerService).addProfilePhoto(eq(99L), any(String.class));
        mockMvc.perform(put("/customers/{customer_id}/add-profile-picture", 99L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("http://example.com/pic.jpg")))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void addUserName_Success() throws Exception {
        mockMvc.perform(put("/customers/{customer_id}/add-username", 1L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("newUser")))
                .andExpect(status().isOk());
        verify(customerService, times(1)).updateUserName(eq(1L), any(String.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void addUserName_NotFound() throws Exception {
        doThrow(new CustomerNotFoundException("not found")).when(customerService).updateUserName(eq(99L), any(String.class));
        mockMvc.perform(put("/customers/{customer_id}/add-username", 99L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("newUser")))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void addEmail_Success() throws Exception {
        mockMvc.perform(put("/customers/{customerId}/add-email", 1L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("new@email.com")))
                .andExpect(status().isOk());
        verify(customerService, times(1)).updateUserEmail(eq(1L), any(String.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void addEmail_NotFound() throws Exception {
        doThrow(new CustomerNotFoundException("not found")).when(customerService).updateUserEmail(eq(99L), any(String.class));
        mockMvc.perform(put("/customers/{customerId}/add-email", 99L)
                .header("X-Api-Version", "v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("new@email.com")))
                .andExpect(status().isNotFound());
    }
}
