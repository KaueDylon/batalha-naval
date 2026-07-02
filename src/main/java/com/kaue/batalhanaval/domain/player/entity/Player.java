package com.kaue.batalhanaval.domain.player.entity;

import com.kaue.batalhanaval.commons.enums.Nation;
import com.kaue.batalhanaval.commons.enums.NationPortrait;
import com.kaue.batalhanaval.commons.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_player", nullable = false)
    private Role rolePlayer;

    @Enumerated(EnumType.STRING)
    @Column(name = "nation")
    private Nation nation;

    @Enumerated(EnumType.STRING)
    @Column(name = "portrait")
    private NationPortrait portrait;

    @Column(nullable = false)
    private int wins = 0;

    @Column(nullable = false)
    private int losses = 0;

    public boolean hasNation() {
        return nation != null;
    }
}