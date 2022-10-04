package ru.practicum.shareit.item.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private int id;
    @NotBlank
    @Size(min = 1)
    private String text;
    private int itemId;
    private int authorId;
    private LocalDateTime created;
}
