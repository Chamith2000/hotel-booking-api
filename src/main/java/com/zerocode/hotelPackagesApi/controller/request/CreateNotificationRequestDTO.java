package com.zerocode.hotelPackagesApi.controller.request;

import com.zerocode.hotelPackagesApi.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateNotificationRequestDTO {

    @NotBlank(message = "Message cannot be blank")
    @Size(min = 1, max = 500, message = "Message must be between 1 and 500 characters")
    private String message;

    @NotNull(message = "Sent date and time cannot be null")
    private LocalDateTime sentDateAndTime;

    @NotNull(message = "Status cannot be null")
    private Boolean status;

    @NotNull(message = "Role cannot be null")
    private Role role;

    public boolean isStatus() {
        return status;
    }
}
