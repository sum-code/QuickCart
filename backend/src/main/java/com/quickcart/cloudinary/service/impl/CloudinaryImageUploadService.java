package com.quickcart.cloudinary.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.quickcart.cloudinary.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryImageUploadService implements ImageUploadService {

	private final Cloudinary cloudinary;
	private final String cloudName;
	private final String apiKey;
	private final String apiSecret;

	public CloudinaryImageUploadService(
			Cloudinary cloudinary,
			@Value("${cloudinary.cloud-name:}") String cloudName,
			@Value("${cloudinary.api-key:}") String apiKey,
			@Value("${cloudinary.api-secret:}") String apiSecret
	) {
		this.cloudinary = cloudinary;
		this.cloudName = cloudName;
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
	}

	@Override
	public String upload(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("Image file is required");
		}

		if (isMissingOrPlaceholder(cloudName) || isMissingOrPlaceholder(apiKey) || isMissingOrPlaceholder(apiSecret)) {
			throw new IllegalArgumentException("Cloudinary is not configured. Set cloudinary.cloud-name, cloudinary.api-key and cloudinary.api-secret.");
		}

		try {
			Map<?, ?> result = cloudinary.uploader().upload(
					file.getBytes(),
					ObjectUtils.asMap("folder", "quickcart/products", "resource_type", "image")
			);
			Object secureUrl = result.get("secure_url");
			if (secureUrl == null) {
				throw new IllegalArgumentException("Cloudinary upload failed: secure_url missing in response.");
			}
			return secureUrl.toString();
		} catch (IOException ex) {
			throw new IllegalArgumentException("Failed to upload image", ex);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Cloudinary upload failed: " + ex.getMessage(), ex);
		}
	}

	private boolean isMissingOrPlaceholder(String value) {
		if (value == null) {
			return true;
		}
		String text = value.trim();
		return text.isEmpty() || text.startsWith("your-cloudinary-");
	}
}
