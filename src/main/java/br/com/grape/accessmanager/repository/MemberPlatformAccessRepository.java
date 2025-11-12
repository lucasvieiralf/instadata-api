package br.com.grape.accessmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.grape.accessmanager.entity.MemberPlatformAccess;
import br.com.grape.accessmanager.enums.PlatformAccessStatus;

@Repository
public interface MemberPlatformAccessRepository extends JpaRepository<MemberPlatformAccess, Integer> {

    Optional<MemberPlatformAccess> findByCompanyMemberIdAndPlatformId(Integer memberId, Integer platformId);
    
    Optional<MemberPlatformAccess> findByAccessToken(String accessToken);

    int countByCompanyMember_Company_IdAndPlatform_Id(Integer companyId, Integer platformId);
    
    int countByCompanyMember_Company_IdAndPlatform_IdAndStatus(
            Integer companyId,
            Integer platformId,
            PlatformAccessStatus status);

    List<MemberPlatformAccess> findByCompanyMember_Company_Id(Integer companyId);
    
    List<MemberPlatformAccess> findByCompanyMemberId(Integer companyMemberId);

}
