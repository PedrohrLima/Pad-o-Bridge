package org.example.canal;

import java.util.ArrayList;
import java.util.List;

public class EmailNotificador implements NotificadorCanal {

    private final String email;
    private final List<String> mensagensEnviadas = new ArrayList<>();

    public EmailNotificador(String email) {
        this.email = email;
    }

    @Override
    public void enviar(String mensagem) {
        String registro = "[EMAIL → " + email + "] " + mensagem;
        mensagensEnviadas.add(registro);
        System.out.println(registro);
    }

    @Override
    public String getCanal() {
        return "Email";
    }

    public List<String> getMensagensEnviadas() {
        return mensagensEnviadas;
    }

    public String getEmail() {
        return email;
    }
}

