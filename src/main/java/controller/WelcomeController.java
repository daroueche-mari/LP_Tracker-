package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import util.UIUtils;

public class WelcomeController {

    @FXML
    private void handleGoToLogin(ActionEvent event) {
        UIUtils.switchScene(event, "LoginView.fxml", "LP Tracker - Connexion");
    }

    @FXML
    private void handleGoToRegister(ActionEvent event) {
        UIUtils.switchScene(event, "RegisterView.fxml", "LP Tracker - Inscription");
    }
}