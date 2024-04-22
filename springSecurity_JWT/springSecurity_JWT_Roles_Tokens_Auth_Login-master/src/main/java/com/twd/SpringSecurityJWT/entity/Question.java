package com.twd.SpringSecurityJWT.entity;
import jakarta.persistence.*;
import lombok.*;
@Data
@Entity
@Table(name = "Question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String text;

    // Define Many-to-One relationship with Sondage
    @ManyToOne
    @JoinColumn(name = "sondage_id")
    private Sondage sondage;
}
