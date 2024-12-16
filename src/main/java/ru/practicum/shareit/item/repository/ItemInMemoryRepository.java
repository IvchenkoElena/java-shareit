//package ru.practicum.shareit.item.repository;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Repository;
//import ru.practicum.shareit.exception.NotFoundException;
//import ru.practicum.shareit.item.model.Item;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Repository
//public class ItemInMemoryRepository implements ItemRepository {
//    private final Map<Long, Item> items = new HashMap<>();
//    private long currentId = 1;
//
//    @Override
//    public Item createItem(Item newItem) {
//        newItem.setId(currentId);
//        items.put(currentId, newItem);
//        log.info("Новая вещь с ID {} добавлена в репозиторий", currentId);
//        currentId++;
//        return newItem;
//    }
//
//    @Override
//    public Item updateItem(long itemId, Item updatedItem) {
//        items.put(itemId, updatedItem);
//        log.info("Вещь с ID {} обновлена в репозитории", currentId);
//        return updatedItem;
//    }
//
//    @Override
//    public List<Item> getAllItemsByOwnerId(long ownerId) {
//        log.info("Выведен список вещей пользователя с ID {}", ownerId);
//        return items.values().stream().filter(i -> i.getOwner().getId() == ownerId).toList();
//    }
//
//    @Override
//    public Item getItemById(long itemId) {
//        if (!items.containsKey(itemId)) {
//            String message = "Вещь с ID " + itemId + " не обнаружена";
//            log.error(message);
//            throw new NotFoundException(message);
//        }
//        Item item = items.get(itemId);
//        log.info("Найдена вещь с ID {}", itemId);
//        return item;
//    }
//
//    @Override
//    public List<Item> searchItems(String text) {
//        List<Item> searchItemsList = items.values().stream()
//                .filter(i -> i.getName().toUpperCase().contains(text.toUpperCase()))
//                .filter(i -> i.getAvailable())
//                .toList();
//        log.info("Выведен результат поиска вещей");
//        return searchItemsList;
//    }
//}
