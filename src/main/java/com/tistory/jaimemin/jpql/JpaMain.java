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
             *         member0_.id as id1_0_,
             *         member0_.age as age2_0_,
             *         member0_.memberType as memberty3_0_,
             *         member0_.TEAM_ID as team_id5_0_,
             *         member0_.username as username4_0_
             *     from
             *         Member member0_
             *     where
             *         member0_.username=?
             */
            Member foundMember = entityManager.createNamedQuery("Member.findByUsername", Member.class)
                            .setParameter("username", "회원1")
                            .getSingleResult();

            System.out.println("foundMember = " + foundMember);

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
