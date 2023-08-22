package com.myflexbox.views;

import com.myflexbox.entity.User;
import com.myflexbox.mapper.CsvColumnMapper;
import com.myflexbox.mapper.CsvMapping;
import com.myflexbox.repository.UserRepository;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Vaadin view class to handle the import of CSV files into a grid and the mapping of CSV columns.
 * The view allows for uploading CSV files, mapping columns to data fields, and saving the mapped data.
 */
@Route(value = "grid-csv", layout = MainLayout.class)
@CssImport("./themes/csvimporter/components/button-styles.css")
@Component
@UIScope
public class GridCsvImport extends VerticalLayout {

    private final UserRepository userRepository;
    private final CsvColumnMapper csvColumnMapper = new CsvColumnMapper();

    /**
     * Constructor for the GridCsvImport view.
     *
     * @param userRepository The user repository used to save the imported data.
     */
    @Autowired
    public GridCsvImport(UserRepository userRepository) {
        this.userRepository = userRepository;
        initializeComponents(); // Method call to initialize UI components
    }

    /**
     * Initializes the UI components including upload, grid, and buttons.
     */
    private void initializeComponents() {
        MemoryBuffer buffer = new MemoryBuffer(); // Buffer to hold uploaded CSV file content
        Upload upload = new Upload(buffer); // Upload component to handle CSV file uploads
        Grid<String[]> grid = new Grid<>(); // Grid component to display CSV content

        configureUpload(upload, grid, buffer); // Configuring the upload component
        configureGrid(grid);

        Button resetButton = createButton("Reset Combo boxes values", click -> csvColumnMapper.resetMapping());
        Button cancelButton = createButton("Clear everything", click -> {
            csvColumnMapper.resetMapping();
            clearGrid(grid);
            upload.clearFileList();
        });
        Button saveButton = createButton("Save", click -> saveData(grid));
        Button clearGridData = createButton("Remove grid data", click -> clearGrid(grid));

        Div buttonDiv = new Div();
        buttonDiv.addClassName("button-div");
        buttonDiv.add(saveButton, cancelButton, resetButton, clearGridData);

        // Adding the upload component, grid, and buttons to the layout
        add(upload, grid, buttonDiv);
    }

    /**
     * Configures the upload component for accepting CSV files and handling successful uploads and rejections.
     *
     * @param upload The upload component.
     * @param grid   The grid component to display the CSV content.
     * @param buffer The buffer to hold the uploaded CSV content.
     */
    private void configureUpload(Upload upload, Grid<String[]> grid, MemoryBuffer buffer) {
        upload.setAcceptedFileTypes(".csv");
        upload.setDropAllowed(true);
        // Listener to handle successful file uploads
        upload.addSucceededListener(event -> csvColumnMapper.loadCsvToGrid(grid, buffer));
        // Listener to handle rejected files
        upload.addFileRejectedListener(fileRejectedEvent -> CustomNotification.show("File rejected: " + fileRejectedEvent.getErrorMessage()));
    }

    /**
     * Configures the grid component by clearing existing columns.
     *
     * @param grid The grid component to be configured.
     */
    private void configureGrid(Grid<String[]> grid) {
        grid.removeAllColumns();
    }

    /**
     * Creates a button with the given text and click listener.
     *
     * @param text     The text displayed on the button.
     * @param listener The click event listener for the button.
     * @return The created button.
     */
    private Button createButton(String text, ComponentEventListener<ClickEvent<Button>> listener) {
        Button button = new Button(text, listener);
        button.addClassName("my-button");
        return button;
    }

    /**
     * Saves the mapped data from the grid by validating, mapping, and persisting the data to the repository.
     *
     * @param grid The grid component containing the CSV data.
     */
    private void saveData(Grid<String[]> grid) {
        List<String[]> entries = csvColumnMapper.retrieveEntries(grid);
        if (!csvColumnMapper.validateCsvAndMapping(entries, csvColumnMapper.getColumnMappingComboBoxes())) {
            return;
        }

        // Extract the selected CsvMapping from each ComboBox
        List<CsvMapping> mappings = new ArrayList<>();
        for (ComboBox<CsvMapping> comboBox : csvColumnMapper.getColumnMappingComboBoxes()) {
            mappings.add(comboBox.getValue());
        }

        List<User> users = csvColumnMapper.mapCsvToUsers(entries, mappings); // Pass the extracted mappings

        if (users.isEmpty()) {
            CustomNotification.show("No valid data to import.");
            return;
        }

        saveUsers(users);
    }

    /**
     * Saves the list of User objects to the repository.
     *
     * @param users The list of User objects to be saved.
     */
    private void saveUsers(List<User> users) {
        try {
            userRepository.saveAll(users);
            CustomNotification.show("Data saved successfully!", "success");
        } catch (Exception e) {
            CustomNotification.show("An error occurred while saving the data: " + e.getMessage(), "error");
        }
    }

    /**
     * Clears the selected items and columns from the grid.
     *
     * @param grid The grid component to be cleared.
     */
    private void clearGrid(Grid<String[]> grid) {
        grid.removeAllColumns(); // Clear the columns
        grid.setItems(new ArrayList<>()); // Clear the items
    }
}
