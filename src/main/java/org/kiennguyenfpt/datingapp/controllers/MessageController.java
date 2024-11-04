package org.kiennguyenfpt.datingapp.controllers;

import org.kiennguyenfpt.datingapp.dtos.requests.MessageRequest;
import org.kiennguyenfpt.datingapp.dtos.responses.MessageResponse;
import org.kiennguyenfpt.datingapp.entities.Match;
import org.kiennguyenfpt.datingapp.entities.Message;
import org.kiennguyenfpt.datingapp.repositories.UserRepository;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.services.MatchService;
import org.kiennguyenfpt.datingapp.services.MessageService;
import org.kiennguyenfpt.datingapp.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/messages")
@CrossOrigin
public class MessageController {
    private final MessageService messageService;
    private final MatchService matchService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate brokerMessagingTemplate;

    public MessageController(MessageService messageService, MatchService matchService, UserService userService, UserRepository userRepository, SimpMessagingTemplate brokerMessagingTemplate) {
        this.messageService = messageService;
        this.matchService = matchService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.brokerMessagingTemplate = brokerMessagingTemplate;
    }

    // Gửi tin nhắn
    @PostMapping("/send")
    public ResponseEntity<CommonResponse<MessageResponse>> sendMessage(@RequestBody MessageRequest messageRequest) {
        CommonResponse<MessageResponse> response = new CommonResponse<>();
        try {
            // Lấy thông tin user từ JWT token (Authentication)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();  // Username từ JWT
            Long senderId = userRepository.findByEmail(username).getUserId();  // Lấy senderId từ repository qua username (email)
            // Lấy thông tin người nhận từ match
            Long receiverId = matchService.getReceiverIdFromMatch(messageRequest.getMatchId(), senderId);

            // Gửi tin nhắn và lưu trong database
            Message message = messageService.sendMessage(
                    messageRequest.getMatchId(),
                    senderId,
                    receiverId,
                    messageRequest.getContent()
            );

            // Tạo đối tượng response trả về cho client
            MessageResponse messageResponse = new MessageResponse(
                    message.getMessageId(),
                    message.getMatch().getMatchId(),
                    message.getSender().getUserId(),
                    message.getReceiver().getUserId(),
                    message.getContent(),
                    message.getCreatedAt().toString(),  // Thời gian gửi tin nhắn
                    true  // Trạng thái tin nhắn gửi thành công
            );

            brokerMessagingTemplate.convertAndSend("/topic/messages/" + messageRequest.getMatchId(), messageResponse);
            System.out.println("Message sent to WebSocket topic: " + messageResponse);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Message sent successfully.");
            response.setData(messageResponse);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // Nếu có lỗi hợp lệ (bad request)
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error sending message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Lấy danh sách tin nhắn theo matchId
    @GetMapping("/match/{matchId}")
    public ResponseEntity<CommonResponse<List<MessageResponse>>> getMessages(@PathVariable Long matchId) {
        CommonResponse<List<MessageResponse>> response = new CommonResponse<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();  // Username từ JWT
            Long userId = userRepository.findByEmail(username).getUserId();

            // Kiểm tra xem user có thuộc về matchId này không
            Match match = matchService.getMatchById(matchId);
            if (!((match.getUser1().getUserId() == userId) || (match.getUser2().getUserId() == userId))) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setMessage("User does not have access to these messages.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Lấy danh sách tin nhắn theo matchId
            List<Message> messages = messageService.getMessages(matchId);

            // Chuyển danh sách tin nhắn thành danh sách MessageResponse
            List<MessageResponse> messageResponses = messages.stream().map(message -> new MessageResponse(
                    message.getMessageId(),
                    message.getMatch().getMatchId(),
                    message.getSender().getUserId(),
                    message.getReceiver().getUserId(),
                    message.getContent(),
                    message.getCreatedAt().toString(),  // Thời gian gửi tin nhắn
                    true  // Trạng thái tin nhắn đã nhận
            )).collect(Collectors.toList());

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Messages retrieved successfully.");
            response.setData(messageResponses);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error retrieving messages: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
