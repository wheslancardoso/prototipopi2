package com.teatro.observer;

/**
 * Interface que define o comportamento de um sujeito no padrão Observer.
 * Um sujeito mantém uma lista de observadores e os notifica quando há mudanças.
 * 
 * @param <T> O tipo de dados que será notificado aos observadores
 */
public interface Subject<T> {
    /**
     * Registra um novo observador.
     * 
     * @param observer O observador a ser registrado
     */
    void registerObserver(Observer<T> observer);
    
    /**
     * Remove um observador registrado.
     * 
     * @param observer O observador a ser removido
     */
    void removeObserver(Observer<T> observer);
    
    /**
     * Notifica todos os observadores registrados com os dados fornecidos.
     * 
     * @param data Os dados a serem notificados
     */
    void notifyObservers(T data);
} 