package com.fifo.compasstep.chat.domain;

import com.fifo.compasstep.common.domain.BaseEntity;
import com.fifo.compasstep.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "chats")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat extends BaseEntity {
    //userPKId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(name = "is_guardrailed", nullable = false)
    private Boolean isGuardrailed;

    public static Chat create(User user, String content, Boolean isGuardrailed) {
        Chat chat = Chat.builder()
                .user(user)
                .content(content)
                .isGuardrailed(isGuardrailed)
                .build();

        user.getChatList().add(chat);
        return chat;
    }
}
