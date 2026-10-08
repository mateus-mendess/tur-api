package com.m2.tur.service;

import com.m2.tur.factory.CommentFactory;
import com.m2.tur.factory.TouristPointFactory;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.mapper.CommentMapper;
import com.m2.tur.dto.request.CommentRequest;
import com.m2.tur.dto.response.CommentResponse;
import com.m2.tur.model.entity.Comment;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.repository.CommentRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

    private CommentRequest commentRequest;
    private Comment comment;
    private CommentResponse commentResponse;
    private TouristPoint touristPoint;
    private UUID touristPointId;

    @BeforeEach
    void setUp() {
        commentRequest = CommentFactory.createRequest();
        comment = CommentFactory.createEntity();
        commentResponse = CommentFactory.createResponse();
        touristPoint = TouristPointFactory.createEntity();
        touristPointId = touristPoint.getId();
    }

    @Nested
    class FindAllComments {
        @Test
        void should_return_all_comments_success() {
            //Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Comment> page = new PageImpl<>(List.of(comment), pageable, 10);

            when(commentRepository.findAllByTouristPointId(any(UUID.class), any(Pageable.class))).thenReturn(page);
            when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

            //Act & Assert
            var result = commentService.findAllComments(touristPointId, pageable);

            assertNotNull(result);
        }
    }

    @Nested
    class Save {
        @Test
        void should_save_comment_success() {
            //Arrange
            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.of(touristPoint));
            when(commentMapper.toEntity(commentRequest)).thenReturn(comment);

            //Act & Assert
            commentService.save(touristPointId, commentRequest);

            verify(commentRepository).save(captor.capture());

            var captured = captor.getValue();

            assertNotNull(captured.getTouristPoint());
        }

        @Test
        void should_throw_not_found_exception_when_tourist_point_not_found() {
            //Arrange
            when(touristPointRepository.findById(touristPointId)).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(NotFoundException.class, () -> commentService.save(touristPointId, commentRequest));

            verify(commentRepository, times(0)).save(any(Comment.class));
        }
    }
}
