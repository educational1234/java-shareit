package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        ItemRequest itemRequest = new ItemRequest(
                null,
                itemRequestDto.getDescription(),
                requestor,
                LocalDateTime.now()
        );
        return ItemRequestMapper.toDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        return requestRepository.findAllByRequestorId(userId).stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getOtherUsersRequests(Long userId) {
        return requestRepository.findOtherUsersRequests(userId).stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Запрос не найден"));
        return ItemRequestMapper.toDto(request);
    }
}
