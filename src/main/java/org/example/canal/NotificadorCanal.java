package org.example.canal;

public interface NotificadorCanal {

    void enviar(String mensagem);

    String getCanal();
}