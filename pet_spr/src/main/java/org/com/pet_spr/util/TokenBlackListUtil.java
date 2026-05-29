package org.com.pet_spr.util;

import io.netty.util.Constant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.com.pet_spr.constant.CommonConstant;
import org.com.pet_spr.domain.entity.TokenBlackList;
import org.com.pet_spr.repository.TokenBlackListRepository;


public class TokenBlackListUtil {


    public static void addTokenToBlackList(String token, String reason, TokenBlackListRepository tokenBlackListRepository){
        TokenBlackList tokenBlackList = tokenBlackListRepository.findByToken(token);
        if(tokenBlackList == null){
            TokenBlackList newTokenBlackList = TokenBlackList.builder()
                    .token(token)
                    .reason(reason)
                    .tokenType(CommonConstant.BEARER_TOKEN)
                    .build();
            tokenBlackListRepository.save(newTokenBlackList);
        }
    }

    public static boolean isTokenBlackList(String token, TokenBlackListRepository tokenBlackListRepository){
        TokenBlackList tokenBlackList = tokenBlackListRepository.findByToken(token);
        if(tokenBlackList != null){
            return true;
        }
        return false;
    }

    public static String getClientIP(HttpServletRequest request) {
        //request.getRemoteAddr(): Nếu có Proxy, hàm này thường chỉ trả về IP của Proxy đó.
        //X-Forwarded-For: Đây là một HTTP Header tiêu chuẩn.
        // Khi Proxy nhận request, nó sẽ đính kèm IP thật của người dùng vào Header này.
        String ip = request.getHeader("X-Forwarded-For");

        //Nếu Header này không tồn tại hoặc có giá trị là "unknown" (một số Proxy set như vậy),
        // nghĩa là người dùng đang kết nối trực tiếp hoặc Proxy không hỗ trợ Header này.
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            //Lúc này, ta mới dùng request.getRemoteAddr() làm phương án dự phòng (fallback)
            ip = request.getRemoteAddr();

        } else {
            //Header X-Forwarded-For có thể chứa một chuỗi các IP
            // nếu request đi qua nhiều Proxy (ví dụ: IP_Client, Proxy1, Proxy2).

            //IP của người dùng gốc luôn đứng đầu tiên trong danh sách này.
            // Do đó, ta dùng split(",") để lấy phần tử đầu tiên.
            ip = ip.split(",")[0];
        }
        return ip;
    }
}
