package io.sanwishe.wsdemo.configuration;

import io.sanwishe.wsdemo.handler.ChatRoomHandler;
import io.sanwishe.wsdemo.handler.EchoHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WSConfiguration implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new EchoHandler(), "echo").setAllowedOrigins("*");
        registry.addHandler(new ChatRoomHandler(), "chat").setAllowedOrigins("*");
    }
}
