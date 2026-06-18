package com.backend.gns.user.domain.services;

import com.backend.gns.user.application.dtos.requests.AdminBanqueRequest;
import com.backend.gns.user.application.dtos.requests.LoginRequest;
import com.backend.gns.user.application.dtos.requests.UserRequest;
import com.backend.gns.user.application.dtos.responses.LoginResponse;
import com.backend.gns.user.application.dtos.responses.UserResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserService {

  UserResponse createUser(UserRequest request);
  UserResponse createAdminBanque(AdminBanqueRequest request);
  LoginResponse login(LoginRequest request);
  UserResponse getUserByTrackingId(UUID trackingId);

  UserResponse updateUserEtat(UUID trackingId, boolean etat);
  void deleteUser(UUID trackingId);

  Page<UserResponse> getAllUsers(int page, int size);

  java.util.List<UserResponse> searchUsers(String query);
}