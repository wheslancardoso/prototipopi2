package com.teatro.view.controllers;

import com.teatro.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SelecionarPoltronaController extends BaseController {

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

    private Sessao sessao;
    private Area area;
    private List<Poltrona> poltronasSelecionadas;
    private List<Integer> poltronasDisponiveis;

    @Override
    protected void inicializarComponentes() {
        if (usuario != null) {
            poltrona_lblUsuario.setText(usuario.getNome());
        }
        
        poltronasSelecionadas = new ArrayList<>();
        poltronasDisponiveis = new ArrayList<>();
        
        poltrona_lblErro.setVisible(false);
        poltrona_btnConfirmar.setDisable(true);
    }

    @Override
    protected void configurarEventos() {
        // Eventos serão configurados nas poltronas dinamicamente
    }

    @Override
    protected void carregarDados() {
        // Receber dados do SceneManager
        Object sessaoData = sceneManager.getUserData("sessao");
        Object areaData = sceneManager.getUserData("area");
        
        if (sessaoData instanceof Sessao && areaData instanceof Area) {
            this.sessao = (Sessao) sessaoData;
            this.area = (Area) areaData;
            
            carregarInformacoesSessao();
            carregarPoltronasDisponiveis();
            criarGridPoltronas();
            atualizarResumo();
        } else {
            mostrarErro("Erro", "Dados da sessão ou área não encontrados");
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
            poltrona_lblEvento.setText(evento.getNome());
        }
        
        poltrona_lblHorario.setText(sessao.getTipoSessao().getDescricao());
        poltrona_lblArea.setText(area.getNome() + " - R$ " + String.format("%.2f", area.getPreco()));
        poltrona_lblValorUnitario.setText("R$ " + String.format("%.2f", area.getPreco()));
    }

    private void carregarPoltronasDisponiveis() {
        try {
            this.poltronasDisponiveis = teatro.getPoltronasDisponiveis(sessao, area);
            
            if (poltronasDisponiveis.isEmpty()) {
                mostrarErro("Atenção", "Esta área não possui mais poltronas disponíveis.");
                handleVoltar();
            }
        } catch (Exception e) {
            mostrarErro("Erro", "Erro ao carregar poltronas disponíveis: " + e.getMessage());
            handleVoltar();
        }
    }

    private void criarGridPoltronas() {
        poltrona_gridPoltronas.getChildren().clear();
        
        int numColunas = 10;
        int capacidade = area.getCapacidadeTotal();
        int numLinhas = (int) Math.ceil((double) capacidade / numColunas);

        for (int i = 0; i < numLinhas; i++) {
            for (int j = 0; j < numColunas; j++) {
                int numero = i * numColunas + j + 1;
                if (numero > capacidade) break;

                Button poltrona = new Button(String.valueOf(numero));
                poltrona.setPrefSize(50, 50);
                poltrona.getStyleClass().add("poltrona-button");

                boolean disponivel = poltronasDisponiveis.contains(numero);

                if (disponivel) {
                    poltrona.getStyleClass().add("poltrona-disponivel");
                    poltrona.setOnAction(e -> handlePoltronaClick(numero, poltrona));
                } else {
                    poltrona.getStyleClass().add("poltrona-ocupada");
                    poltrona.setDisable(true);
                }

                poltrona_gridPoltronas.add(poltrona, j, i);
            }
        }
    }

    private void handlePoltronaClick(int numero, Button poltrona) {
        try {
            // Verificar se ainda está disponível
            if (!teatro.verificarPoltronaDisponivel(sessao, area, numero)) {
                mostrarErro("Atenção", "Esta poltrona acabou de ser ocupada.");
                poltrona.getStyleClass().remove("poltrona-disponivel");
                poltrona.getStyleClass().add("poltrona-ocupada");
                poltrona.setDisable(true);
                poltronasDisponiveis.remove(Integer.valueOf(numero));
                poltronasSelecionadas.removeIf(p -> p.getNumero() == numero);
                atualizarResumo();
                return;
            }
            
            if (poltrona.getStyleClass().contains("poltrona-selecionada")) {
                // Desselecionar
                poltrona.getStyleClass().remove("poltrona-selecionada");
                poltrona.getStyleClass().add("poltrona-disponivel");
                poltronasSelecionadas.removeIf(p -> p.getNumero() == numero);
            } else {
                // Selecionar
                poltrona.getStyleClass().remove("poltrona-disponivel");
                poltrona.getStyleClass().add("poltrona-selecionada");
                poltronasSelecionadas.add(new Poltrona(numero, area));
            }
            
            atualizarResumo();
            ocultarErro();
            
        } catch (Exception e) {
            mostrarError("Erro ao verificar poltrona: " + e.getMessage());
        }
    }

    private void atualizarResumo() {
        int quantidade = poltronasSelecionadas.size();
        double valorTotal = quantidade * area.getPreco();
        
        poltrona_lblQuantidade.setText(String.valueOf(quantidade));
        poltrona_lblValorTotal.setText("R$ " + String.format("%.2f", valorTotal));
        
        poltrona_btnConfirmar.setDisable(quantidade == 0);
    }

    private void mostrarError(String mensagem) {
        poltrona_lblErro.setText(mensagem);
        poltrona_lblErro.setVisible(true);
    }

    private void ocultarErro() {
        poltrona_lblErro.setVisible(false);
    }

    @FXML
    private void handleVoltar() {
        try {
            sceneManager.loadScene("/com/teatro/view/fxml/compra-ingresso.fxml",
                                 "Sistema de Teatro - Compra de Ingressos");
        } catch (Exception e) {
            mostrarErro("Erro", "Erro ao voltar: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        handleVoltar();
    }

    @FXML
    private void handleConfirmar() {
        if (poltronasSelecionadas.isEmpty()) {
            mostrarError("Selecione pelo menos uma poltrona.");
            return;
        }

        try {
            // Verificar disponibilidade de todas as poltronas
            for (Poltrona poltrona : new ArrayList<>(poltronasSelecionadas)) {
                if (!teatro.verificarPoltronaDisponivel(sessao, area, poltrona.getNumero())) {
                    mostrarError("A poltrona " + poltrona.getNumero() + " acabou de ser ocupada.");
                    poltronasSelecionadas.remove(poltrona);
                    atualizarResumo();
                    return;
                }
            }

            // Criar ingressos
            List<IngressoModerno> ingressos = new ArrayList<>();
            boolean todosSalvos = true;

            for (Poltrona poltrona : poltronasSelecionadas) {
                try {
                    // Buscar o evento da sessão
                    Evento evento = teatro.getEventos().stream()
                        .filter(evt -> evt.getSessoes().contains(sessao))
                        .findFirst()
                        .orElse(null);

                    if (evento == null) {
                        throw new Exception("Evento não encontrado");
                    }

                    Optional<IngressoModerno> ingressoOpt = teatro.comprarIngresso(
                        usuario.getCpf(), evento, sessao, area, poltrona.getNumero()
                    );

                    if (ingressoOpt.isPresent()) {
                        ingressos.add(ingressoOpt.get());
                    } else {
                        throw new Exception("Erro ao comprar ingresso para poltrona " + poltrona.getNumero());
                    }

                } catch (Exception ex) {
                    todosSalvos = false;
                    mostrarErro("Erro", ex.getMessage());
                    break;
                }
            }

            if (todosSalvos && !ingressos.isEmpty()) {
                // Adicionar ingressos ao usuário
                usuario.adicionarIngressos(ingressos);

                // Passar dados para tela de impressão
                sceneManager.setUserData("ingressos", ingressos);

                // Navegar para tela de impressão
                sceneManager.loadScene("/com/teatro/view/fxml/impressao-ingresso.fxml",
                                     "Sistema de Teatro - Impressão de Ingressos");
            }

        } catch (Exception e) {
            mostrarErro("Erro", "Erro ao processar compra: " + e.getMessage());
        }
    }
}