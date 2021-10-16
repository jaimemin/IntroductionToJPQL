package com.tistory.jaimemin.jpql;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory
                = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            Team teamA = new Team();
            teamA.setName("팀A");

            entityManager.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");

            entityManager.persist(teamB);

            Team teamC = new Team();
            teamC.setName("팀C");

            entityManager.persist(teamC);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);

            entityManager.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);

            entityManager.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);

            entityManager.persist(member3);

            Member member4 = new Member();
            member4.setUsername("회원4");

            entityManager.persist(member4);

//            entityManager.flush();
//            entityManager.clear();

            // 전부 20살로 업데이트
            // persist 했으므로 FLUSH는 된 상황
            int resultCount = entityManager.createQuery("UPDATE Member m SET m.age = 20")
                    .executeUpdate();

            System.out.println("resultCount = " + resultCount);

            // clear 안했으므로 영속성 컨텍스트에는 반영 안되어있음
            System.out.println("member1.getAge() = " + member1.getAge());
            System.out.println("member2.getAge() = " + member1.getAge());
            System.out.println("member3.getAge() = " + member1.getAge());
            // 따라서, 영속성 컨텍스트 초기화해주는 것이 중요
            entityManager.clear();

            Member foundMember = entityManager.find(Member.class, member1.getId());

            System.out.println("foundMember.getAge() = " + foundMember.getAge());

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
