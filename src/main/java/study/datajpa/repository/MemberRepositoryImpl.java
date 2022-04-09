package study.datajpa.repository;

import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

public record MemberRepositoryImpl(EntityManager em) implements MemberCustomRepository {

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
