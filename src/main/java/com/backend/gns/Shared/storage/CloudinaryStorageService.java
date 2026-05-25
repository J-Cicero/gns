package com.backend.gns.Shared.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryStorageService {

  private final Cloudinary cloudinary;

  public CloudinaryStorageService(Cloudinary cloudinary) {
    this.cloudinary = cloudinary;
  }

    public Map<String, String> upload(MultipartFile fichier, String trackingId) {
    try {
        Map<String, Object> params = ObjectUtils.asMap(
          "folder", "studcash/documents/" + trackingId,
          "resource_type", "auto",
          "use_filename", true,
          "unique_filename", true
      );
        Map<String, Object> result = cloudinary.uploader()
          .upload(fichier.getBytes(), params);

        Map<String, String> response = new HashMap<>();
      response.put("url", (String) result.get("secure_url"));
      response.put("publicId", (String) result.get("public_id"));
      return response;

    } catch (IOException e) {
      throw new RuntimeException("Échec upload Cloudinary : " + e.getMessage());
    }
  }

  public void supprimer(String publicId) {
    try {
      cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    } catch (IOException e) {
      throw new RuntimeException("Échec suppression Cloudinary : " + e.getMessage());
    }
  }
}
