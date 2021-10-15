package com.tistory.jaimemin.jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory
                = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // 엔티티 프로젝션 시 모든 엔티티들이 영속성 컨텍스트로 관리됨
            List<Member> members
                    = entityManager.createQuery("SELECT m FROM Member m", Member.class)
                    .getResultList();
            List<Team> teams = entityManager.createQuery("SELECT m.team FROM Member m")
                    .getResultList();
            
            // update 되는 것을 확인할 수 있음
            Member foundMember = members.get(0);
            foundMember.setAge(20);

            // 임베디드 타입 프로젝션
            List<Address> addresses
                    = entityManager.createQuery("SELECT o.address FROM Order o", Address.class)
                    .getResultList();

            // 스칼라 타입 프로젝션
            List<Object[]> resultList = entityManager.createQuery("SELECT m.username, m.age FROM Member m")
                    .getResultList();

            // 1. Object[] 타입으로 조회
            Object[] result = resultList.get(0);

            System.out.println("result[0] = " + result[0]);
            System.out.println("result[1] = " + result[1]);

            // 2. new 명령어로 조회
            // 패키지명 길어지면 한계가 있는 것이 문제 -> QueryDSL로 해결 가능
            List<MemberDTO> memberDTOs
                    = entityManager.createQuery("SELECT new com.tistory.jaimemin.jpql.MemberDTO(m.username, m.age) FROM Member m", MemberDTO.class)
                    .getResultList();
            MemberDTO memberDTO = memberDTOs.get(0);
            System.out.println("username = " + memberDTO.getUsername());
            System.out.println("age = " + memberDTO.getAge());

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();

            e.printStackTrace();
        } finally {
            entityManager.close();
        }

        entityManagerFactory.close();
    }

}
