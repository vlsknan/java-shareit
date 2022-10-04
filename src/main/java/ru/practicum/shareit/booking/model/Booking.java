package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Entity
@Table(name = "bookings", schema = "public")
@Data
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //идентификатор бронирования
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker; //пользователь забронировавший вещь
    @Column(name = "start_date", nullable = false)
    @NotNull
    private LocalDateTime start; //дата и время начала брони
    @Column(name = "end_date", nullable = false)
    @NotNull
    private LocalDateTime end; // дата и время окончания брони
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item; //вещь, которую забронировали
    @Enumerated(EnumType.STRING)
    @NotNull
    private Status status;

    public Booking(LocalDateTime start, LocalDateTime end, Item item, User booker) {
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
    }
}
