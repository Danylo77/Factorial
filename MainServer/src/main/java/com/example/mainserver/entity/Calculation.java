package com.example.mainserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;
@Getter

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "calculations")
public class Calculation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int number;
    private String result;

    @Column(name = "calculation_time")
    private LocalDateTime time;

    @ManyToOne // Багато до одного, оскільки кожен Calculation належить одному користувачеві
    @JoinColumn(name = "user_id", nullable = false) // Ім'я стовпця в базі даних, що посилається на користувача
    private User user; // Додано поле для зберігання користувача

    private int port;
    private Long idResult;
    public int getPort() {
        return port;
    }

    public void setPort(int port){
        this.port = port;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
}
