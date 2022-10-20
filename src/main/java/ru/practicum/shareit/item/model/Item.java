package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "items", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;//статус вещи (true - доступна для аренды, false - не доступна для аренды)
    @Column(name = "owner_id")
    private int ownerId; //пользователь-владелец
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest; //ссылка на запрос (если вещь создана по запросу)

    public Item(int id, String name, String description, Boolean available, ItemRequest itemRequest) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.itemRequest = itemRequest;
    }
}
