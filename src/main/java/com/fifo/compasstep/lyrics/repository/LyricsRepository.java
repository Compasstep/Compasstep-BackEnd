package com.fifo.compasstep.lyrics.repository;

import com.fifo.compasstep.lyrics.domain.Lyrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LyricsRepository extends JpaRepository<Lyrics, Integer> {
    //Lyrics save(Lyrics lyrics);
}
