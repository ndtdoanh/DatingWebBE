package org.kiennguyenfpt.datingapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private Long messageId;       // ID của tin nhắn đã lưu
    private Long matchId;         // ID của match
    private Long senderId;        // ID của người gửi
    private Long receiverId;      // ID của người nhận
    private String content;       // Nội dung tin nhắn
    private String timestamp;     // Thời gian tin nhắn được gửi
    private boolean isSent;       // Trạng thái gửi tin nhắn

    @Override
    public String toString() {
        return "MessageResponse{" +
                "messageId=" + messageId +
                ", matchId=" + matchId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", content='" + content + '\'' +
                '}';
    }
}
