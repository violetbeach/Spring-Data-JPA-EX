package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).orElse(new Member("default"));

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("member", 10, null);
        Member m2 = new Member("member", 20, null);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("member", 15);
        entityManager.flush();
        entityManager.clear();

        assertThat(result.get(0).getAge()).isEqualTo(20);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("memberA", 10, null);
        Member m2 = new Member("memberB", 20, null);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("memberA", 10);

        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findMemberByUsername() {
        Member m1 = new Member("memberA", 10, null);
        Member m2 = new Member("memberA", 20, null);
        memberRepository.save(m1);
        memberRepository.save(m2);

        Member result = memberRepository.findMemberByUsername("memberA");
        System.out.println(result);
    }

    @Test
    public void paging() {
        for(int i = 0; i<10; i++) {
            memberRepository.save(new Member("member" + i, 10, null));
        }

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Slice<Member> result = memberRepository.findByAge(10, pageRequest);

        assertThat(result.getContent().size()).isEqualTo(3);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("member9");
        assertThat(result.isFirst()).isTrue();
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    public void bulkAgePlusAll() {
        memberRepository.save(new Member("member1", 10, null));
        memberRepository.save(new Member("member1", 10, null));

        int updated = memberRepository.bulkAgePlus(5);
        entityManager.flush();
        
        List<Member> member = memberRepository.findUser("member1", 10);

        assertThat(updated).isEqualTo(2);
    }

    @Test
    public void findMemberLazy() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        List<Member> members = memberRepository.findAll();

    }

    @Test
    public void queryHint() {
        Member member1 = new Member("member1", 10, null);
        memberRepository.save(member1);
        entityManager.flush();
        entityManager.clear();

        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");
        entityManager.flush();
    }

    @Test
    public void lock() {
        Member member1 = new Member("member1", 10, null);
        memberRepository.save(member1);
        entityManager.flush();
        entityManager.clear();

        List<Member> findMember = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

}