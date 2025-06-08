package com.teatro.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação base abstrata do padrão Subject.
 * @param <T> O tipo de dados que será notificado aos observadores
 */
public abstract class AbstractSubject<T> implements Subject<T> {
    private final List<Observer<T>> observers = new ArrayList<>();
    
    @Override
    public void registerObserver(Observer<T> observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    @Override
    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers(T data) {
        for (Observer<T> observer : observers) {
            observer.update(data);
        }
    }
    
    /**
     * Retorna o número de observadores registrados.
     * @return O número de observadores
     */
    public int getObserverCount() {
        return observers.size();
    }
    
    /**
     * Remove todos os observadores registrados.
     */
    public void clearObservers() {
        observers.clear();
    }
} 