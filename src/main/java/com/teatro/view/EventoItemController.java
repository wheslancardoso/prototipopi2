package com.teatro.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.teatro.model.Evento;
import com.teatro.model.Sessao;
import com.teatro.model.Teatro;
import com.teatro.model.Usuario;
import java.io.InputStream;
import java.util.Map;

public class EventoItemController {
    @FXML
    private Label labelNomeEvento;
    @FXML
    private ImageView posterImageView;
    @FXML
    private HBox sessoesBox;

    private static final Map<String, String> EVENTO_POSTER_MAP = Map.of(
        "Hamlet", "hamletposter.jpg",
        "O Auto da Compadecida", "compadecidaposter.jpg",
        "O Fantasma da Opera", "ofantasmadaoperaposter.jpg"
    );

    public void configurarEvento(Evento evento, Teatro teatro, Usuario usuario, Stage stage) {
        labelNomeEvento.setText(evento.getNome());
        String posterFile = evento.getPoster() != null ? evento.getPoster() : "default.jpg";
        InputStream imgStream = getClass().getResourceAsStream("/posters/" + posterFile);
        if (imgStream != null) {
            posterImageView.setImage(new Image(imgStream));
        } else {
            posterImageView.setImage(null);
        }
        sessoesBox.getChildren().clear();
        for (Sessao sessao : evento.getSessoes()) {
            Button btn = new Button(sessao.getHorario());
            btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 22; -fx-font-size: 15px; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #217dbb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 22; -fx-font-size: 15px; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 22; -fx-font-size: 15px; -fx-cursor: hand;"));
            btn.setOnAction(e -> new CompraIngressoView(teatro, usuario, stage, sessao).show());
            sessoesBox.getChildren().add(btn);
        }
    }
} 