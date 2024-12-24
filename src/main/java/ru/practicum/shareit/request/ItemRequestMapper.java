package ru.practicum.shareit.request;


import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collections;

public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest itemRequest) {

        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getId(),
                itemRequest.getRequestor().getName(),
                itemRequest.getCreated().toString(),
                Collections.emptyList()
        );
    }

    public static ItemRequest toEntity(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                null,
                null
        );
    }
}
