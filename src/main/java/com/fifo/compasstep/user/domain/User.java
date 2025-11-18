package com.fifo.compasstep.user.domain;

import com.fifo.compasstep.admin.enums.Role;
import com.fifo.compasstep.chat.domain.Chat;
import com.fifo.compasstep.common.domain.BaseEntity;
import com.fifo.compasstep.lyrics.domain.Lyrics;
import com.fifo.compasstep.reputationAnalysis.domain.ReputationAnalysis;
import com.fifo.compasstep.song.domain.Song;
import com.fifo.compasstep.user.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users") // user로 설정하면 예약어와 충돌해 오류 발생, users로 변경
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert // null이 아닌 필드만으로 INSERT 쿼리를 생성
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    @ColumnDefault("User")
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
    private String s3FileImage = "image/baseImageLocation.png";

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

    public void changeStatus(Status newStatus) {
        this.status = newStatus;
    }

    public void anonymize() {
        // 개인 식별 정보를 정해진 더미 값으로 변경
        this.name = "탈퇴된 사용자" + this.name;
        this.nickname = "탈퇴된 사용자" + this.nickname;

        // UUID를 사용하여 고유한 난수 문자열 생성 (로그인 방지 및 고유성 확보)
        String uniqueId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        this.email = "delete" + uniqueId + "@gmail.com";

        // 로그인 방지를 위해 비밀번호는 사용 불가능한 임의의 값으로 설정 (원래 로직에는 없지만 익명화 시 보안 강화 차원에서 필요)
        // this.password = "impossible_to_login_" + uniqueId;

        this.status = Status.DELETED;
        this.isDeleted = true;
    }
}