package com.example.demo.service.impl;

import com.example.demo.dto.request.CommentRequest;
import com.example.demo.dto.response.CommentResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.constant.NotificationRefType;
import com.example.demo.constant.NotificationType;
import com.example.demo.entity.authAndUser.User;
import com.example.demo.entity.gradeAndEvaluate.Comment;
import com.example.demo.entity.gradeAndEvaluate.ExamQuestion;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.ExamQuestionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.k1.CommentService;
import com.example.demo.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        return toResponse(comment);
    }

    @Override
    public PageResponse<CommentResponse> getListComments(int page, int size, Long questionId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Comment> pageResult;
        if (questionId != null) {
            if (!examQuestionRepository.existsById(questionId)) {
                throw new AppException(ErrorCode.IMFORMATION_NULL);
            }
            pageResult = commentRepository.findByQuestion_Id(questionId, pageable);
        } else {
            pageResult = commentRepository.findAll(pageable);
        }

        List<CommentResponse> data = pageResult.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                data,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @Override
    public CommentResponse addComment(CommentRequest request) {
        if (request == null
                || request.getQuestionId() == null
                || request.getContent() == null
                || request.getUserId() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        ExamQuestion question = examQuestionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));

        Comment comment = new Comment();
        comment.setQuestion(question);
        comment.setContent(request.getContent());
        comment.setUserId(request.getUserId());
        comment.setParentId(request.getParentId());

        Comment saved = commentRepository.save(comment);
        notifyExamCreatorIfStudentComment(saved, question, request.getUserId());
        notifyParentCommentIfReply(saved, request.getUserId());
        return toResponse(saved);
    }

    @Override
    public CommentResponse editComment(Long id, CommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        if (request != null) {
            if (request.getQuestionId() != null) {
                ExamQuestion question = examQuestionRepository.findById(request.getQuestionId())
                        .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
                comment.setQuestion(question);
            }
            if (request.getContent() != null) {
                comment.setContent(request.getContent());
            }
            if (request.getUserId() != null) {
                comment.setUserId(request.getUserId());
            }
            if (request.getParentId() != null) {
                comment.setParentId(request.getParentId());
            }
        }

        Comment saved = commentRepository.save(comment);
        return toResponse(saved);
    }

    @Override
    public String deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        commentRepository.delete(comment);
        return "Delete successful!";
    }

    private CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUserId())
                .questionId(comment.getQuestion() != null ? comment.getQuestion().getId() : null)
                .parentId(comment.getParentId())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private void notifyExamCreatorIfStudentComment(Comment saved, ExamQuestion question, Long commenterId) {
        if (commenterId == null) {
            return;
        }
        User commenter = userRepository.findById(commenterId).orElse(null);
        if (commenter == null) {
            return;
        }
        boolean isStudent = commenter.getRoles().stream()
                .anyMatch(role -> "STUDENT".equals(role.getName()));
        if (!isStudent) {
            return;
        }
        if (question.getExam() == null || question.getExam().getCreatedBy() == null) {
            return;
        }
        Long creatorId = question.getExam().getCreatedBy().getId();
        if (creatorId == null || creatorId.equals(commenterId)) {
            return;
        }

        String title = "Binh luan moi trong bai kiem tra";
        String content = "Hoc vien vua binh luan trong bai kiem tra cua ban.";
        notificationService.notifyUsers(
                title,
                content,
                NotificationType.QUESTION_COMMENT,
                NotificationRefType.COMMENT,
                saved.getId(),
                List.of(creatorId)
        );
    }

    private void notifyParentCommentIfReply(Comment saved, Long commenterId) {
        if (saved == null || saved.getParentId() == null || commenterId == null) {
            return;
        }
        Comment parent = commentRepository.findById(saved.getParentId()).orElse(null);
        if (parent == null) {
            return;
        }
        Long parentUserId = parent.getUserId();
        if (parentUserId == null || parentUserId.equals(commenterId)) {
            return;
        }
        String title = "Tra loi binh luan moi";
        String content = "Ban co mot tra loi moi trong binh luan.";
        notificationService.notifyUsers(
                title,
                content,
                NotificationType.COMMENT_REPLY,
                NotificationRefType.COMMENT,
                saved.getId(),
                List.of(parentUserId)
        );
    }
}
