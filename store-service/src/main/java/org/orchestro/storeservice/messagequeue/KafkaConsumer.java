package org.orchestro.storeservice.messagequeue;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.orchestro.storeservice.dto.OrderReceiptFromStoreDto;
import org.orchestro.storeservice.dto.RequestedReceiptDto;
import org.orchestro.storeservice.jpa.CoffeeEntity;
import org.orchestro.storeservice.jpa.CoffeeRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final CoffeeRepository coffeeRepository;
    private final KafkaProducer kafkaProducer;

    // Coffee 주문 정보 받음
    @Transactional
    @KafkaListener(topics = "coffee-store-ordered-events")
    public void receive(ConsumerRecord<String, RequestedReceiptDto> consumerRecord) {
        log.info("[카프카 메시지 받음]");
        RequestedReceiptDto requestedReceiptDto = consumerRecord.value(); // 카프카로부터 받은 영수증
        CoffeeEntity coffeeEntity = coffeeRepository.findByCoffeeId(requestedReceiptDto.getCoffeeId())
                .orElseThrow(() -> new IllegalArgumentException("해당하는 커피 ID가 없습니다. ID : " + requestedReceiptDto.getCoffeeId()));
        log.info("coffeeEntity 수량 : {}", coffeeEntity.getStock());
        log.info("요청 수량 : {}", requestedReceiptDto.getQty());

        if (coffeeEntity.getStock() < requestedReceiptDto.getQty()) { // 요청 수량이 가진 수량보다 많을 때
            log.info("수량 부족으로 인한 주문 실패");

            OrderReceiptFromStoreDto orderReceiptFromStoreDto = OrderReceiptFromStoreDto.builder()
                    .orderId(requestedReceiptDto.getOrderId())
                    .orderMessage("수량 부족으로 인한 주문 실패")
                    .orderStatus("FAILED")
                    .coffeeId(requestedReceiptDto.getCoffeeId())
                    .coffeeName(requestedReceiptDto.getCoffeeName())
                    .qty(requestedReceiptDto.getQty())
                    .userId(requestedReceiptDto.getUserId())
                    .createdAt(requestedReceiptDto.getCreatedAt())
                    .build();

            kafkaProducer.send("coffee-store-ordered-update-events", orderReceiptFromStoreDto);
            log.info("[STORE-SERVICE => KAFKA] 실패1");
        }
        else if (coffeeRepository.findByCoffeeId(coffeeEntity.getCoffeeId()).isEmpty()) { // 해당하는 커피를 가게에서 없을 때
            log.info("메뉴 없음으로 인한 주문 실패");
            OrderReceiptFromStoreDto orderReceiptFromStoreDto = OrderReceiptFromStoreDto.builder()
                    .orderId(requestedReceiptDto.getOrderId())
                    .orderMessage("가게에 해당하는 메뉴 없어서 주문 실패")
                    .orderStatus("FAILED")
                    .coffeeId(requestedReceiptDto.getCoffeeId())
                    .coffeeName(requestedReceiptDto.getCoffeeName())
                    .qty(requestedReceiptDto.getQty())
                    .userId(requestedReceiptDto.getUserId())
                    .createdAt(requestedReceiptDto.getCreatedAt())
                    .build();
            kafkaProducer.send("coffee-store-ordered-update-events", orderReceiptFromStoreDto);
            log.info("[STORE-SERVICE => KAFKA] 실패2");
        }

        else if (coffeeEntity.getStock() >= requestedReceiptDto.getQty()) {
            log.info(String.format("[kafka Listener store-service] [%s] : 주문 성공", requestedReceiptDto.getOrderId()));
            coffeeEntity.setStock(coffeeEntity.getStock() - requestedReceiptDto.getQty());

            coffeeRepository.save(coffeeEntity);
            OrderReceiptFromStoreDto orderReceiptFromStoreDto = OrderReceiptFromStoreDto.builder()
                    .orderId(requestedReceiptDto.getOrderId())
                    .orderMessage("주문 성공")
                    .orderStatus("SUCCESS")
                    .coffeeId(requestedReceiptDto.getCoffeeId())
                    .coffeeName(requestedReceiptDto.getCoffeeName())
                    .qty(requestedReceiptDto.getQty())
                    .userId(requestedReceiptDto.getUserId())
                    .createdAt(requestedReceiptDto.getCreatedAt())
                    .build();
                kafkaProducer.send("coffee-store-ordered-update-events", orderReceiptFromStoreDto);
            log.info("[STORE-SERVICE => KAFKA] 성공");
        }
    }
}
