package com.example.retailai.chat;

import com.example.retailai.assistant.RetailAssistantService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatController {
    private final RetailAssistantService assistant;

    public ChatController(RetailAssistantService assistant) {
        this.assistant = assistant;
    }

    public record ChatRequest(@NotBlank String message) {}
    public record ChatResponse(String answer) {}

    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return new ChatResponse(assistant.answer(request.message()));
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@Valid @RequestBody ChatRequest request) {
        return assistant.stream(request.message())
                .map(token -> "data: " + token.replace("\n", "\\n") + "\n\n");
    }
}
