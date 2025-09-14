package com.socialThreads.webApp.model;

import com.socialThreads.webApp.model.enums.PostType;
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
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID postId;

    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String mediaContentUrl;
    private String textContent;

    @Enumerated(EnumType.STRING)
    private PostType postType;
}
