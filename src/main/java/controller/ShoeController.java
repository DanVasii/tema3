package controller;

import dao.ManufacturerDAO;
import dao.ShoeDAO;
import dao.ShoeTypeDAO;
import model.Manufacturer;
import model.Shoe;
import model.ShoeType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.LanguageManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ShoeController {

    @FXML
    private TableView<Shoe> shoeTable;

    @FXML
    private TableColumn<Shoe, Integer> idColumn;

    @FXML
    private TableColumn<Shoe, String> modelColumn;

    @FXML
    private TableColumn<Shoe, String> manufacturerColumn;

    @FXML
    private TableColumn<Shoe, String> typeColumn;

    @FXML
    private TableColumn<Shoe, Double> priceColumn;

    @FXML
    private TextField modelField;

    @FXML
    private ComboBox<Manufacturer> manufacturerComboBox;

    @FXML
    private ComboBox<ShoeType> typeComboBox;

    @FXML
    private TextField priceField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button variantsButton;

    private ShoeDAO shoeDAO;
    private ManufacturerDAO manufacturerDAO;
    private ShoeTypeDAO shoeTypeDAO;
    private ObservableList<Shoe> shoeList;
    private ResourceBundle resources;

    public void initialize() {
        this.resources = LanguageManager.getResourceBundle();
        shoeDAO = new ShoeDAO();
        manufacturerDAO = new ManufacturerDAO();
        shoeTypeDAO = new ShoeTypeDAO();
        shoeList = FXCollections.observableArrayList();

        // Inițializăm coloanele tabelului
        idColumn.setCellValueFactory(new PropertyValueFactory<>("shoeId"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));

        // Pentru coloanele care afișează atribute de obiecte asociate, folosim un wrapper
        manufacturerColumn.setCellValueFactory(cellData -> {
            Shoe shoe = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(
                    () -> shoe.getManufacturer() != null ? shoe.getManufacturer().getName() : ""
            );
        });

        typeColumn.setCellValueFactory(cellData -> {
            Shoe shoe = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(
                    () -> shoe.getType() != null ? shoe.getType().getName() : ""
            );
        });

        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Adăugăm un listener pentru selecție
        shoeTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showShoeDetails(newSelection);
                        updateButton.setDisable(false);
                        deleteButton.setDisable(false);
                        variantsButton.setDisable(false);
                    } else {
                        clearFields();
                        updateButton.setDisable(true);
                        deleteButton.setDisable(true);
                        variantsButton.setDisable(true);
                    }
                });

        // Dezactivăm butoanele update, delete și variants până când utilizatorul selectează un articol
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        variantsButton.setDisable(true);

        // Încărcăm producătorii și tipurile de încălțăminte în combo box-uri
        loadManufacturers();
        loadShoeTypes();

        // Încărcăm lista de încălțăminte
        loadShoes();
    }

    private void loadManufacturers() {
        List<Manufacturer> manufacturers = manufacturerDAO.getAllManufacturers();
        manufacturerComboBox.setItems(FXCollections.observableArrayList(manufacturers));
    }

    private void loadShoeTypes() {
        List<ShoeType> types = shoeTypeDAO.getAllShoeTypes();
        typeComboBox.setItems(FXCollections.observableArrayList(types));
    }

    private void loadShoes() {
        shoeList.clear();
        List<Shoe> shoes = shoeDAO.getAllShoes();
        shoeList.addAll(shoes);
        shoeTable.setItems(shoeList);
    }

    private void showShoeDetails(Shoe shoe) {
        modelField.setText(shoe.getModel());
        manufacturerComboBox.getSelectionModel().select(shoe.getManufacturer());
        typeComboBox.getSelectionModel().select(shoe.getType());
        priceField.setText(String.valueOf(shoe.getPrice()));
        descriptionArea.setText(shoe.getDescription());
    }

    private void clearFields() {
        modelField.clear();
        manufacturerComboBox.getSelectionModel().clearSelection();
        typeComboBox.getSelectionModel().clearSelection();
        priceField.clear();
        descriptionArea.clear();
        shoeTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        variantsButton.setDisable(true);
    }

    @FXML
    private void handleAddButton() {
        if (validateInput()) {
            Shoe shoe = new Shoe();
            shoe.setModel(modelField.getText());
            shoe.setManufacturer(manufacturerComboBox.getSelectionModel().getSelectedItem());
            shoe.setType(typeComboBox.getSelectionModel().getSelectedItem());

            try {
                double price = Double.parseDouble(priceField.getText());
                shoe.setPrice(price);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR,
                        resources.getString("validation.error.title"),
                        resources.getString("validation.error.header"),
                        resources.getString("validation.price.invalid"));
                return;
            }

            shoe.setDescription(descriptionArea.getText());

            if (shoeDAO.addShoe(shoe)) {
                showAlert(Alert.AlertType.INFORMATION,
                        resources.getString("shoe.add.success.title"),
                        resources.getString("shoe.add.success.header"),
                        resources.getString("shoe.add.success.content"));
                loadShoes();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR,
                        resources.getString("shoe.add.error.title"),
                        resources.getString("shoe.add.error.header"),
                        resources.getString("shoe.add.error.content"));
            }
        }
    }

    @FXML
    private void handleUpdateButton() {
        Shoe selectedShoe = shoeTable.getSelectionModel().getSelectedItem();
        if (selectedShoe != null && validateInput()) {
            selectedShoe.setModel(modelField.getText());
            selectedShoe.setManufacturer(manufacturerComboBox.getSelectionModel().getSelectedItem());
            selectedShoe.setType(typeComboBox.getSelectionModel().getSelectedItem());

            try {
                double price = Double.parseDouble(priceField.getText());
                selectedShoe.setPrice(price);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR,
                        resources.getString("validation.error.title"),
                        resources.getString("validation.error.header"),
                        resources.getString("validation.price.invalid"));
                return;
            }

            selectedShoe.setDescription(descriptionArea.getText());

            if (shoeDAO.updateShoe(selectedShoe)) {
                showAlert(Alert.AlertType.INFORMATION,
                        resources.getString("shoe.update.success.title"),
                        resources.getString("shoe.update.success.header"),
                        resources.getString("shoe.update.success.content"));
                loadShoes();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR,
                        resources.getString("shoe.update.error.title"),
                        resources.getString("shoe.update.error.header"),
                        resources.getString("shoe.update.error.content"));
            }
        }
    }

    @FXML
    private void handleDeleteButton() {
        Shoe selectedShoe = shoeTable.getSelectionModel().getSelectedItem();
        if (selectedShoe != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(resources.getString("shoe.delete.confirm.title"));
            alert.setHeaderText(resources.getString("shoe.delete.confirm.header"));
            alert.setContentText(resources.getString("shoe.delete.confirm.content"));

            Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                if (shoeDAO.deleteShoe(selectedShoe.getShoeId())) {
                    showAlert(Alert.AlertType.INFORMATION,
                            resources.getString("shoe.delete.success.title"),
                            resources.getString("shoe.delete.success.header"),
                            resources.getString("shoe.delete.success.content"));
                    loadShoes();
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR,
                            resources.getString("shoe.delete.error.title"),
                            resources.getString("shoe.delete.error.header"),
                            resources.getString("shoe.delete.error.content"));
                }
            }
        }
    }

    @FXML
    private void handleClearButton() {
        clearFields();
    }

    @FXML
    private void handleVariantsButton() {
        Shoe selectedShoe = shoeTable.getSelectionModel().getSelectedItem();
        if (selectedShoe != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shoe_variant.fxml"));
                loader.setResources(resources);
                Scene scene = new Scene(loader.load());

                ShoeVariantController controller = loader.getController();
                controller.initData(selectedShoe);

                Stage variantStage = new Stage();
                variantStage.setTitle(resources.getString("shoevariant.title") + " - " + selectedShoe.getModel());
                variantStage.setScene(scene);
                variantStage.initModality(Modality.APPLICATION_MODAL);
                variantStage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR,
                        "Error",
                        "Error Opening Variants Window",
                        "Could not open the variants management window: " + e.getMessage());
            }
        }
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (modelField.getText() == null || modelField.getText().isEmpty()) {
            errorMessage.append(resources.getString("validation.model.empty")).append("\n");
        }

        if (manufacturerComboBox.getSelectionModel().getSelectedItem() == null) {
            errorMessage.append(resources.getString("validation.manufacturer.empty")).append("\n");
        }

        if (typeComboBox.getSelectionModel().getSelectedItem() == null) {
            errorMessage.append(resources.getString("validation.type.empty")).append("\n");
        }

        if (priceField.getText() == null || priceField.getText().isEmpty()) {
            errorMessage.append(resources.getString("validation.price.empty")).append("\n");
        } else {
            try {
                Double.parseDouble(priceField.getText());
            } catch (NumberFormatException e) {
                errorMessage.append(resources.getString("validation.price.invalid")).append("\n");
            }
        }

        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.ERROR,
                    resources.getString("validation.error.title"),
                    resources.getString("validation.error.header"),
                    errorMessage.toString());
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}