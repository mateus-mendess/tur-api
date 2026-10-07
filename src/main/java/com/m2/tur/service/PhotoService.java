package com.m2.tur.service;

import com.m2.tur.infra.client.SupabaseStorageClient;
import com.m2.tur.infra.exception.*;
import com.m2.tur.mapper.PhotoMapper;
import com.m2.tur.model.entity.Photo;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.PhotoRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final PhotoMapper photoMapper;
    private final TouristPointRepository touristPointRepository;
    private final AuthService authService;
    private final SupabaseStorageClient supabaseStorageClient;
    private final CacheManager cacheManager;

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024L;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    @Caching(evict = {
            @CacheEvict(cacheNames = "tourist-point-summary", allEntries = true),
            @CacheEvict(cacheNames = "stats-cache", allEntries = true),
            @CacheEvict(cacheNames = "tourist-point", key = "#touristPointId")
    })
    public void save(UUID touristPointId, MultipartFile file) {
        User user  = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User unauthorized."));

        TouristPoint touristPoint = touristPointRepository.findById(touristPointId)
                .orElseThrow(() -> new NotFoundException("Tourist Point Not Found."));

        if (!touristPoint.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("User not allowed to save photos.");
        }

        validate(file, touristPointId);

        String path = supabaseStorageClient.upload(file);

        try {
            Photo photo = photoMapper.toEntity(path);
            photo.setTouristPoint(touristPoint);

            photoRepository.save(photo);
        } catch (Exception e) {
            supabaseStorageClient.delete(path);
            throw e;
        }
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "tourist-point-summary", allEntries = true),
            @CacheEvict(cacheNames = "stats-cache"),
    })
    public void delete(UUID id) {
        User user  = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User unauthorized."));

        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Photo not found."));

        TouristPoint touristPoint = touristPointRepository.findById(photo.getTouristPoint().getId())
                .orElseThrow(() -> new NotFoundException("Tourist Point Not Found."));

        if (!touristPoint.getUser().equals(user)) {
            throw new ForbiddenException("User not allowed to save photos.");
        }

        photoRepository.delete(photo);

        var cache = cacheManager.getCache("tourist-point");
        if (cache != null) {
            cache.evict(touristPoint.getId());
        }

        try {
            supabaseStorageClient.delete(photo.getPath());
        } catch (Exception e) {
            log.error("Failed to delete file from Supabase after database deletion: {}", e.getMessage());
        }
    }

    public void deleteByTouristPoint(TouristPoint touristPoint) {
        Set<Photo> photos = touristPoint.getPhotos();

        photoRepository.deleteAll(touristPoint.getPhotos());

        for (Photo photo : photos) {
            try {
                supabaseStorageClient.delete(photo.getPath());
            } catch (Exception e) {
                log.error("Failed to delete orphaned photo from supabase: {}", photo.getPath());
            }
        }
    }

    private void validate(MultipartFile file, UUID touristPointId) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File cannot be empty");
        }
        try {
            Tika tika = new Tika();
            String trueMimeType = tika.detect(file.getInputStream());

            if (!ALLOWED_TYPES.contains(trueMimeType)) {
                throw new InvalidFileException("Invalid file type");
            }
        } catch (IOException e) {
            throw new InvalidFileException("Failed to read file content for validation");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("File is too large");
        }

        if (photoRepository.countByTouristPointId(touristPointId) >= 4) {
            throw new PhotoLimitExceededException("Photo limit exceeded");
        }
    }
}
