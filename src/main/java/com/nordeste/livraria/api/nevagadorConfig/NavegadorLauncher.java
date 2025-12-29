package com.nordeste.livraria.api.nevagadorConfig;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NavegadorLauncher {

    @EventListener(ApplicationReadyEvent.class)
    public void abrirNavegador() {
        System.setProperty("java.awt.headless", "false");

        String url = "http://localhost:8080";
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
