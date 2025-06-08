package com.teatro.observer;

/**
 * Interface que define o comportamento de um observador no padrão Observer.
 * @param <T> O tipo de dados que o observador receberá nas atualizações
 */
public interface Observer<T> {
    
    /**
     * Método chamado quando o observador é notificado de uma mudança.
     * @param data Os dados da atualização
     */
    void update(T data);
} 