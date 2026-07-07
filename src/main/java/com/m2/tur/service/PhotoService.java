package com.m2.tur.service;

import com.m2.tur.infra.exception.*;
import com.m2.tur.mapper.PhotoMapper;
import com.m2.tur.model.entity.Photo;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.PhotoRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final PhotoMapper photoMapper;
    private final TouristPointRepository touristPointRepository;
    private final AuthService authService;
    private final SupabaseStorageService supabaseStorageService;

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024L;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    @Transactional
    public void save(UUID touristPointId, MultipartFile file) throws IOException {
        User user  = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User unauthorized."));

        TouristPoint touristPoint = touristPointRepository.findById(touristPointId)
                .orElseThrow(() -> new NotFoundException("Tourist Point Not Found."));

        if (!touristPoint.getUser().equals(user)) {
            throw new ForbiddenException("User not allowed to save photos.");
        }

        validate(file, touristPointId);

        String path = supabaseStorageService.upload(file);

        Photo photo = photoMapper.toEntity(path);
        photo.setTouristPoint(touristPoint);

        photoRepository.save(photo);
    }

    @Transactional
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

        supabaseStorageService.delete(photo.getPath());

        photoRepository.delete(photo);
    }

    private void validate(MultipartFile file, UUID touristPointId) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("File is too large");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new InvalidFileException("Invalid file type");
        }

        if (photoRepository.countByTouristPointId(touristPointId) >= 4) {
            throw new PhotoLimitExceededException("Photo limit exceeded");
        }
    }
}
