package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //идентификатор бронирования
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker; //пользователь забронировавший вещь
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start; //дата и время начала брони
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end; // дата и время окончания брони
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item; //вещь, которую забронировали
    @Enumerated(EnumType.STRING)
    private Status status;

    public Booking(LocalDateTime start, LocalDateTime end, Item item, User booker) {
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
    }
}
