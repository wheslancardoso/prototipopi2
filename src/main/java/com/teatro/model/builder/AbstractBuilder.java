package com.teatro.model.builder;

/**
 * Classe base abstrata para implementação do padrão Builder.
 * @param <T> O tipo do objeto a ser construído
 * @param <B> O tipo do builder (para permitir method chaining)
 */
public abstract class AbstractBuilder<T, B extends AbstractBuilder<T, B>> {
    protected T objeto;
    
    /**
     * Retorna o objeto sendo construído.
     * @return O objeto sendo construído
     */
    protected abstract T getObjeto();
    
    /**
     * Retorna a instância do builder para permitir method chaining.
     * @return A instância do builder
     */
    protected abstract B self();
    
    /**
     * Valida o estado do objeto antes de construí-lo.
     * @throws IllegalArgumentException se o objeto estiver em um estado inválido
     */
    protected abstract void validar();
    
    /**
     * Constrói o objeto, validando seu estado antes.
     * @return O objeto construído
     * @throws IllegalArgumentException se o objeto estiver em um estado inválido
     */
    public T build() {
        validar();
        return getObjeto();
    }
} 