package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(of = "id")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String description; //описание запроса
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requester; //пользователь создавший запрос
    private LocalDateTime created; //дата и время создания запроса

    public ItemRequest(String description, User requester, LocalDateTime created) {
        this.description = description;
        this.requester = requester;
        this.created = created;
    }
}
