package io.snackdeal.backand.domain.cs.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class CsService {

    public Object findNoticeList() {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object findNoticeById(Long id) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object findFaqList() {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object findMyQnaList(String email) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object createQna(String email, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object findQnaById(String email, Long id) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object updateQna(String email, Long id, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public void deleteQna(String email, Long id) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object askChatbot(Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }
}
