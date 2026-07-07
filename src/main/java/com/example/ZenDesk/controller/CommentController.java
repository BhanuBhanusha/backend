package com.example.ZenDesk.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ZenDesk.dto.CommentRequestDto;
import com.example.ZenDesk.dto.CommentResponseDto;
import com.example.ZenDesk.service.CommentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class CommentController {
    
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{ticketId}/comments")
    public ResponseEntity<CommentResponseDto> addComments(@PathVariable Long ticketId,@Valid @RequestBody CommentRequestDto dto)
    {
        return ResponseEntity.status(201).body(commentService.addComment(ticketId,dto));
    }

    @GetMapping("/{ticketId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long ticketId)
    {
        return ResponseEntity.ok(commentService.getComments(ticketId));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId)
    {
        commentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
