package controller;

import dao.InventoryDAO;
import model.Inventory;
import model.Shoe;
import model.ShoeVariant;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SearchController {

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private FlowPane resultsFlowPane;

    private InventoryDAO inventoryDAO;
    private ResourceBundle resources;

    public void initialize(ResourceBundle resources) {
        this.resources = resources;
        inventoryDAO = new InventoryDAO();

        // Configurare acțiuni butoane
        searchButton.setOnAction(event -> handleSearch());

        // Configurare pentru a permite căutare la apăsarea Enter
        searchField.setOnAction(event -> handleSearch());
    }

    /**
     * Setează termenul de căutare în câmpul de căutare și efectuează automat o căutare
     * @param searchTerm Termenul de căutare
     */
    public void setSearchTerm(String searchTerm) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            searchField.setText(searchTerm);
            handleSearch(); // Efectuăm automat căutarea cu termenul furnizat
        }
    }

    @FXML
    public void handleSearch() {
        String modelName = searchField.getText().trim();

        if (modelName.isEmpty()) {
            // Afișare mesaj pentru a introduce un termen de căutare
            showNoResultsMessage(resources.getString("search.empty"));
            return;
        }

        // Căutare în baza de date
        List<Inventory> searchResults = inventoryDAO.searchInventoryByModel(modelName);

        if (searchResults.isEmpty()) {
            // Afișare mesaj pentru niciun rezultat
            showNoResultsMessage(resources.getString("search.no.results"));
            return;
        }

        // Organizare rezultate după model și culoare
        displaySearchResults(searchResults);
    }

    private void displaySearchResults(List<Inventory> searchResults) {
        // Curățare rezultate anterioare
        resultsFlowPane.getChildren().clear();

        // Grupare după ShoeVariant
        Map<ShoeVariant, List<Inventory>> groupedResults = searchResults.stream()
                .collect(Collectors.groupingBy(Inventory::getVariant));

        // Afișare rezultate grupate
        for (Map.Entry<ShoeVariant, List<Inventory>> entry : groupedResults.entrySet()) {
            ShoeVariant variant = entry.getKey();
            List<Inventory> variantInventory = entry.getValue();

            if (variant != null && variant.getShoe() != null) {
                // Creare card pentru fiecare variantă
                VBox variantCard = createVariantCard(variant, variantInventory);
                resultsFlowPane.getChildren().add(variantCard);
            }
        }
    }

    private VBox createVariantCard(ShoeVariant variant, List<Inventory> inventory) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        card.setPrefWidth(220);
        card.setPrefHeight(350);

        Shoe shoe = variant.getShoe();

        // Imagine produs
        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);

        // Încărcare imagine
        String imagePath = variant.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                try {
                    Image image = new Image(imageFile.toURI().toString());
                    imageView.setImage(image);
                } catch (Exception e) {
                    imageView.setImage(new Image(getClass().getResourceAsStream("/com/shoestoreapp/resources/images/no_image.png")));
                }
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/com/shoestoreapp/resources/images/no_image.png")));
            }
        } else {
            imageView.setImage(new Image(getClass().getResourceAsStream("/com/shoestoreapp/resources/images/no_image.png")));
        }

        // Informații produs
        Label modelLabel = new Label(shoe.getModel());
        modelLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label manufacturerLabel = new Label(shoe.getManufacturer() != null ? shoe.getManufacturer().getName() : "");

        Label typeLabel = new Label(shoe.getType() != null ? shoe.getType().getName() : "");

        Label priceLabel = new Label(String.format("%.2f RON", shoe.getPrice()));
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        // Culoare
        Label colorLabel = new Label(resources.getString("inventory.color") + ": " +
                (variant.getColor() != null ? variant.getColor().getName() : ""));

        // Disponibilitate pe mărimi
        VBox sizesBox = new VBox(5);
        sizesBox.setAlignment(Pos.CENTER_LEFT);
        Label sizesTitle = new Label(resources.getString("inventory.size") + " / " +
                resources.getString("inventory.stock") + ":");
        sizesTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        sizesBox.getChildren().add(sizesTitle);

        // Creează un label pentru fiecare mărime și stocul asociat
        Map<Double, Integer> sizeStock = new HashMap<>();
        for (Inventory item : inventory) {
            if (item.getSize() != null) {
                double euSize = item.getSize().getEuSize();
                sizeStock.put(euSize, item.getStock());
            }
        }

        // Sortare după mărime
        sizeStock.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Label sizeStockLabel = new Label(String.format("EU: %.1f - %d %s",
                            entry.getKey(), entry.getValue(),
                            resources.getString(entry.getValue() > 0 ? "inventory.available" : "inventory.unavailable")));

                    // Colorare în funcție de disponibilitate
                    if (entry.getValue() <= 0) {
                        sizeStockLabel.setTextFill(Color.RED);
                    } else {
                        sizeStockLabel.setTextFill(Color.GREEN);
                    }

                    sizesBox.getChildren().add(sizeStockLabel);
                });

        // Adăugare componente la card
        card.getChildren().addAll(
                imageView,
                modelLabel,
                manufacturerLabel,
                typeLabel,
                colorLabel,
                priceLabel,
                sizesBox
        );

        return card;
    }

    private void showNoResultsMessage(String message) {
        resultsFlowPane.getChildren().clear();

        Label noResultsLabel = new Label(message);
        noResultsLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        noResultsLabel.setAlignment(Pos.CENTER);
        noResultsLabel.setPrefWidth(resultsFlowPane.getPrefWidth());
        noResultsLabel.setPrefHeight(100);

        resultsFlowPane.getChildren().add(noResultsLabel);
    }
}