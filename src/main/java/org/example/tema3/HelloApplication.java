package org.example.tema3;

import util.LanguageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class HelloApplication extends Application {

    private Stage primaryStage;
    private ResourceBundle resourceBundle;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Inițializare manager de limbi
        LanguageManager.setLocale(Locale.ENGLISH);
        resourceBundle = LanguageManager.getResourceBundle();

        try {
            // Creare layout principal
            BorderPane root = new BorderPane();

            // Creare meniu
            MenuBar menuBar = createMenuBar();
            root.setTop(menuBar);

            // Încărcare conținut principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shoestoreapp/resources/fxml/main.fxml"));
            loader.setResources(resourceBundle);
            Parent mainContent = loader.load();
            root.setCenter(mainContent);

            // Creare și configurare scenă
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/com/shoestoreapp/resources/css/application.css").toExternalForm());

            // Configurare fereastră principală
            primaryStage.setTitle(resourceBundle.getString("application.title"));
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/shoestoreapp/resources/images/logo.png")));
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                confirmExit();
            });

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Application Error", "Failed to start application", e.getMessage());
        }
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // Meniu Fișier
        Menu fileMenu = new Menu(resourceBundle.getString("menu.file"));
        MenuItem exitMenuItem = new MenuItem(resourceBundle.getString("menu.file.exit"));
        exitMenuItem.setOnAction(event -> confirmExit());
        fileMenu.getItems().add(exitMenuItem);

        // Meniu Limbă
        Menu languageMenu = new Menu(resourceBundle.getString("menu.language"));
        MenuItem englishMenuItem = new MenuItem(resourceBundle.getString("menu.language.english"));
        MenuItem frenchMenuItem = new MenuItem(resourceBundle.getString("menu.language.french"));
        MenuItem spanishMenuItem = new MenuItem(resourceBundle.getString("menu.language.spanish"));

        englishMenuItem.setOnAction(event -> changeLanguage(Locale.ENGLISH));
        frenchMenuItem.setOnAction(event -> changeLanguage(Locale.FRENCH));
        spanishMenuItem.setOnAction(event -> changeLanguage(new Locale("es")));

        languageMenu.getItems().addAll(englishMenuItem, frenchMenuItem, spanishMenuItem);

        // Meniu Ajutor
        Menu helpMenu = new Menu(resourceBundle.getString("menu.help"));
        MenuItem aboutMenuItem = new MenuItem(resourceBundle.getString("menu.help.about"));
        aboutMenuItem.setOnAction(event -> showAboutDialog());
        helpMenu.getItems().add(aboutMenuItem);

        menuBar.getMenus().addAll(fileMenu, languageMenu, helpMenu);

        return menuBar;
    }

    private void changeLanguage(Locale locale) {
        LanguageManager.setLocale(locale);
        resourceBundle = LanguageManager.getResourceBundle();

        // Reîncarcă aplicația
        try {
            start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Language Change Error", "Failed to change language", e.getMessage());
        }
    }

    private void confirmExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resourceBundle.getString("application.exit.confirm.title"));
        alert.setHeaderText(resourceBundle.getString("application.exit.confirm.header"));
        alert.setContentText(resourceBundle.getString("application.exit.confirm.content"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DatabaseConfig.closeConnection();
            System.exit(0);
        }
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(resourceBundle.getString("application.about.title"));
        alert.setHeaderText(resourceBundle.getString("application.about.header"));
        alert.setContentText(resourceBundle.getString("application.about.content"));
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        // Închidere resurse
        DatabaseConfig.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}