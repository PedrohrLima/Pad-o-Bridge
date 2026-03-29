package org.example.canal;

import java.util.ArrayList;
import java.util.List;

public class SMSNotificador implements NotificadorCanal {

    private final String telefone;
    private final List<String> mensagensEnviadas = new ArrayList<>();

    public SMSNotificador(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public void enviar(String mensagem) {
        String truncada = mensagem.length() > 160 ? mensagem.substring(0, 157) + "..." : mensagem;
        String registro = "[SMS → " + telefone + "] " + truncada;
        mensagensEnviadas.add(registro);
        System.out.println(registro);
    }

    @Override
    public String getCanal() {
        return "SMS";
    }

    public List<String> getMensagensEnviadas() {
        return mensagensEnviadas;
    }

    public String getTelefone() {
        return telefone;
    }
}
