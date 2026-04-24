package com.backend.gns.Shared.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryStorageService {

  private final Cloudinary cloudinary;

  public CloudinaryStorageService(Cloudinary cloudinary) {
    this.cloudinary = cloudinary;
  }

  public String upload(MultipartFile fichier, String trackingId) {
    try {
      Map params = ObjectUtils.asMap(
          "folder", "studcash/kyc/" + trackingId,
          "resource_type", "auto",
          "use_filename", true,
          "unique_filename", true
      );
      Map result = cloudinary.uploader()
          .upload(fichier.getBytes(), params);

      return (String) result.get("secure_url");

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
