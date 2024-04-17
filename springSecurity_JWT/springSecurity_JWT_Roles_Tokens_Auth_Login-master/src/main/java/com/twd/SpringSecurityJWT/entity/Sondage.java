package com.twd.SpringSecurityJWT.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "Sondage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sondage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSondage;
    private String title;
    private String description;
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Temporal(TemporalType.DATE)
    private Date endDate;
    private boolean isActive;
    @ManyToOne
    @JoinColumn(name = "created_by_id", referencedColumnName = "id")
    private Users createdBy;

    // Establishing many-to-many relationship with participants (users participating in the survey)
    @ManyToMany
    @JoinTable(
            name = "sondage_participants",
            joinColumns = @JoinColumn(name = "sondage_id_sondage"),
            inverseJoinColumns = @JoinColumn(name = "participants_id")
    )
    private Set<Users> participants;

}
