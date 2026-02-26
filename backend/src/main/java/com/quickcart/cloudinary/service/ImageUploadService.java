package com.quickcart.cloudinary.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {
	String upload(MultipartFile file);
}
