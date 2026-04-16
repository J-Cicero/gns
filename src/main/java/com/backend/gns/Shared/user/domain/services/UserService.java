package com.backend.gns.Shared.user.domain.services;

import java.util.UUID;

import com.backend.gns.Shared.user.application.dtos.requests.UserRequest;
import com.backend.gns.Shared.user.application.dtos.responses.UserResponse;
import org.springframework.data.domain.Page;


public interface UserService {

    UserResponse createUser(UserRequest request);
    UserResponse getUserByTrackingId(UUID trackingId);
    UserResponse updateUserEtat(UUID trackingId, boolean etat);
    void deleteUser(UUID trackingId);
    Page<UserResponse> getAllUsers(int page, int size);

}
