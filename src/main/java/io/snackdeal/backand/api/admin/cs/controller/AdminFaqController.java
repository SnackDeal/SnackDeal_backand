package io.snackdeal.backand.api.admin.cs.controller;

import io.snackdeal.backand.api.admin.cs.dto.AdminFaqRequest;
import io.snackdeal.backand.api.admin.cs.dto.AdminFaqResponse;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.domain.cs.service.AdminFaqService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/cs/faq")
@RequiredArgsConstructor
public class AdminFaqController {

    private final AdminFaqService adminFaqService;

    @GetMapping
    public CommonResponse<List<AdminFaqResponse>> list(@RequestParam(required = false) QnaType type) {
        return CommonResponse.success(adminFaqService.findList(type));
    }

    @GetMapping("/{id}")
    public CommonResponse<AdminFaqResponse> findById(@PathVariable Long id) {
        return CommonResponse.success(adminFaqService.findById(id));
    }

    @PostMapping
    public CommonResponse<AdminFaqResponse> save(@Valid @RequestBody AdminFaqRequest request) {
        return CommonResponse.created(adminFaqService.save(request));
    }

    @PutMapping("/{id}")
    public CommonResponse<AdminFaqResponse> update(@PathVariable Long id, @Valid @RequestBody AdminFaqRequest request) {
        return CommonResponse.success(adminFaqService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public CommonResponse<Void> delete(@PathVariable Long id) {
        adminFaqService.delete(id);
        return CommonResponse.success(null);
    }
}