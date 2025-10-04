package com.fifo.compasstep.user.domain;

import com.fifo.compasstep.chat.domain.Chat;
import com.fifo.compasstep.common.domain.BaseEntity;
import com.fifo.compasstep.lyrics.domain.Lyrics;
import com.fifo.compasstep.reputationAnalysis.domain.ReputationAnalysis;
import com.fifo.compasstep.song.domain.Song;
import com.fifo.compasstep.user.enums.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users") // user로 설정하면 예약어와 충돌해 오류 발생, users로 변경
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert // null이 아닌 필드만으로 INSERT 쿼리를 생성
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'NORMAL'") // DB 레벨 기본값 설정
    private Status status = Status.NORMAL;

    @Column(nullable = true)
    private String statusReason;

    @Builder.Default
    @Column(name = "is_deleted") // DB 컬럼명은 is_deleted 유지
    @ColumnDefault("false")
    private boolean isDeleted = false; // Java에서는 camelCase와 primitive type 사용 - 자바에 맞게 변경

    @Builder.Default
    private String s3FileImage = "/image/baseImageLocation";

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Song> songs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Lyrics> lyricsList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReputationAnalysis> reputationAnalysisList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Chat> chatList = new ArrayList<>();

    public User(String name, String nickname, String email, String s3FileImage) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.s3FileImage = s3FileImage;
        // status, isDeleted 등은 DB 기본값이나 @ColumnDefault를 따름
        this.isDeleted = false;
    }
}