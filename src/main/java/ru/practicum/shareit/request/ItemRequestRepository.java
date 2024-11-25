package ru.practicum.shareit.request;


import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ItemRequestRepository {
    private final Map<Long, ItemRequest> requests = new HashMap<>();
    private Long currentId = 0L;

    public ItemRequest save(ItemRequest request) {
        if (request.getId() == null) {
            request.setId(++currentId);
        }
        requests.put(request.getId(), request);
        return request;
    }

    public Optional<ItemRequest> findById(Long id) {
        return Optional.ofNullable(requests.get(id));
    }

    public List<ItemRequest> findAllByRequestorId(Long requestorId) {
        return requests.values().stream()
                .filter(request -> request.getRequestor().getId().equals(requestorId))
                .toList();
    }

    public List<ItemRequest> findAll() {
        return new ArrayList<>(requests.values());
    }
}
