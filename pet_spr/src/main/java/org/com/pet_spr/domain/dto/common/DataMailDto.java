package org.com.pet_spr.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DataMailDto {

  private String to;

  private String subject;

  private String content;

  private Map<String, Object> properties;


  //chứa các cặp Key-Value mà bạn đã định nghĩa trong file HTML template của mình.
  //Key: Là tên biến bạn đặt trong template (ví dụ: name, otp, orderId).
  //Value: Là giá trị thực tế bạn muốn hiển thị cho người dùng.


  //<h1>Chào bạn, <span th:text="${userName}"></span>!</h1>
  //<p>Mã kích hoạt của bạn là: <strong th:text="${otpCode}"></strong></p>
  //<p>Email này có hiệu lực trong <span th:text="${expiryMin}"></span> phút.</p>

  //DataMailDto mailDto = new DataMailDto();
  //mailDto.setTo("customer@gmail.com");
  //mailDto.setSubject("Xác nhận đăng ký");
  //
  //Map<String, Object> props = new HashMap<>();
  //props.put("userName", "Nguyễn Văn A"); // Tương ứng ${userName}
  //props.put("otpCode", "123456");         // Tương ứng ${otpCode}
  //props.put("expiryMin", 10);             // Tương ứng ${expiryMin}
  //
  //mailDto.setProperties(props);

}