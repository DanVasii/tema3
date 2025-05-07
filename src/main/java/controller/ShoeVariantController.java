package controller;

import dao.ColorDAO;
import dao.ShoeVariantDAO;
import model.Color;
import model.Shoe;
import model.ShoeVariant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.LanguageManager;
import util.Observable;
import util.Observer;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class ShoeVariantController implements Observer {

    @FXML
    private Label shoeModelLabel;

    @FXML
    private TableView<ShoeVariant> variantTable;

    @FXML
    private TableColumn<ShoeVariant, Integer> idColumn;

    @FXML
    private TableColumn<ShoeVariant, String> colorColumn;

    @FXML
    private TableColumn<ShoeVariant, String> imagePathColumn;

    @FXML
    private ComboBox<Color> colorComboBox;

    @FXML
    private TextField imagePathField;

    @FXML
    private Button browseButton;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button closeButton;

    private Shoe currentShoe;
    private ShoeVariantDAO variantDAO;
    private ColorDAO colorDAO;
    private ObservableList<ShoeVariant> variantList;
    private ResourceBundle resources;

    public void initialize() {

        this.resources = LanguageManager.getResourceBundle();
        variantDAO = new ShoeVariantDAO();
        colorDAO = new ColorDAO();
        variantList = FXCollections.observableArrayList();

        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("variantId"));

        colorColumn.setCellValueFactory(cellData -> {
            ShoeVariant variant = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(
                    () -> variant.getColor() != null ? variant.getColor().getName() : ""
            );
        });

        imagePathColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

        // Add a listener for selection
        variantTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showVariantDetails(newSelection);
                        updateButton.setDisable(false);
                        deleteButton.setDisable(false);
                    } else {
                        clearFields();
                        updateButton.setDisable(true);
                        deleteButton.setDisable(true);
                    }
                });

        // Disable update and delete buttons until user selects a variant
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        // Initialize button actions
        browseButton.setOnAction(event -> handleBrowseButton());
        closeButton.setOnAction(event -> handleCloseButton());

        // Update UI text based on current language
        updateUIText();
    }

    private void updateUIText() {
        browseButton.setText(resources.getString("button.browse"));
        addButton.setText(resources.getString("button.add"));
        updateButton.setText(resources.getString("button.update"));
        deleteButton.setText(resources.getString("button.delete"));
        clearButton.setText(resources.getString("button.clear"));
        closeButton.setText(resources.getString("button.close"));

        // Update table column headers and labels
        idColumn.setText(resources.getString("inventory.id"));
        colorColumn.setText(resources.getString("inventory.color"));
        imagePathColumn.setText(resources.getString("inventory.image.path"));
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof Locale) {
            // Update resources when language changes
            resources = LanguageManager.getResourceBundle();

            // Update UI texts
            updateUIText();
        }
    }

    // Metoda pentru a inițializa datele controlerului cu încălțămintea selectată
    public void initData(Shoe shoe) {
        this.currentShoe = shoe;
        shoeModelLabel.setText(shoe.getModel());

        // Încărcăm culorile disponibile
        loadColors();

        // Încărcăm variantele existente pentru această încălțăminte
        loadVariants();
    }

    private void loadColors() {
        List<Color> colors = colorDAO.getAllColors();
        colorComboBox.setItems(FXCollections.observableArrayList(colors));
    }

    private void loadVariants() {
        if (currentShoe != null) {
            variantList.clear();
            List<ShoeVariant> variants = variantDAO.getVariantsByShoeId(currentShoe.getShoeId());
            variantList.addAll(variants);
            variantTable.setItems(variantList);
        }
    }

    private void showVariantDetails(ShoeVariant variant) {
        colorComboBox.getSelectionModel().select(variant.getColor());
        imagePathField.setText(variant.getImagePath());
    }

    private void clearFields() {
        colorComboBox.getSelectionModel().clearSelection();
        imagePathField.clear();
        variantTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    @FXML
    private void handleBrowseButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(browseButton.getScene().getWindow());
        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleAddButton() {
        if (validateInput()) {
            ShoeVariant variant = new ShoeVariant();
            variant.setShoe(currentShoe);
            variant.setColor(colorComboBox.getSelectionModel().getSelectedItem());
            variant.setImagePath(imagePathField.getText());

            if (variantDAO.addVariant(variant)) {
                showAlert(Alert.AlertType.INFORMATION,
                        "Success",
                        "Variant Added",
                        "The variant has been successfully added.");
                loadVariants();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR,
                        "Error",
                        "Error Adding Variant",
                        "There was an error adding the variant.");
            }
        }
    }

    @FXML
    private void handleUpdateButton() {
        ShoeVariant selectedVariant = variantTable.getSelectionModel().getSelectedItem();
        if (selectedVariant != null && validateInput()) {
            selectedVariant.setColor(colorComboBox.getSelectionModel().getSelectedItem());
            selectedVariant.setImagePath(imagePathField.getText());

            if (variantDAO.updateVariant(selectedVariant)) {
                showAlert(Alert.AlertType.INFORMATION,
                        "Success",
                        "Variant Updated",
                        "The variant has been successfully updated.");
                loadVariants();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR,
                        "Error",
                        "Error Updating Variant",
                        "There was an error updating the variant.");
            }
        }
    }

    @FXML
    private void handleDeleteButton() {
        ShoeVariant selectedVariant = variantTable.getSelectionModel().getSelectedItem();
        if (selectedVariant != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("Are you sure you want to delete this variant? This action cannot be undone.");

            Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                if (variantDAO.deleteVariant(selectedVariant.getVariantId())) {
                    showAlert(Alert.AlertType.INFORMATION,
                            "Success",
                            "Variant Deleted",
                            "The variant has been successfully deleted.");
                    loadVariants();
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR,
                            "Error",
                            "Error Deleting Variant",
                            "There was an error deleting the variant.");
                }
            }
        }
    }

    @FXML
    private void handleClearButton() {
        clearFields();
    }

    @FXML
    private void handleCloseButton() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (colorComboBox.getSelectionModel().getSelectedItem() == null) {
            errorMessage.append("Color must be selected.\n");
        }

        if (imagePathField.getText() == null || imagePathField.getText().isEmpty()) {
            errorMessage.append("Image path cannot be empty.\n");
        }

        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.ERROR,
                    "Input Error",
                    "Please correct the following errors:",
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