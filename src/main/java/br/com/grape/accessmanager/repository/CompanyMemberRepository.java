package br.com.grape.accessmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.grape.accessmanager.entity.CompanyMember;

@Repository
public interface CompanyMemberRepository extends JpaRepository<CompanyMember, Integer> {

    List<CompanyMember> findByCompanyId(Integer companyId);

    List<CompanyMember> findByUserId(Integer userId);

    Optional<CompanyMember> findByUserIdAndCompanyId(Integer userId, Integer companyId);
}