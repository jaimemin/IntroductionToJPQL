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
            for (int i = 0; i < 100; i++) {
                Member member = new Member();
                member.setUsername("member" + i);
                member.setAge(i);

                entityManager.persist(member);
            }

            entityManager.flush();
            entityManager.clear();

            /**
             * SELECT
             *         m
             *     FROM
             *         Member m
             *     ORDER BY
             *         m.age DESC
             *
             *         select
             *              member0_.id as id1_0_,
             *              *member0_.age as age2_0_,
             *              *member0_.TEAM_ID as team_id4_0_,
             *              *member0_.username as username3_0_
             *         from
             *              Member member0_
             *         order by
             *              member0_.age DESC limit ?offset ?
             */
            List<Member> members
                    = entityManager.createQuery("SELECT m FROM Member m ORDER BY m.age DESC", Member.class)
                    .setFirstResult(1)
                    .setMaxResults(10)
                    .getResultList();

            System.out.println("members.size = " + members.size());

            for (Member m : members) {
                System.out.println("m.getUsername() = " + m.getUsername());
                System.out.println("m.getAge() = " + m.getAge());
                System.out.println();
            }

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
