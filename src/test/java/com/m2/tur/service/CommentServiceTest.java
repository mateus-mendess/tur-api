package com.m2.tur.service;

import com.m2.tur.factory.CommentFactory;
import com.m2.tur.factory.TouristPointFactory;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.mapper.CommentMapper;
import com.m2.tur.model.dto.request.CommentRequest;
import com.m2.tur.model.dto.response.CommentResponse;
import com.m2.tur.model.entity.Comment;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.repository.CommentRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private TouristPointRepository touristPointRepository;

    @InjectMocks
    private CommentService commentService;

    @Captor
    private ArgumentCaptor<Comment> captor;

    @Nested
    class FindAllComments {
        @Test
        void should_return_all_comments_success() {
            //Arrange
            UUID touristPointId = UUID.randomUUID();
            Comment comment = CommentFactory.createEntity();
            CommentResponse response = CommentFactory.createResponse();

            when(commentRepository.findAllByTouristPointId(touristPointId)).thenReturn(List.of(comment));
            when(commentMapper.toResponse(comment)).thenReturn(response);

            //Act & Assert
            var result = assertDoesNotThrow(() -> commentService.findAllComments(touristPointId));

            assertNotNull(result);
            assertInstanceOf(CommentResponse.class, result.get(0));
        }
    }

    @Nested
    class Save {
        @Test
        void should_save_comment_success() {
            //Arrange
            UUID touristPointId = UUID.randomUUID();
            CommentRequest request =  CommentFactory.createRequest();
            Comment entity =  CommentFactory.createEntity();
            TouristPoint touristPoint = TouristPointFactory.createEntity();

            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.of(touristPoint));
            when(commentMapper.toEntity(request)).thenReturn(entity);
            when(commentRepository.save(entity)).thenReturn(entity);

            //Act & Assert
            assertDoesNotThrow(() -> commentService.save(touristPointId, request));

            verify(commentMapper).toEntity(any(CommentRequest.class));
            verify(commentRepository).save(captor.capture());

            var captured = captor.getValue();
            assertEquals(request.authorName(), captured.getAuthorName());
            assertEquals(touristPoint, captured.getTouristPoint());
        }

        @Test
        void should_throw_not_found_exception_when_tourist_point_not_found() {
            //Arrange
            UUID touristPointId = UUID.randomUUID();
            CommentRequest request =  CommentFactory.createRequest();

            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.empty());

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> commentService.save(touristPointId, request));

            verify(commentRepository, times(0)).save(any(Comment.class));

            assertEquals("Tourist Point Not Found.",  result.getMessage());
        }
    }
}
