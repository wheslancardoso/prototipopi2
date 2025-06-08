package com.teatro.observer;

public class VendaLoggerObserver implements Observer<NotificacaoVenda> {
    @Override
    public void update(NotificacaoVenda notificacao) {
        System.out.println("[LOG - VENDA] " + notificacao.getMensagem());
    }
} 