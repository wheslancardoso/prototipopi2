package com.teatro.observer;

import com.teatro.model.Ingresso;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestObserver implements Observer<NotificacaoVenda> {
    boolean notificado = false;
    NotificacaoVenda ultimaNotificacao;
    @Override
    public void update(NotificacaoVenda notificacao) {
        notificado = true;
        ultimaNotificacao = notificacao;
    }
}

public class VendaLoggerObserverTest {
    @Test
    void observerEhNotificadoAoVenderIngresso() {
        NotificacaoSubject subject = new NotificacaoSubject();
        TestObserver observer = new TestObserver();
        subject.registerObserver(observer);
        Ingresso ingresso = new Ingresso();
        ingresso.setEventoNome("Hamlet");
        ingresso.setHorario("Noite");
        ingresso.setAreaNome("Plateia A");
        ingresso.setNumeroPoltrona(10);
        ingresso.setValor(100.0);
        ingresso.setCodigo("ABC123");
        ingresso.setDataCompra(new java.sql.Timestamp(System.currentTimeMillis()));
        NotificacaoVenda notificacao = new NotificacaoVenda(ingresso);
        subject.notifyObservers(notificacao);
        assertTrue(observer.notificado);
        assertNotNull(observer.ultimaNotificacao);
        assertEquals("Hamlet", observer.ultimaNotificacao.getIngresso().getEventoNome());
    }
} 