package io.snackdeal.backand.global.util;

public class PhoneUtils {

    private PhoneUtils() {}

    /**
     * 하이픈 없는 번호를 하이픈 포함 형식으로 변환
     * 이미 하이픈이 있으면 그대로 반환.
     * 010-XXXX-XXXX / 02-XXX-XXXX / 031-XXX-XXXX 등 일반 패턴 지원.
     */
    public static String format(String phone) {
        if (phone == null) return null;
        String digits = phone.replaceAll("-", "");
        // 010/011/016/017/018/019 (11자리)
        if (digits.matches("01[016789]\\d{8}")) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 7) + "-" + digits.substring(7);
        }
        // 지역번호 2자리 (02, 9~10자리)
        if (digits.matches("02\\d{7,8}")) {
            int mid = digits.length() == 9 ? 5 : 6;
            return digits.substring(0, 2) + "-" + digits.substring(2, mid) + "-" + digits.substring(mid);
        }
        // 지역번호 3자리 (031~099, 10~11자리)
        if (digits.matches("0[3-9]\\d{8,9}")) {
            int mid = digits.length() == 10 ? 6 : 7;
            return digits.substring(0, 3) + "-" + digits.substring(3, mid) + "-" + digits.substring(mid);
        }
        return phone; // 알 수 없는 형식은 그대로
    }
}
