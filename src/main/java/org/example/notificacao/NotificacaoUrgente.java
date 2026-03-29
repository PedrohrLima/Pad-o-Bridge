package org.example.notificacao;

import org.example.canal.NotificadorCanal;

public class NotificacaoUrgente extends Notificacao{

    private final int repeticoes;

    public NotificacaoUrgente(String titulo, NotificadorCanal canal, int repeticoes) {
        super(titulo, canal);
        if (repeticoes < 1) {
            throw new IllegalArgumentException("Repetições devem ser pelo menos 1");
        }
        this.repeticoes = repeticoes;
    }

    @Override
    public void enviar(String mensagem) {
        String texto = "🚨 URGENTE [" + titulo + "] " + mensagem;
        for (int i = 0; i < repeticoes; i++) {
            canal.enviar(texto);
        }
    }

    @Override
    public String getInfo() {
        return "NotificacaoUrgente{titulo='" + titulo + "', repeticoes=" + repeticoes
                + ", canal=" + canal.getCanal() + "}";
    }

    public int getRepeticoes() {
        return repeticoes;
    }
}
