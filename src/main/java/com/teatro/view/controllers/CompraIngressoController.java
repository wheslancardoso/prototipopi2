package com.teatro.view.controllers;

import com.teatro.model.*;
import com.teatro.view.util.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller para a tela de compra de ingressos.
 */
public class CompraIngressoController implements Initializable {
    
    @FXML
    private Label compra_lblLogo;
    
    @FXML
    private Label compra_lblUsuario;
    
    @FXML
    private Label compra_lblTitulo;
    
    @FXML
    private Label compra_lblEventoNome;
    
    @FXML
    private Label compra_lblHorario;
    
    @FXML
    private ComboBox<Area> compra_cmbAreas;
    
    @FXML
    private VBox compra_containerInfoArea;
    
    @FXML
    private Label compra_lblPreco;
    
    @FXML
    private Label compra_lblDisponibilidade;
    
    @FXML
    private Button compra_btnVoltar;
    
    @FXML
    private Button compra_btnSair;
    
    @FXML
    private Button compra_btnCancelar;
    
    @FXML
    private Button compra_btnContinuar;
    
    @FXML
    private Label compra_lblErro;
    
    private Teatro teatro;
    private Usuario usuario;
    private Sessao sessao;
    private Evento evento;
    private Area areaSelecionada;
    private List<Area> areasDisponiveis;
    private SceneManager sceneManager;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sceneManager = SceneManager.getInstance();
        teatro = sceneManager.getTeatro();
        usuario = sceneManager.getUsuarioLogado();
        
        // Configurar interface
        if (usuario != null) {
            compra_lblUsuario.setText(usuario.getNome());
        }
        
        // Inicializar estado dos botões
        compra_btnContinuar.setDisable(true);
        compra_lblErro.setVisible(false);
        
        // Configurar ComboBox
        setupAreaComboBox();
    }
    
    /**
     * Define a sessão selecionada.
     */
    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
        
        // Buscar o evento correspondente
        this.evento = teatro.getEventos().stream()
            .filter(evt -> evt.getSessoes().contains(sessao))
            .findFirst()
            .orElse(null);
        
        if (evento != null && sessao != null) {
            carregarInformacoesSessao();
            carregarAreasDisponiveis();
        }
    }
    
    /**
     * Configura o ComboBox de áreas.
     */
    private void setupAreaComboBox() {
        // Configurar como as áreas são exibidas no ComboBox
        compra_cmbAreas.setCellFactory(listView -> new ListCell<Area>() {
            @Override
            protected void updateItem(Area item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString()); // Usa o método toString() da Area
                }
            }
        });
        
        compra_cmbAreas.setButtonCell(new ListCell<Area>() {
            @Override
            protected void updateItem(Area item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Selecione uma área");
                } else {
                    setText(item.toString());
                }
            }
        });
    }
    
    /**
     * Carrega as informações da sessão.
     */
    private void carregarInformacoesSessao() {
        if (evento != null) {
            compra_lblEventoNome.setText(evento.getNome());
        }
        
        if (sessao != null) {
            compra_lblHorario.setText(sessao.getHorario());
        }
    }
    
    /**
     * Carrega as áreas disponíveis para a sessão.
     */
    private void carregarAreasDisponiveis() {
        try {
            areasDisponiveis = teatro.getAreasDisponiveis(sessao);
            
            if (areasDisponiveis.isEmpty()) {
                mostrarErro("Não há áreas disponíveis para esta sessão.");
                compra_btnContinuar.setDisable(true);
                return;
            }
            
            // Limpar e popular ComboBox
            compra_cmbAreas.getItems().clear();
            compra_cmbAreas.getItems().addAll(areasDisponiveis);
            
        } catch (Exception e) {
            mostrarErro("Erro ao carregar áreas disponíveis: " + e.getMessage());
        }
    }
    
    /**
     * Manipula a seleção de área.
     */
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
                    
                    // Mostrar informações da área
                    compra_containerInfoArea.setVisible(true);
                    compra_containerInfoArea.setManaged(true);
                    
                    // Verificar se ainda há poltronas disponíveis
                    if (areaAtualizada.getPoltronasDisponiveis() == 0) {
                        mostrarErro("Esta área não possui mais poltronas disponíveis. Selecione outra área.");
                        compra_btnContinuar.setDisable(true);
                    } else {
                        ocultarErro();
                        compra_btnContinuar.setDisable(false);
                    }
                    
                    // Atualizar referência
                    areaSelecionada = areaAtualizada;
                } else {
                    mostrarErro("Erro ao obter informações atualizadas da área.");
                    compra_btnContinuar.setDisable(true);
                }
                
            } catch (Exception e) {
                mostrarErro("Erro ao carregar informações da área: " + e.getMessage());
                compra_btnContinuar.setDisable(true);
            }
        } else {
            // Nenhuma área selecionada
            compra_containerInfoArea.setVisible(false);
            compra_containerInfoArea.setManaged(false);
            compra_btnContinuar.setDisable(true);
            ocultarErro();
        }
    }
    
    /**
     * Manipula o botão voltar.
     */
    @FXML
    private void handleVoltar() {
        try {
            sceneManager.goToSessoes();
        } catch (Exception e) {
            mostrarErro("Erro ao voltar para sessões: " + e.getMessage());
        }
    }
    
    /**
     * Manipula o botão sair.
     */
    @FXML
    private void handleSair() {
        try {
            sceneManager.goToLogin();
        } catch (Exception e) {
            mostrarErro("Erro ao fazer logout: " + e.getMessage());
        }
    }
    
    /**
     * Manipula o botão cancelar.
     */
    @FXML
    private void handleCancelar() {
        handleVoltar();
    }
    
    /**
     * Manipula o botão continuar.
     */
    @FXML
    private void handleContinuar() {
        if (areaSelecionada == null) {
            mostrarErro("Por favor, selecione uma área.");
            return;
        }
        
        try {
            // Carregar tela de seleção de poltronas
            sceneManager.loadScene("/com/teatro/view/fxml/selecionar-poltrona.fxml", 
                                 "Sistema de Teatro - Seleção de Poltronas");
            
            // TODO: Passar sessão e área para o próximo controller
            // Isso pode ser feito através do SceneManager ou através de um controlador global
            
        } catch (Exception e) {
            mostrarErro("Erro ao abrir tela de seleção de poltronas: " + e.getMessage());
        }
    }
    
    /**
     * Exibe uma mensagem de erro.
     */
    private void mostrarErro(String mensagem) {
        compra_lblErro.setText(mensagem);
        compra_lblErro.setVisible(true);
    }
    
    /**
     * Oculta a mensagem de erro.
     */
    private void ocultarErro() {
        compra_lblErro.setVisible(false);
    }
    
    /**
     * Obtém a sessão selecionada.
     */
    public Sessao getSessao() {
        return sessao;
    }
    
    /**
     * Obtém a área selecionada.
     */
    public Area getAreaSelecionada() {
        return areaSelecionada;
    }
    
    /**
     * Obtém o evento.
     */
    public Evento getEvento() {
        return evento;
    }
}