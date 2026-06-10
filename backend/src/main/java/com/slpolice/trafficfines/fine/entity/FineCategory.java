package com.slpolice.trafficfines.fine.entity;

import com.slpolice.trafficfines.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a category of traffic fine (e.g., SPD01 = speeding, SIG01 = signal violation).
 * Maps to the FINE_CATEGORIES table as defined in the ER diagram.
 *
 * Key design decision: category_code is a unique business identifier printed on the fine sheet.
 * Fine amounts are fixed per category (as per the implementation plan).
 */
@Entity
@Table(name = "fine_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FineCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique business code printed on the physical fine sheet.
     * e.g., SPD01 (speeding), SIG01 (signal violation)
     */
    @Column(name = "category_code", nullable = false, unique = true, length = 20)
    private String categoryCode;

    @Column(name = "description", nullable = false)
    private String description;

    /**
     * Fixed fine amount for this category (in LKR).
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Soft-delete flag — as per implementation plan's Key Design Decisions:
     * "Use is_active flags rather than hard deletes."
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // Bidirectional mapping back to fines (for JPA navigation only)
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Fine> fines;
}
