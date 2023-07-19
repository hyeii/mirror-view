package com.mirrorview.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mirrorview.domain.user.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {


}
