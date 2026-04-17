package com.it3030.smartcampus.member4.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.it3030.smartcampus.member4.model.UserAccount;

public interface UserRepository extends JpaRepository<UserAccount, Integer> {

	@Override
	Optional<UserAccount> findById(Integer id);

	Optional<UserAccount> findByUserId(String userId);

	Optional<UserAccount> findByEmail(String email);

	Optional<UserAccount> findByUserIdAndEmail(String userId, String email);

	Optional<UserAccount> findTopByUserIdStartingWithOrderByUserIdDesc(String prefix);

	List<UserAccount> findBySuspiciousTrueOrderByEmailAsc();
}