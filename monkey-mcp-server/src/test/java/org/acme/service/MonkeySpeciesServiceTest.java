package org.acme.service;

import org.acme.model.MonkeySpecies;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class MonkeySpeciesServiceTest {

    @Inject
    MonkeySpeciesService service;

    @Test
    void testGetAllSpecies() {
        List<MonkeySpecies> result = service.getAllSpecies();
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.size() > 0);
    }

    @Test
    void testGetSpeciesDetailsWithValidName() {
        Optional<MonkeySpecies> result = service.getSpeciesDetails("Proboscis Monkey");
        
        assertTrue(result.isPresent());
        assertEquals("Proboscis Monkey", result.get().speciesName());
        assertTrue(result.get().accessed() > 0); // Should be incremented
    }

    @Test
    void testGetSpeciesDetailsWithInvalidName() {
        Optional<MonkeySpecies> result = service.getSpeciesDetails("Nonexistent Monkey");
        
        assertFalse(result.isPresent());
    }

    @Test
    void testGetSpeciesDetailsWithNullName() {
        Optional<MonkeySpecies> result = service.getSpeciesDetails(null);
        
        assertFalse(result.isPresent());
    }

    @Test
    void testGetSpeciesDetailsWithEmptyName() {
        Optional<MonkeySpecies> result = service.getSpeciesDetails("   ");
        
        assertFalse(result.isPresent());
    }

    @Test
    void testGetRandomSpecies() {
        Optional<MonkeySpecies> result = service.getRandomSpecies();
        
        assertTrue(result.isPresent());
        assertNotNull(result.get().speciesName());
    }

    @Test
    void testSpeciesExists() {
        assertTrue(service.speciesExists("Proboscis Monkey"));
        assertTrue(service.speciesExists("proboscis monkey")); // Case insensitive
        assertFalse(service.speciesExists("Nonexistent Monkey"));
        assertFalse(service.speciesExists(null));
        assertFalse(service.speciesExists(""));
    }

    @Test
    void testGetSpeciesCount() {
        int count = service.getSpeciesCount();
        
        assertTrue(count > 0);
    }

    @Test
    void testGetAllSpeciesNames() {
        List<String> names = service.getAllSpeciesNames();
        
        assertNotNull(names);
        assertFalse(names.isEmpty());
        assertTrue(names.contains("Proboscis Monkey"));
    }
}
