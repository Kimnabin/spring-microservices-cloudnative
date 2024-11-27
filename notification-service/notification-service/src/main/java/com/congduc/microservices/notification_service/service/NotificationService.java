package com.congduc.microservices.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import com.congduc.microservices.order_service.event.OrderPlacedEvent;
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender javaMailSender;

    @KafkaListener(topics = "order-placed")
    public void listen(OrderPlacedEvent orderPlacedEvent){
        log.info("Got Message from order-placed topic {}", orderPlacedEvent);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("springshop@email.com");
            messageHelper.setTo(orderPlacedEvent.getEmail().toString());
            messageHelper.setSubject(String.format("고객님 주문번호 %s 접수완료했습니다.", orderPlacedEvent.getOrderNumber()));
            messageHelper.setText(String.format("""
                             주문 완료 확인 메일 
                            
                            안녕하세요, %s, %s님. 
                            
                            [웹사이트 이름]을 이용해 주셔서 감사합니다! 고객님의 주문이 성공적으로 접수되었음을 알려드립니다. 
                            
                            예상 배송일: 2-3일 이내
                            주문 상태 확인 및 배송 정보 업데이트는 아래 링크를 통해 가능합니다: [주문 조회 링크] 
                            
                            추가 문의사항이 있거나 도움이 필요하시면 언제든지 아래 연락처로 문의해 주세요. 
                            - 이메일: ducvucong01@gmail.com 
                            - 고객센터: 010-7713-6899
                            
                            다시 한번 CONGDUC-SHOP을 선택해 주셔서 감사드립니다. 고객님께 최고의 제품과 서비스를 제공하기 위해 항상 노력하겠습니다. 
                            
                            감사합니다. 
                            """,
                    orderPlacedEvent.getFirstName().toString(),
                    orderPlacedEvent.getLastName().toString(),
                    orderPlacedEvent.getOrderNumber()));
        };
        try {
            javaMailSender.send(messagePreparator);
            log.info("Order Notifcation email sent!!");
        } catch (MailException e) {
            log.error("Exception occurred when sending mail", e);
            throw new RuntimeException("Exception occurred when sending mail to springshop@email.com", e);
        }
    }
}