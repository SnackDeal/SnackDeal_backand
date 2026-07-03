package io.snackdeal.backand.api.admin.cs.controller;

import io.snackdeal.backand.domain.cs.service.AdminQnaService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/qna")
@RequiredArgsConstructor
public class AdminQnaController {

    private final AdminQnaService adminQnaService;

    @GetMapping
    public CommonResponse<Object> list() {
        return CommonResponse.success(adminQnaService.findList());
    }

    @GetMapping("/{id}")
    public CommonResponse<Object> findById(@PathVariable Long id) {
        return CommonResponse.success(adminQnaService.findById(id));
    }

    @PostMapping("/{id}/answer")
    public CommonResponse<Object> answer(@PathVariable Long id, @RequestBody Object request) {
        return CommonResponse.success(adminQnaService.answer(id, request));
    }
}
