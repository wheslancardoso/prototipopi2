package com.teatro.view.controllers;

import com.teatro.model.*;
import com.teatro.view.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller para a tela de seleção de poltronas.
 */
public class SelecionarPoltronaController implements Initializable {
    
    @FXML
    private Label poltrona_lblLogo;
    
    @FXML
    private Label poltrona_lblUsuario;
    
    @FXML
    private Label poltrona_lblTitulo;
    
    @FXML
    private Label poltrona_lblEvento;
    
    @FXML
    private Label poltrona_lblHorario;
    
    @FXML
    private Label poltrona_lblArea;
    
    @FXML
    private Rectangle poltrona_rectTela;
    
    @FXML
    private GridPane poltrona_gridPoltronas;
    
    @FXML
    private Label poltrona_lblQuantidade;
    
    @FXML
    private Label poltrona_lblValorUnitario;
    
    @FXML
    private Label poltrona_lblValorTotal;
    
    @FXML
    private Button poltrona_btnVoltar;
    
    @FXML
    private Button poltrona_btnSair;
    
    @FXML
    private Button poltrona_btnCancelar;
    
    @FXML
    private Button poltrona_btnConfirmar;
    
    @FXML
    private Label poltrona_lblErro;
    
    private Teatro teatro;
    private Usuario usuario;
    private Sessao sessao;
    private Area area;
    private Evento evento;
    private List<Poltrona> poltronasSelecionadas;
    private List<Integer> poltronasDisponiveis;
    private SceneManager sceneManager;
    
    // Constantes para cores das poltronas
    private static final String COR_DISPONIVEL = "#2ecc71";
    private static final String COR_OCUPADA = "#e74c3c";
    private static final String COR_SELECIONADA = "#3498db";
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sceneManager = SceneManager.getInstance();
        teatro = sceneManager.getTeatro();
        usuario = sceneManager.getUsuarioLogado();
        
        // Configurar interface
        if (usuario != null) {
            poltrona_lblUsuario.setText(usuario.getNome());
        }
        
        // Inicializar listas
        poltronasSelecionadas = new ArrayList<>();
        poltronasDisponiveis = new ArrayList<>();
        
        // Inicializar estado dos botões
        poltrona_btnConfirmar.setDisable(true);
        poltrona_lblErro.setVisible(false);
        
        // TODO: Obter sessão e área do controller anterior ou do SceneManager
        // Por agora, vamos simular com dados de exemplo
        carregarDadosExemplo();
    }
    
    /**
     * Carrega dados de exemplo para teste.
     * TODO: Remover quando a navegação estiver implementada.
     */
    private void carregarDadosExemplo() {
        // Este método será removido quando a navegação estiver funcionando
        // Por agora, cria dados fictícios para teste
        if (sessao == null || area == null) {
            // Buscar primeiro evento disponível para teste
            List<Evento> eventos = teatro.getEventos();
            if (!eventos.isEmpty()) {
                evento = eventos.get(0);
                if (!evento.getSessoes().isEmpty()) {
                    sessao = evento.getSessoes().get(0);
                    List<Area> areas = teatro.getAreasDisponiveis(sessao);
                    if (!areas.isEmpty()) {
                        area = areas.get(0);
                    }
                }
            }
        }
        
        if (sessao != null && area != null) {
            carregarInformacoesSessao();
            carregarPoltronasDisponiveis();
            criarGridPoltronas();
            atualizarResumo();
        }
    }
    
    /**
     * Define a sessão e área selecionadas.
     */
    public void setDados(Sessao sessao, Area area, Evento evento) {
        this.sessao = sessao;
        this.area = area;
        this.evento = evento;
        
        if (sessao != null && area != null) {
            carregarInformacoesSessao();
            carregarPoltronasDisponiveis();
            criarGridPoltronas();
            atualizarResumo();
        }
    }
    
    /**
     * Carrega as informações da sessão na interface.
     */
    private void carregarInformacoesSessao() {
        if (evento != null) {
            poltrona_lblEvento.setText(evento.getNome());
        }
        
        if (sessao != null) {
            poltrona_lblHorario.setText(sessao.getHorario());
        }
        
        if (area != null) {
            poltrona_lblArea.setText(area.getNome() + " - R$ " + String.format("%.2f", area.getPreco()));
            poltrona_lblValorUnitario.setText("R$ " + String.format("%.2f", area.getPreco()));
        }
    }
    
    /**
     * Carrega as poltronas disponíveis para a área na sessão.
     */
    private void carregarPoltronasDisponiveis() {
        try {
            poltronasDisponiveis = teatro.getPoltronasDisponiveis(sessao, area);
            
            if (poltronasDisponiveis.isEmpty()) {
                mostrarErro("Não há poltronas disponíveis nesta área.");
                poltrona_btnConfirmar.setDisable(true);
            }
            
        } catch (Exception e) {
            mostrarErro("Erro ao carregar poltronas disponíveis: " + e.getMessage());
        }
    }
    
    /**
     * Cria o grid de poltronas.
     */
    private void criarGridPoltronas() {
        poltrona_gridPoltronas.getChildren().clear();
        
        if (area == null) return;
        
        // Configurar grid baseado na capacidade da área
        int numColunas = 10; // Número padrão de colunas
        int capacidade = area.getCapacidadeTotal();
        int numLinhas = (int) Math.ceil((double) capacidade / numColunas);
        
        // Criar poltronas
        for (int linha = 0; linha < numLinhas; linha++) {
            for (int coluna = 0; coluna < numColunas; coluna++) {
                int numero = linha * numColunas + coluna + 1;
                if (numero > capacidade) break;
                
                Button btnPoltrona = criarBotaoPoltrona(numero);
                poltrona_gridPoltronas.add(btnPoltrona, coluna, linha);
            }
        }
    }
    
    /**
     * Cria um botão para uma poltrona.
     */
    private Button criarBotaoPoltrona(int numero) {
        Button btn = new Button(String.valueOf(numero));
        btn.setPrefSize(50, 50);
        btn.getStyleClass().add("poltrona-button");
        
        // Verificar se a poltrona está disponível
        boolean disponivel = poltronasDisponiveis.contains(numero);
        
        if (disponivel) {
            btn.getStyleClass().add("poltrona-disponivel");
            btn.setOnAction(e -> togglePoltronaSelecionada(numero, btn));
        } else {
            btn.getStyleClass().add("poltrona-ocupada");
            btn.setDisable(true);
        }
        
        return btn;
    }
    
    /**
     * Alterna o estado de seleção de uma poltrona.
     */
    private void togglePoltronaSelecionada(int numero, Button btn) {
        try {
            // Verificar se a poltrona ainda está disponível
            if (!teatro.verificarPoltronaDisponivel(sessao, area, numero)) {
                mostrarErro("Esta poltrona acabou de ser ocupada. Por favor, selecione outra.");
                btn.getStyleClass().removeAll("poltrona-disponivel");
                btn.getStyleClass().add("poltrona-ocupada");
                btn.setDisable(true);
                poltronasDisponiveis.remove(Integer.valueOf(numero));
                removerPoltronaDaSelecao(numero);
                atualizarResumo();
                return;
            }
            
            // Verificar se já está selecionada
            boolean jaSelecionada = poltronasSelecionadas.stream()
                .anyMatch(p -> p.getNumero() == numero);
            
            if (jaSelecionada) {
                // Desselecionar
                removerPoltronaDaSelecao(numero);
                btn.getStyleClass().removeAll("poltrona-selecionada");
                btn.getStyleClass().add("poltrona-disponivel");
            } else {
                // Selecionar
                Poltrona poltrona = new Poltrona(numero, area);
                poltronasSelecionadas.add(poltrona);
                btn.getStyleClass().removeAll("poltrona-disponivel");
                btn.getStyleClass().add("poltrona-selecionada");
            }
            
            atualizarResumo();
            ocultarErro();
            
        } catch (Exception e) {
            mostrarErro("Erro ao selecionar poltrona: " + e.getMessage());
        }
    }
    
    /**
     * Remove uma poltrona da seleção.
     */
    private void removerPoltronaDaSelecao(int numero) {
        poltronasSelecionadas.removeIf(p -> p.getNumero() == numero);
    }
    
    /**
     * Atualiza o resumo da compra.
     */
    private void atualizarResumo() {
        int quantidade = poltronasSelecionadas.size();
        double valorUnitario = area != null ? area.getPreco() : 0.0;
        double valorTotal = quantidade * valorUnitario;
        
        poltrona_lblQuantidade.setText(String.valueOf(quantidade));
        poltrona_lblValorTotal.setText("R$ " + String.format("%.2f", valorTotal));
        
        // Habilitar/desabilitar botão de confirmar
        poltrona_btnConfirmar.setDisable(quantidade == 0);
    }
    
    /**
     * Manipula o botão voltar.
     */
    @FXML
    private void handleVoltar() {
        try {
            sceneManager.loadScene("/com/teatro/view/fxml/compra-ingresso.fxml", 
                                 "Sistema de Teatro - Compra de Ingressos");
        } catch (Exception e) {
            mostrarErro("Erro ao voltar: " + e.getMessage());
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
     * Manipula o botão confirmar compra.
     */
    @FXML
    private void handleConfirmar() {
        if (poltronasSelecionadas.isEmpty()) {
            mostrarErro("Selecione pelo menos uma poltrona.");
            return;
        }
        
        try {
            // Desabilitar botão durante processamento
            poltrona_btnConfirmar.setDisable(true);
            poltrona_btnConfirmar.setText("Processando...");
            
            List<IngressoModerno> ingressos = new ArrayList<>();
            boolean todosSalvos = true;
            
            // Verificar disponibilidade de todas as poltronas novamente
            for (Poltrona poltrona : new ArrayList<>(poltronasSelecionadas)) {
                if (!teatro.verificarPoltronaDisponivel(sessao, area, poltrona.getNumero())) {
                    mostrarErro("A poltrona " + poltrona.getNumero() + " acabou de ser ocupada. Tente novamente.");
                    poltronasSelecionadas.remove(poltrona);
                    atualizarResumo();
                    return;
                }
            }
            
            // Comprar ingressos
            for (Poltrona poltrona : poltronasSelecionadas) {
                try {
                    Optional<IngressoModerno> ingressoOpt = teatro.comprarIngresso(
                        usuario.getCpf(), evento, sessao, area, poltrona.getNumero()
                    );
                    
                    if (ingressoOpt.isPresent()) {
                        ingressos.add(ingressoOpt.get());
                    } else {
                        throw new Exception("Não foi possível comprar o ingresso para a poltrona " + poltrona.getNumero());
                    }
                    
                } catch (Exception ex) {
                    todosSalvos = false;
                    mostrarErro("Erro ao comprar ingresso: " + ex.getMessage());
                    break;
                }
            }
            
            if (todosSalvos && !ingressos.isEmpty()) {
                // Adicionar ingressos ao usuário
                usuario.adicionarIngressos(ingressos);
                
                // Navegar para tela de impressão
                sceneManager.loadScene("/com/teatro/view/fxml/impressao-ingresso.fxml", 
                                     "Sistema de Teatro - Compra Realizada");
                
                // TODO: Passar ingressos para o controller de impressão
                
            } else if (ingressos.isEmpty()) {
                mostrarErro("Nenhum ingresso foi comprado. Tente novamente.");
            }
            
        } catch (Exception e) {
            mostrarErro("Erro ao processar compra: " + e.getMessage());
        } finally {
            // Reabilitar botão
            poltrona_btnConfirmar.setDisable(poltronasSelecionadas.isEmpty());
            poltrona_btnConfirmar.setText("Confirmar Compra");
        }
    }
    
    /**
     * Exibe uma mensagem de erro.
     */
    private void mostrarErro(String mensagem) {
        poltrona_lblErro.setText(mensagem);
        poltrona_lblErro.setVisible(true);
    }
    
    /**
     * Oculta a mensagem de erro.
     */
    private void ocultarErro() {
        poltrona_lblErro.setVisible(false);
    }
    
    /**
     * Obtém as poltronas selecionadas.
     */
    public List<Poltrona> getPoltronasSelecionadas() {
        return new ArrayList<>(poltronasSelecionadas);
    }
    
    /**
     * Obtém o valor total da compra.
     */
    public double getValorTotal() {
        return poltronasSelecionadas.size() * (area != null ? area.getPreco() : 0.0);
    }
}