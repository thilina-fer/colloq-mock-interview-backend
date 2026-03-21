package lk.ijse.springbootbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Level {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long levelId;
    @Column(nullable = false, unique = true)
    private String name;
    private Integer sessionDuration; // minutes
    private Double price;
    private String status; // ACTIVE / INACTIVE
}