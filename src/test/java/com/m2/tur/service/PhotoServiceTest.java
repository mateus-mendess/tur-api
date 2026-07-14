package com.m2.tur.service;

import com.m2.tur.factory.PhotoFactory;
import com.m2.tur.factory.TouristPointFactory;
import com.m2.tur.factory.UserFactory;
import com.m2.tur.infra.client.SupabaseStorageClient;
import com.m2.tur.infra.exception.*;
import com.m2.tur.mapper.PhotoMapper;
import com.m2.tur.model.entity.Photo;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.PhotoRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

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

    @InjectMocks
    private PhotoService photoService;

    @Captor
    private ArgumentCaptor<Photo> captor;

    @Nested
    class Save {
        @Test
        void should_save_photo_with_success() {
            //Arrange
            UUID touristPointId = UUID.randomUUID();
            MultipartFile file = mock(MultipartFile.class);
            TouristPoint touristPoint = TouristPointFactory.createEntity();
            Photo photo = PhotoFactory.createEntity();

            when(file.isEmpty()).thenReturn(false);
            when(file.getContentType()).thenReturn("image/jpeg");
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.of(touristPoint));
            when(supabaseStorageClient.upload(file)).thenReturn(photo.getPath());
            when(photoMapper.toEntity(any(String.class))).thenReturn(photo);
            when(photoRepository.save(photo)).thenReturn(photo);

            //Act & Assert
            assertDoesNotThrow(() -> photoService.save(touristPointId, file));

            verify(authService).getAuthenticatedUser();
            verify(supabaseStorageClient).upload(file);
            verify(photoMapper).toEntity(any(String.class));
            verify(photoRepository).save(captor.capture());
            verify(supabaseStorageClient, times(0)).delete(any(String.class));

            var captured = captor.getValue();

            assertEquals(touristPoint.getUser(), captured.getTouristPoint().getUser());
            assertEquals(touristPoint, captured.getTouristPoint());
            assertEquals(photo.getPath(), captured.getPath());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            UUID touristPointId = UUID.randomUUID();
            MultipartFile file = mock(MultipartFile.class);

            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, () -> photoService.save(touristPointId, file));

            verify(supabaseStorageClient, times(0)).upload(file);
            verify(photoRepository, times(0)).save(any(Photo.class));
        }

        @Test
        void should_throw_not_found_exception_when_tourist_point_not_found() {
            //Arrange
            UUID touristPointId = UUID.randomUUID();
            MultipartFile file = mock(MultipartFile.class);
            User user = UserFactory.createEntity();

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
            UUID touristPointId = UUID.randomUUID();
            MultipartFile file = mock(MultipartFile.class);
            User user = UserFactory.createEntity();
            TouristPoint touristPoint = TouristPointFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.of(touristPoint));

            //Act & Assert
            assertThrows(ForbiddenException.class, () ->  photoService.save(touristPointId, file));

            verify(authService).getAuthenticatedUser();
            verify(supabaseStorageClient, times(0)).upload(any(MultipartFile.class));
            verify(touristPointRepository, times(0)).save(any(TouristPoint.class));
        }

        @Test
        void should_throw_invalid_file_exception_when_file_is_empty() {
            //Arrange
            UUID touristPointId = UUID.randomUUID();
            MultipartFile file = mock(MultipartFile.class);
            TouristPoint touristPoint = TouristPointFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
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
            UUID touristPointId = UUID.randomUUID();
            MultipartFile file = mock(MultipartFile.class);
            TouristPoint touristPoint = TouristPointFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
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
            UUID touristPointId = UUID.randomUUID();
            MultipartFile file = mock(MultipartFile.class);
            TouristPoint touristPoint = TouristPointFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
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
            UUID touristPointId = UUID.randomUUID();
            MultipartFile file = mock(MultipartFile.class);
            TouristPoint touristPoint = TouristPointFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
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
            UUID touristPointId = UUID.randomUUID();
            MultipartFile file = mock(MultipartFile.class);
            TouristPoint touristPoint = TouristPointFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.of(touristPoint));
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(2 * 1024 * 1024L);
            when(file.getContentType()).thenReturn("image/jpeg");
            when(photoRepository.countByTouristPointId(touristPointId)).thenReturn(3);
            when(supabaseStorageClient.upload(file)).thenThrow(new StorageException("Failed to upload file."));

            //Act & Assert
            assertThrows(StorageException.class, () -> photoService.save(touristPointId, file));

            verify(photoRepository, times(0)).save(any(Photo.class));
        }

        @Test
        void should_delete_uploaded_file_when_photo_save_fails() {
            //Arrange
            UUID touristPointId = UUID.randomUUID();
            MultipartFile file = mock(MultipartFile.class);
            TouristPoint touristPoint = TouristPointFactory.createEntity();
            Photo photo = PhotoFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
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
            UUID id = UUID.randomUUID();
            Photo photo = PhotoFactory.createEntity();
            TouristPoint touristPoint = TouristPointFactory.createEntity();
            photo.setTouristPoint(touristPoint);

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
            when(photoRepository.findById(id)).thenReturn(Optional.of(photo));
            when(touristPointRepository.findById(photo.getTouristPoint().getId())).thenReturn(Optional.of(touristPoint));
            doNothing().when(supabaseStorageClient).delete(photo.getPath());
            doNothing().when(photoRepository).delete(photo);

            //Act & Assert
            assertDoesNotThrow(() -> photoService.delete(id));

            verify(authService).getAuthenticatedUser();
            verify(supabaseStorageClient).delete(photo.getPath());
            verify(photoRepository).delete(captor.capture());

            var captured = captor.getValue();

            assertEquals(touristPoint.getUser(), captured.getTouristPoint().getUser());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            UUID id = UUID.randomUUID();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, () -> photoService.delete(id));

            verify(supabaseStorageClient, times(0)).delete(anyString());
            verify(photoRepository, times(0)).delete(any(Photo.class));
        }

        @Test
        void should_throw_not_found_exception_when_photo_not_found() {
            //Arrange
            UUID id = UUID.randomUUID();
            TouristPoint touristPoint = TouristPointFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
            when(photoRepository.findById(id)).thenReturn(Optional.empty());

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> photoService.delete(id));

            verify(supabaseStorageClient, times(0)).delete(anyString());
            verify(photoRepository, times(0)).delete(any(Photo.class));

            assertEquals("Photo not found.", result.getMessage());
        }

        @Test
        void should_throw_not_found_exception_when_tourist_point_not_found() {
            //Arrange
            UUID id = UUID.randomUUID();
            Photo photo = PhotoFactory.createEntity();
            photo.setTouristPoint(TouristPointFactory.createEntity());

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(photo.getTouristPoint().getUser()));
            when(photoRepository.findById(id)).thenReturn(Optional.of(photo));
            when(touristPointRepository.findById(photo.getTouristPoint().getId())).thenReturn(Optional.empty());

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> photoService.delete(id));

            verify(supabaseStorageClient, times(0)).delete(anyString());
            verify(photoRepository, times(0)).delete(any(Photo.class));

            assertEquals("Tourist Point Not Found.", result.getMessage());


        }

        @Test
        void should_throw_forbidden_exception_when_user_not_authorized() {
            //Arrange
            UUID id = UUID.randomUUID();
            Photo photo = PhotoFactory.createEntity();
            User user = UserFactory.createEntity();
            photo.setTouristPoint(TouristPointFactory.createEntity());

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(photoRepository.findById(id)).thenReturn(Optional.of(photo));
            when(touristPointRepository.findById(photo.getTouristPoint().getId())).thenReturn(Optional.of(photo.getTouristPoint()));

            //Act & Assert
            assertThrows(ForbiddenException.class, () -> photoService.delete(id));

            verify(authService).getAuthenticatedUser();
            verify(supabaseStorageClient, times(0)).delete(anyString());
            verify(photoRepository, times(0)).delete(any(Photo.class));
        }

        @Test
        void should_throw_storage_exception_when_supabase_storage_fails() {
            //Arrange
            UUID id = UUID.randomUUID();
            Photo photo = PhotoFactory.createEntity();
            photo.setTouristPoint(TouristPointFactory.createEntity());

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(photo.getTouristPoint().getUser()));
            when(photoRepository.findById(id)).thenReturn(Optional.of(photo));
            when(touristPointRepository.findById(photo.getTouristPoint().getId())).thenReturn(Optional.of(photo.getTouristPoint()));
            doThrow(new StorageException("Failed to delete file.")).when(supabaseStorageClient).delete(photo.getPath());

            //Act & Assert
            assertThrows(StorageException.class, () -> photoService.delete(id));

            verify(photoRepository, times(0)).delete(any(Photo.class));
        }
    }
}
