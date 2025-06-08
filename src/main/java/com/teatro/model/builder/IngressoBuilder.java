package com.teatro.model.builder;

import com.teatro.model.Ingresso;
import com.teatro.util.TeatroLogger;
import com.teatro.util.Validator;
import java.sql.Timestamp;

/**
 * Builder para a classe Ingresso.
 */
public class IngressoBuilder extends AbstractBuilder<Ingresso, IngressoBuilder> {
    private final TeatroLogger logger = TeatroLogger.getInstance();
    private final Ingresso objeto;
    
    public IngressoBuilder() {
        this.objeto = new Ingresso();
        this.objeto.setDataCompra(new Timestamp(System.currentTimeMillis()));
    }
    
    public IngressoBuilder comId(Long id) {
        objeto.setId(id);
        return self();
    }
    
    public IngressoBuilder comUsuarioId(Long usuarioId) {
        objeto.setUsuarioId(usuarioId);
        return self();
    }
    
    public IngressoBuilder comSessaoId(Long sessaoId) {
        objeto.setSessaoId(sessaoId);
        return self();
    }
    
    public IngressoBuilder comAreaId(Long areaId) {
        objeto.setAreaId(areaId);
        return self();
    }
    
    public IngressoBuilder comNumeroPoltrona(int numeroPoltrona) {
        objeto.setNumeroPoltrona(numeroPoltrona);
        return self();
    }
    
    public IngressoBuilder comValor(double valor) {
        objeto.setValor(valor);
        return self();
    }
    
    public IngressoBuilder comDataCompra(Timestamp dataCompra) {
        objeto.setDataCompra(dataCompra);
        return self();
    }
    
    public IngressoBuilder comEventoNome(String eventoNome) {
        objeto.setEventoNome(eventoNome);
        return self();
    }
    
    public IngressoBuilder comHorario(String horario) {
        objeto.setHorario(horario);
        return self();
    }
    
    public IngressoBuilder comAreaNome(String areaNome) {
        objeto.setAreaNome(areaNome);
        return self();
    }
    
    public IngressoBuilder comCodigo(String codigo) {
        objeto.setCodigo(codigo);
        return self();
    }
    
    @Override
    protected Ingresso getObjeto() {
        return objeto;
    }
    
    @Override
    protected IngressoBuilder self() {
        return this;
    }
    
    @Override
    protected void validar() {
        Validator.validarNaoNulo(objeto.getUsuarioId(), "ID do usuário");
        Validator.validarNaoNulo(objeto.getSessaoId(), "ID da sessão");
        Validator.validarNaoNulo(objeto.getAreaId(), "ID da área");
        Validator.validarNumeroPositivo(objeto.getNumeroPoltrona(), "Número da poltrona");
        Validator.validarNumeroPositivo(objeto.getValor(), "Valor");
        Validator.validarNaoNulo(objeto.getDataCompra(), "Data da compra");
        Validator.validarStringNaoVazia(objeto.getEventoNome(), "Nome do evento");
        Validator.validarStringNaoVazia(objeto.getHorario(), "Horário");
        Validator.validarStringNaoVazia(objeto.getAreaNome(), "Nome da área");
        Validator.validarStringNaoVazia(objeto.getCodigo(), "Código do ingresso");
    }
} 