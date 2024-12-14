package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull(message = "Дата начала не может быть null")
    @FutureOrPresent(message = "Дата начала должна быть в будущем")
    private LocalDateTime start;
    @NotNull(message = "Дата окончания не может быть null")
    @FutureOrPresent(message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;
    @NotNull(message = "ID вещи не может быть null")
    private Long itemId;
    private Long bookerId;
    private BookingStatus status;

    private Item item;
    private User booker;
}
