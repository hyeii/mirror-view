package com.mirrorview.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mirrorview.domain.user.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByUserId(String userId);

	Member findByUserId(String userId);

	boolean existsByNickname(String nickname);

	Optional<Member> findByEmail(String email);

	Optional<Member> findByEmailAndUserId(String email, String userId);

	List<Member> findByUserIdContaining(String userId);
}