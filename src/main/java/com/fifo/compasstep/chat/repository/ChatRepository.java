package com.fifo.compasstep.chat.repository;

import com.fifo.compasstep.chat.domain.Chat;
import com.fifo.compasstep.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    List<Chat> findTop20ByUserOrderByCreatedAtDesc(User user);
}
