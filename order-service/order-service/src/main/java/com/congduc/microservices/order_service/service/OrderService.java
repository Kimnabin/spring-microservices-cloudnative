package com.congduc.microservices.order_service.service;

import com.congduc.microservices.order_service.client.InventoryClient;
import com.congduc.microservices.order_service.dto.OrderRequest;
import com.congduc.microservices.order_service.event.OrderEvent;
import com.congduc.microservices.order_service.event.OrderPlacedEvent;
import com.congduc.microservices.order_service.model.Order;
import com.congduc.microservices.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

//    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {

        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
        if (isProductInStock) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price().multiply(BigDecimal.valueOf(orderRequest.quantity())));
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());

            orderRepository.save(order);

            // Send OrderPlacedEvent to Kafka
//            OrderEvent orderEvent = new OrderEvent(order.getOrderNumber(), orderRequest.userDetails().email());
//            log.info("Start- Sending OrderPlacedEvent {} to Kafka Topic", orderEvent);
//            kafkaTemplate.send("order-placed", orderEvent);
//            log.info("End- Sending OrderPlacedEvent {} to Kafka Topic", orderEvent);

            // Send the message to Kafka Topic
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
            orderPlacedEvent.setOrderNumber(order.getOrderNumber());
            orderPlacedEvent.setEmail(orderRequest.userDetails().email());
            orderPlacedEvent.setFirstName(orderRequest.userDetails().firstName());
            orderPlacedEvent.setLastName(orderRequest.userDetails().lastName());
//            log.info("Start - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
            kafkaTemplate.send("order-placed", orderPlacedEvent);
//            log.info("End - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent );
        } else {
            throw new RuntimeException("Product with SkuCode " + orderRequest.skuCode() + " is not in stock");
        }
    }
}