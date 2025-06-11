package com.teatro.view.controllers;

import com.teatro.model.Area;
import com.teatro.model.Evento;
import com.teatro.model.Sessao;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompraIngressoController extends BaseController {

    @FXML private Label compra_lblUsuario;
    @FXML private Label compra_lblEventoNome;
    @FXML private Label compra_lblHorario;
    @FXML private ComboBox<Area> compra_cmbAreas;
    @FXML private VBox compra_containerInfoArea;
    @FXML private Label compra_lblPreco;
    @FXML private Label compra_lblDisponibilidade;
    @FXML private Label compra_lblErro;
    @FXML private Button compra_btnContinuar;

    private Sessao sessao;
    private Area areaSelecionada;
    private List<Area> areasDisponiveis;

    @Override
    protected void inicializarComponentes() {
        if (usuario != null) {
            compra_lblUsuario.setText(usuario.getNome());
        }
        
        // Ocultar inicialmente
        compra_containerInfoArea.setVisible(false);
        compra_containerInfoArea.setManaged(false);
        compra_lblErro.setVisible(false);
        compra_btnContinuar.setDisable(true);
        
        // Configurar ComboBox
        compra_cmbAreas.setConverter(new StringConverter<Area>() {
            @Override
            public String toString(Area area) {
                return area != null ? area.toString() : "";
            }

            @Override
            public Area fromString(String string) {
                return null;
            }
        });
    }

    @Override
    protected void configurarEventos() {
        compra_cmbAreas.setOnAction(e -> handleAreaSelecionada());
    }

    @Override
    protected void carregarDados() {
        // Receber sessão do SceneManager
        Object sessaoData = sceneManager.getUserData("sessao");
        if (sessaoData instanceof Sessao) {
            this.sessao = (Sessao) sessaoData;
            carregarInformacoesSessao();
            carregarAreasDisponiveis();
        } else {
            mostrarErro("Erro", "Sessão não encontrada");
            handleVoltar();
        }
    }

    private void carregarInformacoesSessao() {
        // Buscar o evento da sessão
        Evento evento = teatro.getEventos().stream()
            .filter(evt -> evt.getSessoes().contains(sessao))
            .findFirst()
            .orElse(null);

        if (evento != null) {
            compra_lblEventoNome.setText(evento.getNome());
        }
        
        compra_lblHorario.setText(sessao.getTipoSessao().getDescricao());
    }

    private void carregarAreasDisponiveis() {
        try {
            this.areasDisponiveis = teatro.getAreasDisponiveis(sessao);
            
            if (areasDisponiveis.isEmpty()) {
                mostrarErro("Atenção", "Não há áreas disponíveis para esta sessão.");
                handleVoltar();
                return;
            }
            
            // Ordenar áreas
            List<Area> areasOrdenadas = new ArrayList<>(areasDisponiveis);
            Collections.sort(areasOrdenadas);
            
            compra_cmbAreas.getItems().clear();
            compra_cmbAreas.getItems().addAll(areasOrdenadas);
            
        } catch (Exception e) {
            mostrarErro("Erro", "Erro ao carregar áreas disponíveis: " + e.getMessage());
            handleVoltar();
        }
    }

    @FXML
    private void handleAreaSelecionada() {
        areaSelecionada = compra_cmbAreas.getValue();
        
        if (areaSelecionada != null) {
            try {
                // Atualizar informações da área
                Area areaAtualizada = teatro.getAreaAtualizada(sessao.getId(), areaSelecionada.getId());
                
                if (areaAtualizada.getPoltronasDisponiveis() == 0) {
                    mostrarErro("Atenção", "Esta área não possui mais poltronas disponíveis.");
                    compra_cmbAreas.setValue(null);
                    areaSelecionada = null;
                    ocultarInfoArea();
                    return;
                }
                
                // Mostrar informações da área
                compra_lblPreco.setText(String.format("R$ %.2f", areaAtualizada.getPreco()));
                compra_lblDisponibilidade.setText(areaAtualizada.getPoltronasDisponiveis() + 
                                                 " de " + areaAtualizada.getCapacidadeTotal());
                
                mostrarInfoArea();
                compra_btnContinuar.setDisable(false);
                ocultarErro();
                
            } catch (Exception e) {
                mostrarError("Erro ao atualizar informações da área: " + e.getMessage());
                compra_cmbAreas.setValue(null);
                areaSelecionada = null;
                ocultarInfoArea();
            }
        } else {
            ocultarInfoArea();
        }
    }

    private void mostrarInfoArea() {
        compra_containerInfoArea.setVisible(true);
        compra_containerInfoArea.setManaged(true);
    }

    private void ocultarInfoArea() {
        compra_containerInfoArea.setVisible(false);
        compra_containerInfoArea.setManaged(false);
        compra_btnContinuar.setDisable(true);
    }

    private void mostrarError(String mensagem) {
        compra_lblErro.setText(mensagem);
        compra_lblErro.setVisible(true);
    }

    private void ocultarErro() {
        compra_lblErro.setVisible(false);
    }

    @FXML
    private void handleVoltar() {
        try {
            sceneManager.loadScene("/com/teatro/view/fxml/sessoes.fxml",
                                 "Sistema de Teatro - Sessões");
        } catch (Exception e) {
            mostrarErro("Erro", "Erro ao voltar para sessões: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        handleVoltar();
    }

    @FXML
    private void handleContinuar() {
        if (areaSelecionada != null) {
            // Passar dados para próxima tela
            sceneManager.setUserData("sessao", sessao);
            sceneManager.setUserData("area", areaSelecionada);
            
            try {
                sceneManager.loadScene("/com/teatro/view/fxml/selecionar-poltrona.fxml",
                                     "Sistema de Teatro - Seleção de Poltronas");
            } catch (Exception e) {
                mostrarErro("Erro", "Erro ao navegar para seleção de poltronas: " + e.getMessage());
            }
        } else {
            mostrarError("Por favor, selecione uma área.");
        }
    }
}
