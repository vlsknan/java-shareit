package ru.practicum.shareit.request.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "requests", schema = "public")
@Data
@EqualsAndHashCode(of = "id")
public class ItemRequest {
    @Id
    private int id;
    @NotNull
    private String description; //описание запроса
    @NotNull
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor; //пользователь создавший запрос
    @NotNull
    private LocalDateTime created; //дата и время создания запроса
}
