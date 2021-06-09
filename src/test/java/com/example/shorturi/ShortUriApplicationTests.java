package com.example.shorturi;

import com.example.shorturi.controller.ShortUriManager;
import com.example.shorturi.repository.AssociatedUriRepository;
import com.example.shorturi.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ShortUriApplicationTests {

    private static ShortUriManager shortUriManager;

    @BeforeAll
    public static void initializationOfManager(AssociatedUriRepository associatedUriRepository, UserRepository userRepository){
        shortUriManager = ShortUriManager.getInstance(associatedUriRepository, userRepository);
    }

    @Test
    public void testCreationOfShortUri(){
        // ARRANGE
        String expectedShortUri = "sho.rt/ff295cdb";

        // ACT
        String actualShortUri = shortUriManager.createShortUri("https://example.com");

        // ASSERT
        assertThat(actualShortUri).isEqualTo(expectedShortUri);
    }

}
