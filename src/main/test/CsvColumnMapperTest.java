import com.myflexbox.entity.Address;
import com.myflexbox.entity.User;
import com.myflexbox.mapper.CsvColumnMapper;
import com.myflexbox.mapper.CsvMapping;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CsvColumnMapperTest {

    @Test
    public void shouldMapCsvEntriesToUsersGivenValidData() {
        // given
        List<String[]> entries = Collections.singletonList(new String[]{"John", "Doe", "123 Main St", "12345", "USA"});
        List<CsvMapping> mappings = createMockMappingsForValidUser();

        CsvColumnMapper mapper = new CsvColumnMapper();

        // when
        List<User> result = mapper.mapCsvToUsers(entries, mappings);

        // then
        assertEquals(1, result.size());
        mappings.forEach(mapping -> verify(mapping, times(1)).applyToUser(any(User.class), anyString()));
    }

    @Test
    public void shouldReturnEmptyListGivenNullMappings() {
        // given
        List<String[]> entries = Collections.singletonList(new String[]{"John", "Doe", "123 Main St", "12345", "USA"});
        List<CsvMapping> mappings = Arrays.asList(null, null, null, null, null);

        CsvColumnMapper mapper = new CsvColumnMapper();

        // when
        List<User> result = mapper.mapCsvToUsers(entries, mappings);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListGivenInvalidCsvData() {
        // given
        List<String[]> entries = Collections.singletonList(new String[]{"John", null, "123 Main St", "12345", "USA"});
        List<CsvMapping> mappings = createMockMappings(5);

        CsvColumnMapper mapper = new CsvColumnMapper();

        // when
        List<User> result = mapper.mapCsvToUsers(entries, mappings);

        // then
        assertTrue(result.isEmpty());
    }

    private List<CsvMapping> createMockMappings(int count) {
        List<CsvMapping> mappings = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            CsvMapping mockMapping = mock(CsvMapping.class);
            when(mockMapping.getCsvColumnName()).thenReturn("MockColumn" + i);
            mappings.add(mockMapping);
        }
        return mappings;
    }

    private List<CsvMapping> createMockMappingsForValidUser() {

        // Mock mappings for First Name
        CsvMapping firstNameMapping = mock(CsvMapping.class);
        when(firstNameMapping.getCsvColumnName()).thenReturn("First");
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setFirstName("John");
            return null;
        }).when(firstNameMapping).applyToUser(any(User.class), eq("John"));

        // Mock mappings for Last Name
        CsvMapping lastNameMapping = mock(CsvMapping.class);
        when(lastNameMapping.getCsvColumnName()).thenReturn("Last");
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setLastName("Doe");
            return null;
        }).when(lastNameMapping).applyToUser(any(User.class), eq("Doe"));

        // Mock mappings for Address
        CsvMapping addressMapping = mock(CsvMapping.class);
        when(addressMapping.getCsvColumnName()).thenReturn("Address");
        doAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            address.setStreet("123 Main St");
            return null;
        }).when(addressMapping).applyToAddress(any(Address.class), eq("123 Main St"));

        // Mock mappings for ZIP
        CsvMapping zipMapping = mock(CsvMapping.class);
        when(zipMapping.getCsvColumnName()).thenReturn("ZIP");
        doAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            address.setPostcode("12345");
            return null;
        }).when(zipMapping).applyToAddress(any(Address.class), eq("12345"));

        // Mock mappings for Country
        CsvMapping countryMapping = mock(CsvMapping.class);
        when(countryMapping.getCsvColumnName()).thenReturn("Country");
        doAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            address.setCountry("USA");
            return null;
        }).when(countryMapping).applyToAddress(any(Address.class), eq("USA"));

        return new ArrayList<>(Arrays.asList(firstNameMapping, lastNameMapping, addressMapping, zipMapping, countryMapping));
    }
}
