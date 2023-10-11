package com.sample.controller;

import com.sample.controller.request.Message;
import com.sample.controller.response.OutputMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Slf4j
public class WebSocketTextController {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public OutputMessage send(Message message) throws Exception {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        OutputMessage outputMessage = new OutputMessage(message.getFrom(), message.getText(), time);
        log.info("Message={}", outputMessage.toString());
        return outputMessage;
    }

}
