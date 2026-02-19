package com.codillas.loyaltycard.controller;

import com.codillas.loyaltycard.controller.dto.CardDto;
import com.codillas.loyaltycard.mapper.CardMapper;
import com.codillas.loyaltycard.service.CardService;
import com.codillas.loyaltycard.service.model.Card;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final CardMapper cardMapper;

    @Secured("ADMIN")
    @GetMapping("/{cardId}")
    public ResponseEntity<CardDto> getCard(@PathVariable UUID cardId) {

        Card card = cardService.getCard(cardId);

        CardDto cardDto = cardMapper.toDto(card);

        return ResponseEntity.ok().body(cardDto);
    }

    @Secured("ADMIN")
    @PutMapping("/{cardId}/activate")
    public ResponseEntity<CardDto> activateCard(@PathVariable UUID cardId) {

        Card card = cardService.activateCard(cardId);

        CardDto cardDto = cardMapper.toDto(card);

        return ResponseEntity.ok().body(cardDto);
    }

    @Secured("ADMIN")
    @PutMapping("/{cardId}/block")
    public ResponseEntity<CardDto> blockCard(@PathVariable UUID cardId) {

        Card card = cardService.blockCard(cardId);

        CardDto cardDto = cardMapper.toDto(card);

        return ResponseEntity.ok().body(cardDto);
    }
}
