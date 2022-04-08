package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired MemberRepository memberRepository;

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
        Page<Member> result = memberRepository.findByAge(10, pageRequest);

        assertThat(result.getContent().size()).isEqualTo(3);
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("member9");
        assertThat(result.getTotalPages()).isEqualTo(4);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.hasNext()).isTrue();
    }

}