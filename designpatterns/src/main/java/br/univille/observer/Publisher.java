package br.univille.observer;

import java.util.ArrayList;

public class Publisher {
    private final ArrayList<Subscriber> assinantes = new ArrayList<>();
    private String mensagem;

    public void assinar(Subscriber assinante) {
        assinantes.add(assinante);
    }

    public void notificarAssinatura() {
        for (Subscriber assinante : assinantes) {
            assinante.update(mensagem);
        }
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
