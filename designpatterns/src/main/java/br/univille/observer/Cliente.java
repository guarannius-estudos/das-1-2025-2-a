package br.univille.observer;

public class Cliente {
    public static void main(String[] args) {
        var grupo = new Publisher();
        var pessoa1 = new ConcreteSubscriber();
        var pessoa2 = new ConcreteSubscriber();
        var pessoa3 = new ConcreteSubscriber();

        grupo.assinar(pessoa1);
        grupo.assinar(pessoa2);
        grupo.assinar(pessoa3);

        grupo.setMensagem("Bom dia!");
    }
}
