package com.mirrorview.domain.chatroom.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import com.mirrorview.domain.chatroom.domain.ChatRoom;
import com.mirrorview.domain.chatroom.domain.ChatUser;
import com.mirrorview.domain.chatroom.repository.ChatRepository;
import com.mirrorview.domain.chatroom.repository.ChatUserRepository;
import com.mirrorview.global.auth.security.CustomMemberDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatUserService {

	private final ChatUserRepository chatUserRepository;
	private final ChatRepository chatRepository;

	public void findUserInRedis(String userId, String userNickname){
		Optional<ChatUser> byUserId = chatUserRepository.findById(userId);
		if(!byUserId.isPresent())
			addUserToRedis(userId, userNickname);
	}

	public Set<ChatRoom> findFavoriteRoomsByUserId(String userId) {
		Optional<ChatUser> optionalChatUser = chatUserRepository.findById(userId);
		if(!optionalChatUser.isPresent())
			return Collections.emptySet();

		ChatUser user = optionalChatUser.get();
		Set<ChatRoom> favoriteRooms = user.getFavoriteChatRoomIds()
			.stream()
			.map(chatRepository::findById)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toSet());

		return favoriteRooms;
	}

	public void addChatRoomToFavorites(String userId, String roomId) {
		Optional<ChatUser> chatUser = chatUserRepository.findById(userId);
		if(chatUser.isPresent()) {
			ChatUser chatUser1 = chatUser.get();
			Set<String> favoriteChatRoomIds = chatUser1.getFavoriteChatRoomIds();

			// roomId가 이미 favoriteChatRoomIds에 존재하는지 확인
			if (favoriteChatRoomIds.contains(roomId)) {
				throw new RuntimeException("이미 즐겨찾기에 등록된 채팅방입니다.");
			}

			favoriteChatRoomIds.add(roomId);
			chatUserRepository.save(chatUser1);
		}
	}

	public void removeChatRoomFromFavorites(String userId, String roomId) {
		Optional<ChatUser> chatUser = chatUserRepository.findById(userId);
		if(chatUser.isPresent()) {
			ChatUser chatUser1 = chatUser.get();
			Set<String> favoriteChatRoomIds = chatUser1.getFavoriteChatRoomIds();

			// roomId가 이미 favoriteChatRoomIds에 존재하는지 확인
			if (!favoriteChatRoomIds.contains(roomId)) {
				throw new RuntimeException("이미 즐겨찾기에서 삭제된 채팅방입니다.");
			}

			favoriteChatRoomIds.remove(roomId);
			chatUserRepository.save(chatUser1);
		}
	}

	public ChatUser addUserToRedis(String userId, String userNickname){
		ChatUser chatUser = ChatUser.builder()
			.userId(userId)
			.nickname(userNickname)
			.favoriteChatRoomIds(new HashSet<>())
			.build();
		chatUserRepository.save(chatUser);
		return chatUser ;
	}
}
