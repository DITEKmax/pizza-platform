package edu.rutmiit.storage;

import edu.rutmiit.dto.enums.OrderStatus;
import edu.rutmiit.dto.response.MenuItemResponse;
import edu.rutmiit.dto.response.OrderResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {
    public final Map<Long, MenuItemResponse> menu = new ConcurrentHashMap<>();
    public final Map<Long, OrderResponse> order = new ConcurrentHashMap<>();

    public final AtomicLong menuSequence = new AtomicLong(0);
    public final AtomicLong orderSequence = new AtomicLong(0);


    @PostConstruct
    public void init() {
        menuInit();
        orderInit();
    }

    private void menuInit() {
        addMenuItem("Маргарита",
                "Томатный соус, моцарелла, базилик", "645.00");

        addMenuItem("Пепперони",
                "Томатный соус, моцарелла, пепперони", "720.00");

        addMenuItem("Четыре сыра",
                "Моцарелла, горгонзола, пармезан, эмменталь", "850.00");

        addMenuItem("Гавайская",
                "Томатный соус, моцарелла, ветчина, ананас", "690.00");

        addMenuItem("Карбонара",
                "Сливочный соус, моцарелла, бекон, яйцо, пармезан", "780.00");

        addMenuItem("Диабло",
                "Томатный соус, моцарелла, салями, халапеньо, перец чили", "760.00");

        addMenuItem("Грибная",
                "Сливочный соус, моцарелла, шампиньоны, лук", "650.00");

        addMenuItem("Мясная",
                "Томатный соус, моцарелла, бекон, ветчина, говядина, курица", "890.00");

        addMenuItem("Маринара",
                "Томатный соус, чеснок, орегано, оливковое масло", "540.00");

        addMenuItem("Капричоза",
                "Томатный соус, моцарелла, ветчина, грибы, артишоки, оливки", "810.00");

        addMenuItem("Вегетарианская",
                "Томатный соус, моцарелла, перец, томаты, лук, оливки", "700.00");

        addMenuItem("Барбекю",
                "Соус барбекю, моцарелла, курица, бекон, лук", "820.00");

        addMenuItem("Песто",
                "Соус песто, моцарелла, томаты черри, руккола", "750.00");

        addMenuItem("Морская",
                "Томатный соус, моцарелла, креветки, мидии, кальмары", "950.00");

        addMenuItem("Цыпленок ранч",
                "Соус ранч, моцарелла, курица, бекон, томаты", "800.00");
    }

    private void orderInit() {
        addOrder(42L, OrderStatus.CREATED, "ул. Пушкина, д. 10",
                List.of(item(1L, 2), item(3L, 1)),
                "2140.00", "+79000000001", Instant.now(), null);

        addOrder(17L, OrderStatus.COOKING, "Невский проспект, д. 25",
                List.of(item(2L, 1), item(5L, 1)),
                "1500.00", "+79000000002", Instant.now(), Instant.now());

        addOrder(31L, OrderStatus.READY, "ул. Ленина, д. 8",
                List.of(item(4L, 2), item(7L, 1)),
                "2030.00", "+79000000003", Instant.now(), Instant.now());

        addOrder(56L, OrderStatus.DELIVERING, "ул. Гагарина, д. 14",
                List.of(item(8L, 1), item(12L, 1), item(13L, 1)),
                "2460.00", "+79000000004", Instant.now(), Instant.now());

        addOrder(73L, OrderStatus.DELIVERED, "ул. Советская, д. 3",
                List.of(item(14L, 2), item(9L, 1)),
                "2440.00", "+79000000005", Instant.now(), Instant.now());

        addOrder(88L, OrderStatus.CANCELLED, "ул. Мира, д. 45",
                List.of(item(6L, 1), item(15L, 2)),
                "2360.00", "+79000000006", Instant.now(), Instant.now());

        addOrder(12L, OrderStatus.CREATED, "ул. Чехова, д. 6",
                List.of(item(10L, 1), item(11L, 1)),
                "1510.00", "+79000000007", Instant.now(), null);

        addOrder(29L, OrderStatus.COOKING, "ул. Тверская, д. 19",
                List.of(item(1L, 1), item(2L, 2), item(13L, 1)),
                "2835.00", "+79000000008", Instant.now(), Instant.now());

        addOrder(64L, OrderStatus.READY, "ул. Садовая, д. 11",
                List.of(item(5L, 2), item(9L, 1)),
                "2100.00", "+79000000009", Instant.now(), Instant.now());

        addOrder(91L, OrderStatus.DELIVERING, "ул. Кирова, д. 22",
                List.of(item(3L, 1), item(6L, 1), item(14L, 1)),
                "2560.00", "+79000000010", Instant.now(), Instant.now());

        addOrder(105L, OrderStatus.DELIVERED, "ул. Баумана, д. 7",
                List.of(item(12L, 2), item(4L, 1)),
                "2330.00", "+79000000011", Instant.now(), Instant.now());

        addOrder(118L, OrderStatus.CANCELLED, "ул. Фрунзе, д. 30",
                List.of(item(7L, 3)),
                "1950.00", "+79000000012", Instant.now(), Instant.now());

        addOrder(127L, OrderStatus.CREATED, "ул. Лесная, д. 4",
                List.of(item(15L, 1), item(11L, 2)),
                "2200.00", "+79000000013", Instant.now(), null);

        addOrder(136L, OrderStatus.COOKING, "ул. Победы, д. 18",
                List.of(item(8L, 2), item(10L, 1)),
                "2590.00", "+79000000014", Instant.now(), Instant.now());

        addOrder(149L, OrderStatus.READY, "ул. Московская, д. 2",
                List.of(item(9L, 2), item(13L, 1), item(1L, 1)),
                "2475.00", "+79000000015", Instant.now(), Instant.now());

        addOrder(153L, OrderStatus.DELIVERING, "пр-т Мира, д. 61",
                List.of(item(14L, 1), item(6L, 2)),
                "2470.00", "+79000000016", Instant.now(), Instant.now());

        addOrder(168L, OrderStatus.DELIVERED, "ул. Молодежная, д. 9",
                List.of(item(2L, 1), item(4L, 1), item(5L, 1), item(7L, 1)),
                "2840.00", "+79000000017", Instant.now(), Instant.now());

        addOrder(174L, OrderStatus.CANCELLED, "ул. Центральная, д. 16",
                List.of(item(11L, 1), item(12L, 1), item(15L, 1)),
                "2320.00", "+79000000018", Instant.now(), Instant.now());

        addOrder(189L, OrderStatus.CREATED, "ул. Рабочая, д. 13",
                List.of(item(3L, 2), item(10L, 1), item(14L, 1)),
                "3460.00", "+79000000019", Instant.now(), null);

        addOrder(200L, OrderStatus.DELIVERED, "ул. Академическая, д. 5",
                List.of(item(1L, 1), item(6L, 1), item(8L, 1), item(13L, 2)),
                "3795.00", "+79000000020", Instant.now(), Instant.now());
    }

    private OrderResponse.OrderItemResponse item(Long pizzaId, int quantity) {
        MenuItemResponse pizza = menu.get(pizzaId);
        return OrderResponse.OrderItemResponse.builder()
                .pizzaId(pizza.getId())
                .pizzaName(pizza.getName())
                .quantity(quantity)
                .unitPrice(pizza.getPrice())
                .build();
    }

    private void addMenuItem(String name, String description, String price) {
        MenuItemResponse item = MenuItemResponse.builder()
                .id(menuSequence.incrementAndGet())
                .name(name)
                .description(description)
                .price(new BigDecimal(price))
                .available(true)
                .build();

        this.menu.put(item.getId(), item);
    }

    private void addOrder(Long customerId, OrderStatus orderStatus, String deliveryAddress,
                          List<OrderResponse.OrderItemResponse> items, String totalPrice,
                          String phoneNumber,
                          Instant createdAt, Instant updatedAt) {

        OrderResponse order = OrderResponse.builder()
                .id(orderSequence.incrementAndGet())
                .customerId(customerId)
                .status(orderStatus)
                .deliveryAddress(deliveryAddress)
                .items(items)
                .totalPrice(new BigDecimal(totalPrice))
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .phoneNumber(phoneNumber)
                .build();

        this.order.put(order.getId(), order);
    }
}
