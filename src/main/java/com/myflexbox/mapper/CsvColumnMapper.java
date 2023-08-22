package com.myflexbox.mapper;

import com.myflexbox.entity.Address;
import com.myflexbox.entity.User;
import com.myflexbox.views.CustomNotification;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.util.SharedUtil;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class responsible for handling CSV column mapping tasks.
 * This includes loading the CSV into a grid, retrieving entries, validating data and mappings, and transforming the CSV rows into user entities.
 */
public class CsvColumnMapper {

    private boolean updatingMappings = false;

    @Getter
    private final List<ComboBox<CsvMapping>> columnMappingComboBoxes = new ArrayList<>();

    // Define the mappings for CSV columns
    private final List<CsvMapping> allMappings = List.of(
            new CsvMapping("First", User::setFirstName, null),
            new CsvMapping("Last", User::setLastName, null),
            new CsvMapping("Address", null, Address::setStreet),
            new CsvMapping("ZIP", null, Address::setPostcode),
            new CsvMapping("Country", null, Address::setCountry)
    );
    private final List<CsvMapping> selectedMappings = new ArrayList<>();

    /**
     * Loads the uploaded CSV data into the provided grid.
     *
     * @param grid The grid component to display the CSV content.
     * @param buffer The buffer containing the uploaded CSV content.
     */
    public void loadCsvToGrid(Grid<String[]> grid, MemoryBuffer buffer) {
        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(buffer.getInputStream(), StandardCharsets.UTF_8))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {

            List<String[]> entries = reader.readAll();
            String[] headers = entries.get(0);

            grid.removeAllColumns();
            for (int i = 0; i < headers.length; i++) {
                final int columnIndex = i;
                String header = headers[i];
                SharedUtil.camelCaseToHumanFriendly(header);
                grid.addColumn(str -> str[columnIndex])
                        .setHeader(buildColumnHeader());
            }
            grid.setItems(entries.subList(1, entries.size()));
        } catch (IOException | CsvException e) {
            grid.addColumn(nop -> "Unable to load CSV: " + e.getMessage())
                    .setHeader("Failed to import CSV file");
        }
    }

    /**
     * Retrieves the entries (rows) from the provided grid.
     *
     * @param grid The grid component containing the CSV data.
     * @return A list of string arrays representing the CSV rows.
     */
    public List<String[]> retrieveEntries(Grid<String[]> grid) {
        List<String[]> entries = new ArrayList<>();
        grid.getDataProvider().fetch(new Query<>()).forEach(entries::add);
        return entries;
    }

    /**
     * Validates the CSV data and its mappings.
     *
     * @param entries CSV rows to be validated.
     * @param comboBoxes The column mappings for the CSV data.
     * @return true if validation is successful, false otherwise.
     */
    public boolean validateCsvAndMapping(List<String[]> entries, List<ComboBox<CsvMapping>> comboBoxes) {
        if (entries.isEmpty() || entries.get(0).length != comboBoxes.size()) {
            CustomNotification.show("Invalid CSV structure!");
            return false;
        }

        boolean allMapped = comboBoxes.stream()
                .map(ComboBox::getValue)
                .allMatch(Objects::nonNull);
        if (!allMapped) {
            CustomNotification.show("Please complete the mapping!");
            return false;
        }
        return true;
    }

    /**
     * Transforms the CSV rows into user entities based on the provided mappings.
     *
     * @param entries CSV rows to be transformed.
     * @param mappings The column mappings for the CSV data.
     * @return A list of user entities.
     */
    public List<User> mapCsvToUsers(List<String[]> entries, List<CsvMapping> mappings) {
        List<User> users = new ArrayList<>();
        for (String[] row : entries) {
            User user = new User();
            Address address = new Address();

            for (int i = 0; i < row.length; i++) {
                CsvMapping mapping = mappings.get(i);
                String value = row[i];
                if (mapping != null && !mapping.getCsvColumnName().equals("Ignore")) {
                    mapping.applyToUser(user, value);
                    mapping.applyToAddress(address, value);
                }
            }

            if (isUserPopulated(user, address)) {
                user.setAddress(address);
                users.add(user);
            }
        }
        return users;
    }

    /**
     * Builds a column header containing a ComboBox for CSV column mapping.
     *
     * @return a Div containing a ComboBox for selecting the mapping of a CSV column.
     */
    private Div buildColumnHeader() {
        Div container = new Div();
        ComboBox<CsvMapping> comboBox = new ComboBox<>();
        createColumnMappingComboBoxes(comboBox); // Initialize ComboBox
        container.add(comboBox);
        return container;
    }

    /**
     * Resets the column mapping by clearing the ComboBox selections.
     */
    public void resetMapping() {
        columnMappingComboBoxes.forEach(HasValue::clear);
    }

    /**
     * Initializes a ComboBox for CSV column mapping.
     * Sets up the available options, default value, and change listener for a given ComboBox.
     *
     * @param comboBox the ComboBox to initialize.
     */
    private void createColumnMappingComboBoxes(ComboBox<CsvMapping> comboBox) {
        comboBox.setItems(allMappings);
        comboBox.setValue(new CsvMapping("Ignore", null, null)); // Add a unique "Ignore" instance

        comboBox.addValueChangeListener(event -> {
            if (event.getOldValue() != null && !"Ignore".equals(event.getOldValue().getCsvColumnName())) {
                selectedMappings.remove(event.getOldValue());
            }
            if (event.getValue() != null && !"Ignore".equals(event.getValue().getCsvColumnName())) {
                selectedMappings.add(event.getValue());
            }
            updateAvailableMappings();
        });

        columnMappingComboBoxes.add(comboBox);
    }

    /**
     * Updates the available mappings for all ComboBoxes, ensuring that a mapping
     * is not used in more than one column. If a mapping is already selected, it's excluded
     * from the available options in other ComboBoxes.
     */
    private void updateAvailableMappings() {
        if (updatingMappings) {
            return;
        }
        updatingMappings = true;

        // Create a new list of mappings excluding selected mappings (excluding "Ignore")
        List<CsvMapping> availableMappings = new ArrayList<>(allMappings);
        selectedMappings.stream()
                .filter(mapping -> !"Ignore".equals(mapping.getCsvColumnName()))
                .forEach(availableMappings::remove);

        // Always include exactly one "Ignore" instance
        CsvMapping ignoreMapping = new CsvMapping("Ignore", null, null);
        availableMappings.add(ignoreMapping);

        for (ComboBox<CsvMapping> comboBox : columnMappingComboBoxes) {
            CsvMapping selectedValue = comboBox.getValue();

            // Check if selectedValue is null or if it's the "Ignore" mapping
            if (selectedValue == null || "Ignore".equals(selectedValue.getCsvColumnName())) {
                selectedValue = ignoreMapping; // Use the single "Ignore" instance
            }

            comboBox.setItems(availableMappings);
            comboBox.setValue(selectedValue);
        }

        updatingMappings = false;
    }

    /**
     * Checks if a user and associated address have any populated fields.
     *
     * @param user the User object to check.
     * @param address the Address object to check.
     * @return true if either user or address have any fields populated, false otherwise.
     */
    private boolean isUserPopulated(User user, Address address) {
        return user.getFirstName() != null ||
                user.getLastName() != null ||
                address.getStreet() != null ||
                address.getPostcode() != null ||
                address.getCountry() != null;
    }
}
