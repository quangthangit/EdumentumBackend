package com.EdumentumBackend.EdumentumBackend.entity;

import com.EdumentumBackend.EdumentumBackend.entity.listener.GroupEntityListener;
import com.EdumentumBackend.EdumentumBackend.enums.GroupTier;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(GroupEntityListener.class)
@Table(name = "groups")
public class GroupEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 6, max = 100)
    @Column(nullable = false)
    private String name;

    @Size(max = 255)
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean isPublic = true;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(name = "member_count", nullable = false)
    private int memberCount = 1;

    @Column(name = "member_limit")
    @Min(1)
    @Max(50)
    private int memberLimit;

    @Column(unique = true, nullable = false)
    private String key;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupTier tier = GroupTier.BRONZE;

    @Column(nullable = false)
    private int contributionPoints = 0;

    @Version
    private Integer version;
}
