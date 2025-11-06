package com.openclassrooms.starterjwt.payload.response;

import org.junit.jupiter.api.Test;

public class MessageResponseTest {

    @Test
    public void testGetMessage() {
        // Given
        String message = "Test message";
        MessageResponse messageResponse = new MessageResponse(message);

        // When
        String result = messageResponse.getMessage();

        // Then
        assert (result.equals(message));
    }

    @Test
    public void testSetMessage() {
        // Given
        String initialMessage = "Initial message";
        MessageResponse messageResponse = new MessageResponse(initialMessage);
        String newMessage = "New message";

        // When
        messageResponse.setMessage(newMessage);
        String result = messageResponse.getMessage();

        // Then
        assert (result.equals(newMessage));
    }
}
