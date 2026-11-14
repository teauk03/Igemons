package com.igemons.adapter.in.web.pets;

import lombok.RequiredArgsConstructor;
import com.igemons.adapter.in.web.auth.CurrentUser;
import com.igemons.application.pets.PetService;
import com.igemons.domain.pets.PetId;
import com.igemons.domain.users.UserId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping
    public ResponseEntity<PetResponse> register(@CurrentUser UserId me, @RequestBody PetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(PetResponse.from(petService.register(me, request.toCommand())));
    }

    @GetMapping
    public List<PetResponse> listMine(@CurrentUser UserId me) {
        return petService.listMine(me).stream().map(PetResponse::from).toList();
    }

    @GetMapping("/{petId}")
    public PetResponse get(@CurrentUser UserId me, @PathVariable long petId) {
        return PetResponse.from(petService.get(me, PetId.of(petId)));
    }

    /** 프로필 전체 교체 (보내지 않은 선택 필드는 비워짐) */
    @PutMapping("/{petId}")
    public PetResponse update(@CurrentUser UserId me, @PathVariable long petId,
                              @RequestBody PetRequest request) {
        return PetResponse.from(petService.update(me, PetId.of(petId), request.toCommand()));
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> delete(@CurrentUser UserId me, @PathVariable long petId) {
        petService.delete(me, PetId.of(petId));
        return ResponseEntity.noContent().build();
    }
}
