package com.goose.cinema.persistance;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(name = "order_time", nullable = false)
    private LocalDate orderTime;

    @Column(name = "participants", nullable = false)
    private Integer participants;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Order o1 = (Order) o;
        return id != null && id.equals(o1.getId());
    }

    @Override
    public int hashCode() { return getId().hashCode(); }
}
