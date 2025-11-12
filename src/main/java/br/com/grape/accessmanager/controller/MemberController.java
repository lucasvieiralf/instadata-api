package br.com.grape.accessmanager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.grape.accessmanager.config.security.AppUserDetails;
import br.com.grape.accessmanager.dto.member.MemberDTOs.InviteMemberRequest;
import br.com.grape.accessmanager.dto.member.MemberDTOs.MemberResponse;
import br.com.grape.accessmanager.dto.member.MemberDTOs.UpdateMemberRoleRequest;
import br.com.grape.accessmanager.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/members")
@PreAuthorize("hasAuthority('MANAGE_USERS')")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * Lista todos os membros da empresa do admin logado.
     */
    @GetMapping
    public ResponseEntity<List<MemberResponse>> listMyCompanyMembers(
            @AuthenticationPrincipal AppUserDetails principal) {

        List<MemberResponse> members = memberService.listMembers(principal.getUserEntity());
        return ResponseEntity.ok(members);
    }

    /**
     * Convida um novo membro para a empresa.
     */
    @PostMapping("/invite")
    public ResponseEntity<MemberResponse> inviteMember(
            @AuthenticationPrincipal AppUserDetails principal,
            @Valid @RequestBody InviteMemberRequest request) {

        MemberResponse newMember = memberService.inviteMember(request, principal.getUserEntity());
        return ResponseEntity.status(201).body(newMember);
    }

    /**
     * Altera o papel (Role) de um membro.
     */
    @PutMapping("/{memberId}/role")
    public ResponseEntity<MemberResponse> changeMemberRole(
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable Integer memberId,
            @Valid @RequestBody UpdateMemberRoleRequest request) {

        MemberResponse updatedMember = memberService.changeMemberRole(memberId, request, principal.getUserEntity());
        return ResponseEntity.ok(updatedMember);
    }

    /**
     * Remove um membro da empresa.
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable Integer memberId) {

        memberService.removeMember(memberId, principal.getUserEntity());
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}