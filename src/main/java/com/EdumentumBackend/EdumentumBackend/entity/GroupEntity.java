package com.EdumentumBackend.EdumentumBackend.entity;

import com.EdumentumBackend.EdumentumBackend.entity.listener.GroupEntityListener;
import com.EdumentumBackend.EdumentumBackend.enums.GroupTier;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Entity
@EntityListeners(GroupEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "groups",
        indexes = {
                @Index(name = "idx_user_public_id", columnList = "publicId", unique = true)
        }
)
public class GroupEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String publicId;

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
}
