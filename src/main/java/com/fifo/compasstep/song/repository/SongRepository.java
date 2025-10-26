package com.fifo.compasstep.song.repository;

import com.fifo.compasstep.song.domain.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Integer> {
    //Song save(Song song);

}
