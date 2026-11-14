package com.igemons.adapter.in.web.petlog;

import lombok.RequiredArgsConstructor;
import com.igemons.adapter.in.web.auth.CurrentUser;
import com.igemons.application.petlog.PetLogService;
import com.igemons.domain.petlog.PetLogId;
import com.igemons.domain.petlog.PetLogType;
import com.igemons.domain.pets.PetId;
import com.igemons.domain.users.UserId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets/{petId}/logs")
@RequiredArgsConstructor
public class PetLogController {

    private final PetLogService petLogService;

    @PostMapping
    public ResponseEntity<PetLogResponse> record(@CurrentUser UserId me, @PathVariable long petId,
                                                 @RequestBody PetLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(PetLogResponse.from(petLogService.record(me, PetId.of(petId), request.toCommand())));
    }

    @GetMapping
    public List<PetLogResponse> list(@CurrentUser UserId me, @PathVariable long petId,
                                     @RequestParam(required = false) PetLogType type) {
        return petLogService.list(me, PetId.of(petId), type).stream().map(PetLogResponse::from).toList();
    }

    @GetMapping("/{logId}")
    public PetLogResponse get(@CurrentUser UserId me, @PathVariable long petId, @PathVariable long logId) {
        return PetLogResponse.from(petLogService.get(me, PetId.of(petId), PetLogId.of(logId)));
    }

    /** 기록 수정 (type은 변경 불가) */
    @PutMapping("/{logId}")
    public PetLogResponse amend(@CurrentUser UserId me, @PathVariable long petId, @PathVariable long logId,
                                @RequestBody PetLogRequest request) {
        return PetLogResponse.from(
            petLogService.amend(me, PetId.of(petId), PetLogId.of(logId), request.toCommand()));
    }

    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> delete(@CurrentUser UserId me, @PathVariable long petId, @PathVariable long logId) {
        petLogService.delete(me, PetId.of(petId), PetLogId.of(logId));
        return ResponseEntity.noContent().build();
    }
}
