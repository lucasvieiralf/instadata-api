package br.com.grape.accessmanager.service;

import java.util.List;

import br.com.grape.accessmanager.dto.member.MemberDTOs.*;
import br.com.grape.accessmanager.entity.User;

public interface MemberService {
    MemberResponse inviteMember(InviteMemberRequest request, User adminUser);
    MemberResponse changeMemberRole(Integer memberId, UpdateMemberRoleRequest request, User adminUser);
    List<MemberResponse> listMembers(User adminUser);
    void removeMember(Integer memberId, User adminUser);
}