package de.xstampp.service.push.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import de.xstampp.service.push.controller.PushWebsocketHandler;

@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer{

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(pushHandler(), "/api/push/ws").setAllowedOrigins("*");	
	}
	
	@Bean
	public WebSocketHandler pushHandler() {
		return new PushWebsocketHandler();
	}

}
