package ru.practicum.shareit.item;


import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;

import java.util.List;

public interface ItemService {
    ItemWithBookingsDto addItem(ItemDto itemDto, Long userId);

    ItemWithBookingsDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemWithBookingsDto getItemById(Long itemId);

    List<ItemWithBookingsDto> getUserItems(Long userId);

    List<ItemWithCommentsDto> searchItems(String text);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);

}
