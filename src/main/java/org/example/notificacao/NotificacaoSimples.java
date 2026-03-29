package org.example.notificacao;

import org.example.canal.NotificadorCanal;

public class NotificacaoSimples extends Notificacao {

    private final int prioridade;

    public NotificacaoSimples(String titulo, NotificadorCanal canal, int prioridade) {
        super(titulo, canal);
        if (prioridade < 1 || prioridade > 3) {
            throw new IllegalArgumentException("Prioridade deve ser entre 1 e 3");
        }
        this.prioridade = prioridade;
    }

    @Override
    public void enviar(String mensagem) {
        String texto = "[" + titulo + "] " + mensagem;
        canal.enviar(texto);
    }

    @Override
    public String getInfo() {
        return "NotificacaoSimples{titulo='" + titulo + "', prioridade=" + prioridade
                + ", canal=" + canal.getCanal() + "}";
    }

    public int getPrioridade() {
        return prioridade;
    }
}
