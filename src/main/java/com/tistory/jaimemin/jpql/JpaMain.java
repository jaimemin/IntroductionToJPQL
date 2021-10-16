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

            entityManager.flush();
            entityManager.clear();

            /**
             * select
             *         team0_.id as id1_3_0_,
             *         team0_.name as name2_3_0_ 
             *     from
             *         Team team0_
             *     where
             *         team0_.id=?
             */
            // 엔티티 자체를 파라미터로 넘길 수 있음
            String query = "SELECT m FROM Member m WHERE m = :member";
            Member foundMember = entityManager.createQuery(query, Member.class)
                            .setParameter("member", member1)
                            .getSingleResult();

            System.out.println("foundMember = " + foundMember);

            /**
             * select
             *         team0_.id as id1_3_0_,
             *         team0_.name as name2_3_0_
             *     from
             *         Team team0_
             *     where
             *         team0_.id=?
             */
            String foreignKeyQuery = "SELECT m FROM Member m WHERE m.team = :team";
            Member foundTeamMember = entityManager.createQuery(foreignKeyQuery, Member.class)
                            .setParameter("team", teamB)
                            .getSingleResult();

            System.out.println("foundTeamMember = " + foundTeamMember);

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
