package com.teatro.view.controllers;

import com.teatro.model.*;
import com.teatro.view.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class CompraIngressoController implements Initializable {

    @FXML private Label compra_lblUsuario;
    @FXML private Label compra_lblEventoNome;
    @FXML private Label compra_lblHorario;
    @FXML private ComboBox<Area> compra_cmbAreas;
    @FXML private VBox compra_containerInfoArea;
    @FXML private Label compra_lblPreco;
    @FXML private Label compra_lblDisponibilidade;
    @FXML private Label compra_lblErro;
    @FXML private Button compra_btnContinuar;

    private Teatro teatro;
    private Usuario usuario;
    private SceneManager sceneManager;
    private Sessao sessao;
    private Area areaSelecionada;
    private List<Area> areasDisponiveis;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sceneManager = SceneManager.getInstance();
        teatro = sceneManager.getTeatro();
        usuario = sceneManager.getUsuarioLogado();
        
        // Obter dados passados pela navegação
        Object sessaoData = sceneManager.getUserData("sessao_selecionada");
        if (sessaoData instanceof Sessao) {
            this.sessao = (Sessao) sessaoData;
            carregarDadosSessao();
        }

        if (usuario != null) {
            compra_lblUsuario.setText(usuario.getNome());
        }

        // Ocultar informações da área inicialmente
        compra_containerInfoArea.setVisible(false);
        compra_containerInfoArea.setManaged(false);
        compra_lblErro.setVisible(false);
        
        // Desabilitar botão continuar inicialmente
        compra_btnContinuar.setDisable(true);

        configurarComboBoxAreas();
        carregarAreasDisponiveis();
    }

    private void carregarDadosSessao() {
        if (sessao != null) {
            // Buscar o evento correspondente à sessão
            Evento evento = teatro.getEventos().stream()
                .filter(evt -> evt.getSessoes().contains(sessao))
                .findFirst()
                .orElse(null);

            if (evento != null) {
                compra_lblEventoNome.setText(evento.getNome());
            } else {
                compra_lblEventoNome.setText("Evento desconhecido");
            }
            
            compra_lblHorario.setText(sessao.getTipoSessao().getDescricao());
        }
    }

    private void configurarComboBoxAreas() {
        compra_cmbAreas.setConverter(new StringConverter<Area>() {
            @Override
            public String toString(Area area) {
                return area != null ? area.toString() : "";
            }

            @Override
            public Area fromString(String string) {
                return null; // Não usado para seleção
            }
        });
    }

    private void carregarAreasDisponiveis() {
        if (sessao == null) return;

        try {
            areasDisponiveis = teatro.getAreasDisponiveis(sessao);
            
            if (areasDisponiveis.isEmpty()) {
                showError("Não há áreas disponíveis para esta sessão.");
                return;
            }

            // Ordenar áreas
            Collections.sort(areasDisponiveis);
            compra_cmbAreas.getItems().clear();
            compra_cmbAreas.getItems().addAll(areasDisponiveis);
            
        } catch (Exception e) {
            showError("Erro ao carregar áreas disponíveis: " + e.getMessage());
        }
    }

    @FXML
    private void handleAreaSelecionada() {
        areaSelecionada = compra_cmbAreas.getValue();
        
        if (areaSelecionada != null) {
            try {
                // Atualizar informações da área
                Area areaAtualizada = teatro.getAreaAtualizada(sessao.getId(), areaSelecionada.getId());
                
                if (areaAtualizada != null) {
                    compra_lblPreco.setText(String.format("R$ %.2f", areaAtualizada.getPreco()));
                    compra_lblDisponibilidade.setText(
                        areaAtualizada.getPoltronasDisponiveis() + " de " + areaAtualizada.getCapacidadeTotal()
                    );
                    
                    compra_containerInfoArea.setVisible(true);
                    compra_containerInfoArea.setManaged(true);
                    compra_lblErro.setVisible(false);
                    
                    // Verificar se ainda há poltronas disponíveis
                    if (areaAtualizada.getPoltronasDisponiveis() == 0) {
                        showError("Esta área não possui mais poltronas disponíveis. Selecione outra área.");
                        compra_cmbAreas.setValue(null);
                        areaSelecionada = null;
                        ocultarInfoArea();
                        compra_btnContinuar.setDisable(true);
                    } else {
                        compra_btnContinuar.setDisable(false);
                    }
                } else {
                    showError("Erro ao atualizar informações da área.");
                    ocultarInfoArea();
                    compra_btnContinuar.setDisable(true);
                }
            } catch (Exception e) {
                showError("Erro ao verificar disponibilidade da área: " + e.getMessage());
                ocultarInfoArea();
                compra_btnContinuar.setDisable(true);
            }
        } else {
            ocultarInfoArea();
            compra_btnContinuar.setDisable(true);
        }
    }

    private void ocultarInfoArea() {
        compra_containerInfoArea.setVisible(false);
        compra_containerInfoArea.setManaged(false);
    }

    @FXML
    private void handleContinuar() {
        if (areaSelecionada == null) {
            showError("Por favor, selecione uma área.");
            return;
        }

        try {
            // Passar dados para a próxima tela
            sceneManager.setUserData("sessao_selecionada", sessao);
            sceneManager.setUserData("area_selecionada", areaSelecionada);
            
            sceneManager.loadScene("/com/teatro/view/fxml/selecionar-poltrona.fxml",
                                 "Sistema de Teatro - Seleção de Poltronas");
        } catch (Exception e) {
            showError("Erro ao navegar para seleção de poltronas: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        sceneManager.goToSessoes();
    }

    @FXML
    private void handleVoltar() {
        sceneManager.goToSessoes();
    }

    @FXML
    private void handleSair() {
        sceneManager.goToLogin();
    }

    private void showError(String mensagem) {
        compra_lblErro.setText(mensagem);
        compra_lblErro.setVisible(true);
    }
}