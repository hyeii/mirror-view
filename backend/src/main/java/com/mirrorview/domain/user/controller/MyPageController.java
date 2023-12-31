package com.mirrorview.domain.user.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mirrorview.domain.essay.dto.EssayCreateDto;
import com.mirrorview.domain.essay.dto.EssayDetailDto;
import com.mirrorview.domain.essay.dto.EssayDto;
import com.mirrorview.domain.essay.dto.EssayUpdateDto;
import com.mirrorview.domain.essay.service.EssayDetailService;
import com.mirrorview.domain.essay.service.EssayService;
import com.mirrorview.domain.feedback.dto.FeedbackDto;
import com.mirrorview.domain.feedback.dto.FeedbackListDto;
import com.mirrorview.domain.feedback.dto.FeedbackSaveDto;
import com.mirrorview.domain.feedback.service.FeedbackService;
import com.mirrorview.domain.user.dto.ChangePasswordDto;
import com.mirrorview.domain.user.dto.MemberProfileDto;
import com.mirrorview.domain.user.service.MemberProfileService;
import com.mirrorview.global.auth.security.CustomMemberDetails;
import com.mirrorview.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

	private final MemberProfileService memberProfileService;
	private final FeedbackService feedbackService;
	private final EssayService essayService;
	private final EssayDetailService essayDetailService;

	@GetMapping
	public ResponseEntity<?> getInfo(@AuthenticationPrincipal CustomMemberDetails member) {
		String userId = member.getUsername();
		MemberProfileDto memberProfileDto = memberProfileService.findByUserId(userId);
		return BaseResponse.okWithData(HttpStatus.OK, "회원정보 조회 성공", memberProfileDto);
	}

	@PostMapping("/password")
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto,
		@AuthenticationPrincipal CustomMemberDetails member) {
		try {
			memberProfileService.changePassword(dto, member.getUsername());
			return BaseResponse.ok(HttpStatus.OK, "비밀번호 변경 완료");
		} catch (Exception e) {
			return BaseResponse.fail(e.getMessage(), 400);
		}
	}

	@PatchMapping("/image")
	public ResponseEntity<?> updateImage(String userId, String photo) {
		memberProfileService.updatePhoto(userId, photo);
		return BaseResponse.ok(HttpStatus.OK, "프로필 사진 수정 완료");
	}

	@PostMapping("/nickname")
	public ResponseEntity<?> updateNickname(@AuthenticationPrincipal CustomMemberDetails member, String nickname) {
		String userId = member.getUsername();
		try {
			memberProfileService.updateNickname(userId, nickname);
		} catch (IllegalArgumentException e) {
			return BaseResponse.fail(e.getMessage(), 400);
		}
		return BaseResponse.ok(HttpStatus.OK, "닉네임 변경 완료");
	}

	@PostMapping("/feedbacks/save") // 피드백 저장하기
	public ResponseEntity<?> saveFeedback(@RequestBody FeedbackSaveDto dto,
		@AuthenticationPrincipal CustomMemberDetails member) {
		try {
			feedbackService.saveFeedback(dto, member.getUsername());
			return BaseResponse.ok(HttpStatus.OK, "피드백 저장 성공");
		} catch (Exception e) {
			return BaseResponse.fail(e.getMessage(), 400);
		}
	}

	@GetMapping("/feedbacks") // 전체 피드백 목록
	public ResponseEntity<?> getFeedbackList(@AuthenticationPrincipal CustomMemberDetails member, @PageableDefault(size = 5)
		Pageable pageable) {
		Page<FeedbackDto> list = feedbackService.findFeedbackByUserId(member.getUsername(), pageable);
		return BaseResponse.okWithData(HttpStatus.OK, "전체 피드백 목록 조회 성공", list);
	}

	@GetMapping("/feedbacks/{roomId}") // 피드백 방마다 피드백들
	public ResponseEntity<?> getFeedback(@PathVariable("roomId") String roomId) {
		List<FeedbackDto> list = feedbackService.findFeedbackByRoomId(roomId);
		return BaseResponse.okWithData(HttpStatus.OK, "피드백 조회 성공", list);
	}

	@DeleteMapping("/feedbacks/{roomId}")
	public ResponseEntity<?> deleteFeedback(@PathVariable("roomId") String roomId) {
		try {
			feedbackService.deleteByRoomId(roomId);
			return BaseResponse.ok(HttpStatus.OK, "삭제 성공");
		} catch (Exception e) {
			return BaseResponse.fail("삭제 실패", 400);
		}
	}

	@PutMapping("/essays") // 자소서안의 문항들 수정/삭제와 같은 자소서 문항에 대한 변경
	public ResponseEntity<?> updateEssays(@RequestBody EssayUpdateDto essays, @AuthenticationPrincipal
		CustomMemberDetails member) {
		try {
			essayDetailService.updateEssayDetails(essays, member.getUsername());
		} catch (Exception e) {
			return BaseResponse.fail(e.getMessage(), 400);
		}
		return BaseResponse.ok(HttpStatus.OK, "수정 완료");
	}

	@PostMapping("/essays") // 자소서 추가 완료
	public ResponseEntity<?> createEssays(@RequestBody EssayCreateDto essays, @AuthenticationPrincipal
		CustomMemberDetails member) {
		try {
			essayService.insertEssayAndEssayDetails(essays, member.getUsername());
			return BaseResponse.ok(HttpStatus.OK, "자소서 저장 성공!");
		} catch (Exception e) {
			if (e instanceof NullPointerException) {
				return BaseResponse.fail("형식에 맞는 json이 아닙니다", 400);
			}
			return BaseResponse.fail(e.getMessage(), 400);
		}
	}

	@GetMapping("/essays") // 전체 자소서 목록 불러오기
	public ResponseEntity<?> showEssays(@AuthenticationPrincipal
		CustomMemberDetails member, Pageable pageable) {
		Page<EssayDto> list = essayService.findEssayByUserId(member.getUsername(), pageable);
		return BaseResponse.okWithData(HttpStatus.OK, "에세이 목록 불러오기 성공", list);
	}

	@GetMapping("/essays/{essay-id}")  // 자소서 상세 보기
	public ResponseEntity<?> detailEssays(@PathVariable("essay-id") Long essayId) {
		List<EssayDetailDto> list = essayDetailService.findEssayByEssayId(essayId);
		return BaseResponse.okWithData(HttpStatus.OK, "에세이 불러오기 성공", list);
	}

	@DeleteMapping("/essays/{essay-id}") // 자소서 삭제
	public ResponseEntity<?> deleteEssays(@PathVariable("essay-id") Long essayId) {
		try {
			essayService.deleteByEssayId(essayId);
			return BaseResponse.ok(HttpStatus.OK, "삭제 성공");
		} catch (Exception e) {
			return BaseResponse.fail(e.getMessage(), 400);
		}
	}

	@PatchMapping("/email")
	public ResponseEntity<?> changeEmail(@AuthenticationPrincipal
		CustomMemberDetails member, String email) {
		String userId = member.getUsername();
		memberProfileService.changeEmail(email, userId);
		return BaseResponse.ok(HttpStatus.OK, "이메일 변경 성공");
	}
}
