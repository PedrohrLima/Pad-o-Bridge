package org.example.notificacao;

import org.example.canal.NotificadorCanal;

public abstract class Notificacao {

    protected final NotificadorCanal canal;
    protected final String titulo;

    protected Notificacao(String titulo, NotificadorCanal canal) {
        if (canal == null) throw new IllegalArgumentException("Canal não pode ser nulo");
        if (titulo == null || titulo.isBlank()) throw new IllegalArgumentException("Título não pode ser vazio");
        this.titulo = titulo;
        this.canal = canal;
    }

    public abstract void enviar(String mensagem);

    public abstract String getInfo();

    public String getTitulo() {
        return titulo;
    }

    public NotificadorCanal getCanal() {
        return canal;
    }
}
