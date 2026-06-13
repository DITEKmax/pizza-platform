package edu.rutmiit.service;

import edu.rutmiit.dto.request.MenuItemRequest;
import edu.rutmiit.dto.request.PatchMenuItemRequest;
import edu.rutmiit.dto.request.UpdateMenuItemRequest;
import edu.rutmiit.dto.response.MenuItemResponse;
import edu.rutmiit.dto.response.PagedResponse;
import edu.rutmiit.event.MenuItemEventPublisher;
import edu.rutmiit.exception.DublicateItemException;
import edu.rutmiit.exception.ResourceNotFoundException;
import edu.rutmiit.storage.InMemoryStorage;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class MenuService {

    private final InMemoryStorage storage;
    private final MenuItemEventPublisher eventPublisher;

    public MenuService(InMemoryStorage storage, MenuItemEventPublisher eventPublisher){
        this.storage = storage;
        this.eventPublisher = eventPublisher;
    }

    public MenuItemResponse findMenuItemById(Long id) {
        return Optional.ofNullable(storage.menu.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
    }

    public PagedResponse<MenuItemResponse> findAllMenuItems(Boolean availableOnly, String nameSearch, int page, int size){

        Stream<MenuItemResponse> stream = storage.menu.values().stream().sorted((i1, i2) -> i1.getId().compareTo(i2.getId()));

        if (availableOnly != null){
            stream = stream.filter(a -> a.getAvailable().equals(availableOnly));
        }
        if (nameSearch != null){
            String name = nameSearch.toLowerCase();
            stream = stream.filter(a -> a.getName() != null && a.getName().toLowerCase().contains(name));
        }

        List<MenuItemResponse> allItems = stream.toList();
        int totalElements = allItems.size();
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
        int from = page * size;
        int to = Math.min(totalElements, from + size);
        List<MenuItemResponse> content = (from >= totalElements) ? List.of() : allItems.subList(from, to);
        return new PagedResponse<>(content, page, size, totalElements, totalPages, page >= totalPages - 1);
    }


    public MenuItemResponse createMenuItem (MenuItemRequest request) {
        validateUnicMenuItem(null, request.name(), request.price());

        long id = storage.menuSequence.incrementAndGet();

        MenuItemResponse menuItem = MenuItemResponse.builder()
                .id(id)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .available(request.available())
                .build();

        storage.menu.put(id, menuItem);
        eventPublisher.publishCreated(menuItem);
        return menuItem;
    }

    public MenuItemResponse updateMenuItem (Long id, UpdateMenuItemRequest request){

        findMenuItemById(id);
        MenuItemResponse menuItem = MenuItemResponse.builder()
                .id(id)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .available(request.available())
                .build();

        storage.menu.put(id, menuItem);
        eventPublisher.publishUpdated(menuItem);
        return menuItem;

    }

    public MenuItemResponse patchMenuItem(Long id, PatchMenuItemRequest request){
        MenuItemResponse existing = findMenuItemById(id);

        MenuItemResponse updated = MenuItemResponse.builder()
                .id(id)
                .name(request.name() != null ? request.name() : existing.getName())
                .description(request.description() != null ? request.description() : existing.getDescription())
                .price(request.price() != null ? request.price() : existing.getPrice())
                .available(request.available() != null ? request.available() : existing.getAvailable())
                .build();

        storage.menu.put(id, updated);
        eventPublisher.publishUpdated(updated);
        return updated;
    }

    public void deleteMenuItem(Long id){
        MenuItemResponse menuItem = findMenuItemById(id);
        storage.menu.remove(id);
        eventPublisher.publishDeleted(menuItem);
    }

    private void validateUnicMenuItem (Long id, String name, BigDecimal price){
        storage.menu.values().stream()
                .filter(a -> a.getName().trim().equalsIgnoreCase(name.trim()))
                .filter(a -> a.getPrice().equals(price))
                .findAny()
                .ifPresent(a -> { throw new DublicateItemException(id);
                });
    }
}