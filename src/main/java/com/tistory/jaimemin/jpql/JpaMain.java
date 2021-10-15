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
            Team team = new Team();
            team.setName("team");
            entityManager.persist(team);

            Member member = new Member();
            member.setUsername("member");
            member.setAge(10);
            member.setTeam(team);

            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            /**
             * select
             *                 (select
             *                     avg(cast(member2_.age as double))
             *             from
             *                 Member member2_) as col_0_0_
             *         from
             *             Member member0_
             *         inner join
             *             Team team1_
             *                 on (
             *                     member0_.username=team1_.name
             *                 )
             */
            String query = "SELECT (SELECT AVG(m1.age) FROM Member m1) as avgAge FROM Member m JOIN Team t on m.username = t.name";
            List<Double> ages = entityManager.createQuery(query, Double.class)
                            .getResultList();

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
