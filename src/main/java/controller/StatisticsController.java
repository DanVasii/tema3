package controller;

import dao.InventoryDAO;
import dao.ShoeDAO;
import dao.StoreDAO;
import model.ChartData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import util.LanguageManager;

import java.util.Map;
import java.util.ResourceBundle;

public class StatisticsController {

    @FXML
    private TabPane statisticsTabPane;

    @FXML
    private Tab storeTab;

    @FXML
    private Tab typeTab;

    @FXML
    private Tab manufacturerTab;

    @FXML
    private Tab colorTab;

    @FXML
    private Tab outOfStockTab;

    @FXML
    private BarChart<String, Number> storeBarChart;

    @FXML
    private PieChart typePieChart;

    @FXML
    private PieChart manufacturerPieChart;

    @FXML
    private PieChart colorPieChart;

    @FXML
    private BarChart<String, Number> outOfStockBarChart;

    @FXML
    private Button refreshButton;

    @FXML
    private ComboBox<String> chartTypeComboBox;

    @FXML
    private Label totalItemsLabel;

    private ResourceBundle resources;
    private StoreDAO storeDAO;
    private ShoeDAO shoeDAO;
    private InventoryDAO inventoryDAO;

    public void initialize() {
        this.resources = LanguageManager.getResourceBundle();
        storeDAO = new StoreDAO();
        shoeDAO = new ShoeDAO();
        inventoryDAO = new InventoryDAO();

        // Inițializare ComboBox pentru tipul de grafic
        chartTypeComboBox.setItems(FXCollections.observableArrayList("Bar Chart", "Pie Chart"));
        chartTypeComboBox.getSelectionModel().selectFirst();
        chartTypeComboBox.setOnAction(event -> updateCharts());

        // Inițializare butoane
        refreshButton.setOnAction(event -> updateCharts());

        // Încărcare date inițiale
        updateCharts();
    }

    private void updateCharts() {
        updateStoreChart();
        updateTypeChart();
        updateManufacturerChart();
        updateColorChart();
        updateOutOfStockChart();

        // Actualizare număr total produse
        int totalItems = inventoryDAO.getTotalInventoryCount();
        totalItemsLabel.setText(resources.getString("statistics.total") + ": " + totalItems);
    }

    private void updateStoreChart() {
        storeBarChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(resources.getString("statistics.bystore"));

        Map<String, Integer> storeData = inventoryDAO.getInventoryCountByStore();

        for (Map.Entry<String, Integer> entry : storeData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        storeBarChart.getData().add(series);
    }

    private void updateTypeChart() {
        typePieChart.getData().clear();

        Map<String, Integer> typeData = shoeDAO.getShoeCountByType();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : typeData.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        typePieChart.setData(pieChartData);
        typePieChart.setTitle(resources.getString("statistics.bytype"));
    }

    private void updateManufacturerChart() {
        manufacturerPieChart.getData().clear();

        Map<String, Integer> manufacturerData = shoeDAO.getShoeCountByManufacturer();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : manufacturerData.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        manufacturerPieChart.setData(pieChartData);
        manufacturerPieChart.setTitle(resources.getString("statistics.bymanufacturer"));
    }

    private void updateColorChart() {
        colorPieChart.getData().clear();

        Map<String, Integer> colorData = inventoryDAO.getInventoryCountByColor();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : colorData.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        colorPieChart.setData(pieChartData);
        colorPieChart.setTitle(resources.getString("statistics.bycolor"));
    }

    private void updateOutOfStockChart() {
        outOfStockBarChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(resources.getString("statistics.outofstock"));

        Map<String, Integer> outOfStockData = inventoryDAO.getOutOfStockCountByStore();

        for (Map.Entry<String, Integer> entry : outOfStockData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        outOfStockBarChart.getData().add(series);
    }
}