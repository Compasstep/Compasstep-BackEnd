package com.fifo.compasstep.admin.domain;

import com.fifo.compasstep.admin.enums.Role;
import com.fifo.compasstep.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.UUID;


@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Admin extends BaseEntity {

    private String name;
    private String email;
    private String password;

    @Column(name = "temp_pwd")
    @ColumnDefault("false")
    private boolean tempPwd;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "is_deleted")
    @ColumnDefault("false")
    private boolean isDeleted;


    public Admin(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password; // 비밀번호 암호화는 서비스 계층에서
        this.role = role;
        this.tempPwd = false;
        this.isDeleted = false;
    }

    public void anonymize() {
        // 개인 식별 정보를 정해진 더미 값으로 변경
        this.name = "탈퇴된 사용자" + this.name;
        this.email = "탈퇴된 사용자" + this.email;
        // 로그인 방지를 위해 비밀번호는 사용 불가능한 임의의 값으로 설정
        this.password = UUID.randomUUID().toString();
        this.role = Role.DELETED;
        this.isDeleted = true;
        this.tempPwd = false;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void setTempPwd(boolean tempPwd) {
        this.tempPwd = tempPwd;
    }
}
