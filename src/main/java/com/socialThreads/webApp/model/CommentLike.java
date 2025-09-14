package com.socialThreads.webApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment_like")
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID commentLikeId;

    private UUID commentId;
    private UUID postId;
    private UUID userId;
    private LocalDateTime createdAt;
}
