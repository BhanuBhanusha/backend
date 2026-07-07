package com.example.ZenDesk.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ZenDesk.dto.CommentRequestDto;
import com.example.ZenDesk.dto.CommentResponseDto;
import com.example.ZenDesk.entity.AppUser;
import com.example.ZenDesk.entity.Role;
import com.example.ZenDesk.entity.Status;
import com.example.ZenDesk.entity.SupportTicket;
import com.example.ZenDesk.entity.TicketComment;
import com.example.ZenDesk.exception.BusinessValidationException;
import com.example.ZenDesk.exception.ResourceNotFoundException;
import com.example.ZenDesk.repository.SupportTicketRepository;
import com.example.ZenDesk.repository.TicketCommentRepository;


@Service
public class CommentService {
    
    private final SupportTicketRepository ticketRepo;
    private final TicketCommentRepository commentRepo;
    private final CurrentUserService currentUserService;

    public CommentService(TicketCommentRepository commentRepo, CurrentUserService currentUserService, SupportTicketRepository ticketRepo) {
        this.commentRepo = commentRepo;
        this.currentUserService = currentUserService;
        this.ticketRepo = ticketRepo;
    }

  
    
    private CommentResponseDto mapToDto(TicketComment comment)
    {
        CommentResponseDto response =new CommentResponseDto();
        response.setId(comment.getId());
        response.setTicketId(comment.getTicketId());
        response.setContent(comment.getContent());
        response.setIsInternal(comment.isIsInternal());
        response.setCreatedAt(comment.getCreatedAt());
        response.setAuthorName(comment.getAuthorName());
        response.setAuthorRole(comment.getAuthorRole());
        return response;
    }
    public CommentResponseDto addComment(Long ticketId,CommentRequestDto dto)
    {
        AppUser author = currentUserService.getCurrentUser();
        SupportTicket ticket = ticketRepo.findById(ticketId).orElseThrow(()-> new ResourceNotFoundException("Ticket not found"));
        if(ticket.getStatus() == Status.CLOSED)
        {
            throw new BusinessValidationException("cannot comments on a closed ticket");
        }
        if(author.getRole() == Role.CUSTOMER)
        {
            dto.setIsInternal(false);
        }
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepo.save(ticket);

        TicketComment comment = new TicketComment();
        comment.setTicketId(ticketId);
        comment.setAuthorId(author.getId());
        comment.setAuthorName(author.getFullName());
        comment.setAuthorRole(author.getRole());
        comment.setContent(dto.getContent());
        comment.setIsInternal(dto.isIsInternal());
        comment.setCreatedAt(LocalDateTime.now());
        commentRepo.save(comment);

        return mapToDto(comment);
    }

    public List<CommentResponseDto> getComments(Long ticketId)
    {
        AppUser viewer = currentUserService.getCurrentUser();
        List<TicketComment> comments; 

        if(viewer.getRole() == Role.CUSTOMER) { 
            comments = commentRepo.findByTicketIdAndIsInternalFalseOrderByCreatedAtAsc(ticketId);
        }
        else { 
            comments = commentRepo.findByTicketIdOrderByCreatedAtAsc(ticketId);
        }

        List<CommentResponseDto> list=new ArrayList<>();
        for (TicketComment comment: comments) 
        {
            list.add(mapToDto(comment));   
        }
        return list;
    }
       
    public void deleteComment(Long commentId) 
    {
        AppUser requestor = currentUserService.getCurrentUser();
        TicketComment comment = commentRepo.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        if(requestor.getRole() != Role.SUPERVISOR  && !comment.getAuthorId().equals(requestor.getId()))
        {
            throw new BusinessValidationException("You are not authorized");
        }
        commentRepo.delete(comment);
    }
    
}
