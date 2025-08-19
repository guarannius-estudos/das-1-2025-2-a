package br.univille.singleton;

public class Cliente {
    public static void main(String[] args) {
        var segredo = "Eu ainda gosto dela";
        var singleton = Singleton.getInstance();
        singleton.setSegredo(segredo);
        mostrarSegredo();
    }

    public static void mostrarSegredo() {
        System.out.println(Singleton.getInstance().getSegredo());
    }
}
