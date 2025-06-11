package com.teatro.view.controllers;

import com.teatro.model.*;
import com.teatro.view.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.io.IOException;

public class SelecionarPoltronaController implements Initializable {

    @FXML private Label poltrona_lblUsuario;
    @FXML private Label poltrona_lblEvento;
    @FXML private Label poltrona_lblHorario;
    @FXML private Label poltrona_lblArea;
    @FXML private GridPane poltrona_gridPoltronas;
    @FXML private Label poltrona_lblQuantidade;
    @FXML private Label poltrona_lblValorUnitario;
    @FXML private Label poltrona_lblValorTotal;
    @FXML private Label poltrona_lblErro;
    @FXML private Button poltrona_btnConfirmar;

    private Teatro teatro;
    private Usuario usuario;
    private SceneManager sceneManager;
    private Sessao sessao;
    private Area area;
    private List<Integer> poltronasSelecionadas;
    private List<Integer> poltronasDisponiveis;

    // Cores das poltronas
    private static final String COR_DISPONIVEL = "#2ecc71";
    private static final String COR_OCUPADA = "#e74c3c";
    private static final String COR_SELECIONADA = "#3498db";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sceneManager = SceneManager.getInstance();
        teatro = sceneManager.getTeatro();
        usuario = sceneManager.getUsuarioLogado();
        
        // Obter dados passados pela navegação
        Object sessaoData = sceneManager.getUserData("sessao_selecionada");
        Object areaData = sceneManager.getUserData("area_selecionada");
        
        if (sessaoData instanceof Sessao && areaData instanceof Area) {
            this.sessao = (Sessao) sessaoData;
            this.area = (Area) areaData;
            carregarDados();
        }

        if (usuario != null) {
            poltrona_lblUsuario.setText(usuario.getNome());
        }

        this.poltronasSelecionadas = new ArrayList<>();
        poltrona_lblErro.setVisible(false);
        poltrona_btnConfirmar.setDisable(true);

        carregarPoltronasDisponiveis();
        criarMapaPoltronas();
        atualizarResumo();
    }

    private void carregarDados() {
        if (sessao != null && area != null) {
            // Buscar o evento correspondente à sessão
            Evento evento = teatro.getEventos().stream()
                .filter(evt -> evt.getSessoes().contains(sessao))
                .findFirst()
                .orElse(null);

            if (evento != null) {
                poltrona_lblEvento.setText(evento.getNome());
            } else {
                poltrona_lblEvento.setText("Evento desconhecido");
            }
            
            poltrona_lblHorario.setText(sessao.getTipoSessao().getDescricao());
            poltrona_lblArea.setText(area.getNome() + " - R$ " + String.format("%.2f", area.getPreco()));
            poltrona_lblValorUnitario.setText("R$ " + String.format("%.2f", area.getPreco()));
        }
    }

    private void carregarPoltronasDisponiveis() {
        try {
            if (sessao != null && area != null) {
                poltronasDisponiveis = teatro.getPoltronasDisponiveis(sessao, area);
                
                if (poltronasDisponiveis.isEmpty()) {
                    showError("Esta área não possui poltronas disponíveis.");
                }
            }
        } catch (Exception e) {
            showError("Erro ao carregar poltronas disponíveis: " + e.getMessage());
        }
    }

    private void criarMapaPoltronas() {
        if (area == null) return;

        poltrona_gridPoltronas.getChildren().clear();

        int numColunas = 10;
        int capacidade = area.getCapacidadeTotal();
        int numLinhas = (int) Math.ceil((double) capacidade / numColunas);

        for (int i = 0; i < numLinhas; i++) {
            for (int j = 0; j < numColunas; j++) {
                int numeroPoltrona = i * numColunas + j + 1;
                if (numeroPoltrona > capacidade) break;

                Button btnPoltrona = new Button(String.valueOf(numeroPoltrona));
                btnPoltrona.setPrefSize(50, 50);
                btnPoltrona.setFont(javafx.scene.text.Font.font("System", 
                    javafx.scene.text.FontWeight.BOLD, 14));

                // Verificar se a poltrona está disponível
                boolean disponivel = poltronasDisponiveis.contains(numeroPoltrona);

                if (disponivel) {
                    configurarPoltronaDisponivel(btnPoltrona, numeroPoltrona);
                } else {
                    configurarPoltronaOcupada(btnPoltrona);
                }

                poltrona_gridPoltronas.add(btnPoltrona, j, i);
            }
        }
    }

    private void configurarPoltronaDisponivel(Button btnPoltrona, int numeroPoltrona) {
        btnPoltrona.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 5;",
            COR_DISPONIVEL
        ));

        btnPoltrona.setOnAction(e -> {
            try {
                // Verificar se a poltrona ainda está disponível
                if (!teatro.verificarPoltronaDisponivel(sessao, area, numeroPoltrona)) {
                    showError("Esta poltrona acabou de ser ocupada. Selecione outra.");
                    criarMapaPoltronas(); // Recriar mapa
                    return;
                }

                if (poltronasSelecionadas.contains(numeroPoltrona)) {
                    // Desselecionar
                    poltronasSelecionadas.remove(Integer.valueOf(numeroPoltrona));
                    btnPoltrona.setStyle(String.format(
                        "-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 5;",
                        COR_DISPONIVEL
                    ));
                } else {
                    // Selecionar
                    poltronasSelecionadas.add(numeroPoltrona);
                    btnPoltrona.setStyle(String.format(
                        "-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 5;",
                        COR_SELECIONADA
                    ));
                }
                
                atualizarResumo();
                poltrona_lblErro.setVisible(false);
                
            } catch (Exception ex) {
                showError("Erro ao selecionar poltrona: " + ex.getMessage());
            }
        });
    }

    private void configurarPoltronaOcupada(Button btnPoltrona) {
        btnPoltrona.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 5;",
            COR_OCUPADA
        ));
        btnPoltrona.setDisable(true);
    }

    private void atualizarResumo() {
        int quantidade = poltronasSelecionadas.size();
        double valorTotal = quantidade * area.getPreco();

        poltrona_lblQuantidade.setText(String.valueOf(quantidade));
        poltrona_lblValorTotal.setText("R$ " + String.format("%.2f", valorTotal));

        poltrona_btnConfirmar.setDisable(quantidade == 0);
    }

    @FXML
    private void handleConfirmar() {
        if (poltronasSelecionadas.isEmpty()) {
            showError("Selecione pelo menos uma poltrona.");
            return;
        }

        try {
            // Verificar se todas as poltronas ainda estão disponíveis
            for (Integer numeroPoltrona : poltronasSelecionadas) {
                if (!teatro.verificarPoltronaDisponivel(sessao, area, numeroPoltrona)) {
                    showError("Algumas poltronas foram ocupadas. Selecione outras.");
                    criarMapaPoltronas();
                    poltronasSelecionadas.clear();
                    atualizarResumo();
                    return;
                }
            }

            // Processar compra dos ingressos
            List<IngressoModerno> ingressosComprados = new ArrayList<>();
            
            // Buscar o evento da sessão
            Evento evento = teatro.getEventos().stream()
                .filter(evt -> evt.getSessoes().contains(sessao))
                .findFirst()
                .orElse(null);
            
            if (evento == null) {
                showError("Erro: Não foi possível encontrar o evento.");
                return;
            }

            boolean todasComprasSucesso = true;
            
            for (Integer numeroPoltrona : poltronasSelecionadas) {
                try {
                    Optional<IngressoModerno> ingressoOpt = teatro.comprarIngresso(
                        usuario.getCpf(), evento, sessao, area, numeroPoltrona
                    );
                    
                    if (ingressoOpt.isPresent()) {
                        ingressosComprados.add(ingressoOpt.get());
                    } else {
                        todasComprasSucesso = false;
                        showError("Erro ao comprar ingresso para poltrona " + numeroPoltrona);
                        break;
                    }
                } catch (Exception e) {
                    todasComprasSucesso = false;
                    showError("Erro ao processar compra: " + e.getMessage());
                    break;
                }
            }

            if (todasComprasSucesso && !ingressosComprados.isEmpty()) {
                // Adicionar ingressos ao usuário
                usuario.adicionarIngressos(ingressosComprados);
                
                // Passar dados para a tela de impressão
                sceneManager.setUserData("ingressos_comprados", ingressosComprados);
                
                sceneManager.loadScene("/com/teatro/view/fxml/impressao-ingresso.fxml",
                                     "Sistema de Teatro - Impressão de Ingressos");
            }
            
        } catch (Exception e) {
            showError("Erro ao processar compra: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        try {
            sceneManager.loadScene("/com/teatro/view/fxml/compra-ingresso.fxml",
                                 "Sistema de Teatro - Compra de Ingressos");
        } catch (IOException e) {
            showError("Erro ao voltar: " + e.getMessage());
        }
    }

    @FXML
    private void handleVoltar() {
        try {
            sceneManager.loadScene("/com/teatro/view/fxml/compra-ingresso.fxml",
                                 "Sistema de Teatro - Compra de Ingressos");
        } catch (Exception e) {
            showError("Erro ao voltar: " + e.getMessage());
        }
    }

    @FXML
    private void handleSair() {
        sceneManager.goToLogin();
    }

    private void showError(String mensagem) {
        poltrona_lblErro.setText(mensagem);
        poltrona_lblErro.setVisible(true);
    }
}