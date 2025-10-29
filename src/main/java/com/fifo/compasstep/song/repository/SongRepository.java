package com.fifo.compasstep.song.repository;

import com.fifo.compasstep.song.domain.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {
    //Song save(Song song);

    boolean existsByIdAndUser_Id(Long songId, Long userId);
}
