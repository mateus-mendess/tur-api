package com.m2.tur.service;

import com.m2.tur.core.exception.*;
import com.m2.tur.factory.PhotoFactory;
import com.m2.tur.factory.TouristPointFactory;
import com.m2.tur.factory.UserFactory;
import com.m2.tur.infra.client.SupabaseStorageClient;
import com.m2.tur.mapper.PhotoMapper;
import com.m2.tur.model.entity.Photo;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.PhotoRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PhotoServiceTest {
    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private PhotoMapper photoMapper;

    @Mock
    private TouristPointRepository touristPointRepository;

    @Mock
    private AuthService authService;

    @Mock
    private SupabaseStorageClient supabaseStorageClient;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private PhotoService photoService;

    @Captor
    private ArgumentCaptor<Photo> captor;

    private User user;
    private TouristPoint touristPoint;
    private UUID touristPointId;
    private MultipartFile file;
    private Photo photo;
    private UUID id;

    @BeforeEach
    void setUp() {
        touristPoint = TouristPointFactory.createEntity();
        photo = PhotoFactory.createEntity(touristPoint);
        id = photo.getId();
        touristPointId = touristPoint.getId();
        user = touristPoint.getUser();
        file = mock(MultipartFile.class);
    }

    @Nested
    class Save {
        @Test
        void should_save_photo_with_success() {
            //Arrange
            when(file.isEmpty()).thenReturn(false);
            when(file.getContentType()).thenReturn("image/jpeg");
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.of(touristPoint));
            when(supabaseStorageClient.upload(file)).thenReturn(photo.getPath());
            when(photoMapper.toEntity(any(String.class))).thenReturn(photo);
            when(photoRepository.save(photo)).thenReturn(photo);

            //Act & Assert
            photoService.save(touristPointId, file);

            verify(photoRepository).save(captor.capture());

            var captured = captor.getValue();

            assertEquals(user, captured.getTouristPoint().getUser());
            assertEquals(photo.getPath(), captured.getPath());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, () -> photoService.save(touristPointId, file));

            verify(supabaseStorageClient, times(0)).upload(file);
            verify(photoRepository, times(0)).save(any(Photo.class));
        }

        @Test
        void should_throw_not_found_exception_when_tourist_point_not_found() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(NotFoundException.class, () -> photoService.save(touristPointId, file));

            verify(supabaseStorageClient, times(0)).upload(any(MultipartFile.class));
            verify(photoRepository, times(0)).save(any(Photo.class));
        }

        @Test
        void should_throw_forbidden_exception_when_user_not_authorized() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.of(touristPoint));

            //Act & Assert
            assertThrows(ForbiddenException.class, () ->  photoService.save(touristPointId, file));

            verify(supabaseStorageClient, times(0)).upload(any(MultipartFile.class));
            verify(touristPointRepository, times(0)).save(any(TouristPoint.class));
        }

        @Test
        void should_throw_invalid_file_exception_when_file_is_empty() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.of(touristPoint));
            when(file.isEmpty()).thenReturn(true);

            //Act & Assert
            var result = assertThrows(InvalidFileException.class, () -> photoService.save(touristPointId, file));

            verify(supabaseStorageClient, times(0)).upload(any(MultipartFile.class));
            verify(photoRepository, times(0)).save(any(Photo.class));

            assertEquals("File cannot be empty", result.getMessage());
        }

        @Test
        void should_throw_invalid_file_exception_when_file_exceeds_max_size() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.of(touristPoint));
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(2 * 1024 * 1024 + 1L);

            //Act & Assert
            var result = assertThrows(InvalidFileException.class, () -> photoService.save(touristPointId, file));

            verify(supabaseStorageClient, times(0)).upload(any(MultipartFile.class));
            verify(photoRepository, times(0)).save(any(Photo.class));

            assertEquals("File is too large", result.getMessage());
        }

        @Test
        void should_throw_invalid_file_exception_when_file_content_type_is_invalid() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.of(touristPoint));
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(2 * 1024 * 1024L);
            when(file.getContentType()).thenReturn("file/pdf");

            //Act & Assert
            var result = assertThrows(InvalidFileException.class, () -> photoService.save(touristPointId, file));

            verify(supabaseStorageClient, times(0)).upload(any(MultipartFile.class));
            verify(photoRepository, times(0)).save(any(Photo.class));

            assertEquals("Invalid file type", result.getMessage());
        }

        @Test
        void should_throw_invalid_file_exception_when_photo_limit_exceeded() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.of(touristPoint));
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(2 * 1024 * 1024L);
            when(file.getContentType()).thenReturn("image/jpeg");
            when(photoRepository.countByTouristPointId(touristPointId)).thenReturn(4);

            //Act & Assert
            assertThrows(PhotoLimitExceededException.class, () -> photoService.save(touristPointId, file));

            verify(supabaseStorageClient, times(0)).upload(any(MultipartFile.class));
            verify(photoRepository, times(0)).save(any(Photo.class));
        }

        @Test
        void should_throw_storage_exception_when_supabase_storage_fails() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.of(touristPoint));
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(2 * 1024 * 1024L);
            when(file.getContentType()).thenReturn("image/jpeg");
            when(photoRepository.countByTouristPointId(touristPointId)).thenReturn(3);
            when(supabaseStorageClient.upload(file)).thenThrow(StorageException.class);

            //Act & Assert
            assertThrows(StorageException.class, () -> photoService.save(touristPointId, file));

            verify(photoRepository, times(0)).save(any(Photo.class));
        }

        @Test
        void should_delete_uploaded_file_when_photo_save_fails() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.of(touristPoint));
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(2 * 1024 * 1024L);
            when(file.getContentType()).thenReturn("image/jpeg");
            when(photoRepository.countByTouristPointId(touristPointId)).thenReturn(3);
            when(supabaseStorageClient.upload(file)).thenReturn(photo.getPath());
            when(photoMapper.toEntity(photo.getPath())).thenReturn(photo);
            when(photoRepository.save(photo)).thenThrow(new DataAccessResourceFailureException("Failed to save photo entity."));

            //Act & Assert
            assertThrows(DataAccessResourceFailureException.class, () -> photoService.save(touristPointId, file));

            verify(supabaseStorageClient).upload(file);
            verify(photoRepository).save(any(Photo.class));
            verify(supabaseStorageClient).delete(photo.getPath());
        }
    }

    @Nested
    class Delete {
        @Test
        void should_delete_file_with_success() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(photoRepository.findById(id)).thenReturn(Optional.of(photo));
            when(touristPointRepository.findById(photo.getTouristPoint().getId())).thenReturn(Optional.of(touristPoint));
            when(cacheManager.getCache(anyString())).thenReturn(cache);

            //Act & Assert
            photoService.delete(id);

            verify(supabaseStorageClient).delete(photo.getPath());
            verify(photoRepository).delete(captor.capture());
            verify(cache).evict(touristPoint.getId());

            var captured = captor.getValue();

            assertEquals(user, captured.getTouristPoint().getUser());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, () -> photoService.delete(id));

            verify(supabaseStorageClient, times(0)).delete(anyString());
            verify(photoRepository, times(0)).delete(any(Photo.class));
            verifyNoInteractions(cacheManager);
        }

        @Test
        void should_throw_not_found_exception_when_photo_not_found() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(photoRepository.findById(id)).thenReturn(Optional.empty());

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> photoService.delete(id));

            verify(supabaseStorageClient, times(0)).delete(anyString());
            verify(photoRepository, times(0)).delete(any(Photo.class));
            verifyNoInteractions(cacheManager);

            assertEquals("Photo not found.", result.getMessage());
        }

        @Test
        void should_throw_not_found_exception_when_tourist_point_not_found() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(photo.getTouristPoint().getUser()));
            when(photoRepository.findById(id)).thenReturn(Optional.of(photo));
            when(touristPointRepository.findById(photo.getTouristPoint().getId())).thenReturn(Optional.empty());

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> photoService.delete(id));

            verify(supabaseStorageClient, times(0)).delete(anyString());
            verify(photoRepository, times(0)).delete(any(Photo.class));
            verifyNoInteractions(cacheManager);

            assertEquals("Tourist Point Not Found.", result.getMessage());


        }

        @Test
        void should_throw_forbidden_exception_when_user_not_authorized() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(photoRepository.findById(id)).thenReturn(Optional.of(photo));
            when(touristPointRepository.findById(photo.getTouristPoint().getId())).thenReturn(Optional.of(touristPoint));

            //Act & Assert
            assertThrows(ForbiddenException.class, () -> photoService.delete(id));

            verify(supabaseStorageClient, times(0)).delete(anyString());
            verify(photoRepository, times(0)).delete(any(Photo.class));
            verifyNoInteractions(cacheManager);
        }

        @Test
        void should_throw_storage_exception_when_supabase_storage_fails() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(photo.getTouristPoint().getUser()));
            when(photoRepository.findById(id)).thenReturn(Optional.of(photo));
            when(touristPointRepository.findById(photo.getTouristPoint().getId())).thenReturn(Optional.of(photo.getTouristPoint()));
            doThrow(StorageException.class).when(supabaseStorageClient).delete(photo.getPath());

            //Act & Assert
            assertThrows(StorageException.class, () -> photoService.delete(id));

            verify(photoRepository, times(0)).delete(any(Photo.class));
            verifyNoInteractions(cacheManager);
        }
    }

    @Nested
    class DeleteByTouristPoint {
        @Test
        void should_delete_all_photos_with_success() {
            //Arrange
            doNothing().when(supabaseStorageClient).delete(any(String.class));
            doNothing().when(photoRepository).deleteAll(anySet());

            //Act & Assert
            photoService.deleteByTouristPoint(touristPoint);

            verify(supabaseStorageClient).delete(photo.getPath());
            verify(photoRepository).deleteAll(touristPoint.getPhotos());
        }

        @Test
        void should_throw_storage_exception_when_supabase_storage_fails() {
            //Arrange
            doThrow(StorageException.class).when(supabaseStorageClient).delete(photo.getPath());

            //Act & Assert
            assertThrows(StorageException.class, () -> photoService.deleteByTouristPoint(touristPoint));

            verify(photoRepository, times(0)).deleteAll(anySet());
        }
    }
}
