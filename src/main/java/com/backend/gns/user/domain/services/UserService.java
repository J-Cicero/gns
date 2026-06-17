package com.backend.gns.user.domain.services;

import com.backend.gns.user.application.dtos.requests.AdminBanqueRequest;
import com.backend.gns.user.application.dtos.requests.UserRequest;
import com.backend.gns.user.application.dtos.responses.UserResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface UserService {

  UserResponse createUser(UserRequest request);
  UserResponse createAdminBanque(AdminBanqueRequest request);
  UserResponse registerStudent(com.backend.gns.student.application.dtos.requests.StudentRequest request, org.springframework.web.multipart.MultipartFile rib, org.springframework.web.multipart.MultipartFile mandat);
  UserResponse registerMerchant(com.backend.gns.commerce.application.dtos.requests.MerchantRequest request, org.springframework.web.multipart.MultipartFile rib);

  com.backend.gns.user.application.dtos.responses.LoginResponse login(
      com.backend.gns.user.application.dtos.requests.LoginRequest request);

  UserResponse getUserByTrackingId(UUID trackingId);

  UserResponse updateUserEtat(UUID trackingId, boolean etat);

  void deleteUser(UUID trackingId);

  Page<UserResponse> getAllUsers(int page, int size);

  java.util.List<UserResponse> searchUsers(String query);
}