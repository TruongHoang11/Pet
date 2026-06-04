package org.com.pet_spr.constant;

public enum BookingStatus {

    //Chờ tiệm xác nhận lịch trống
    PENDING,

    // Đã xác nhận lịch hẹn
    CONFIRMED,

    //Đang tắm, cắt tỉa lông cho mèo
    IN_PROGRESS,

    //Đã làm xong và thanh toán hoàn tất
    COMPLETED,

    // Khách hủy hoặc không đến
    CANCELLED
}
