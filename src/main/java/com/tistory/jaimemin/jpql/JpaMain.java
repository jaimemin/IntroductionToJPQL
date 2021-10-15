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
            Member member1 = new Member();
            member1.setUsername("관리자1");

            entityManager.persist(member1);

            Member member2 = new Member();
            member2.setUsername("관리자2");

            entityManager.persist(member2);

            entityManager.flush();
            entityManager.clear();

            String query = "SELECT group_concat(m.username) FROM Member m";
            List<String> result = entityManager.createQuery(query, String.class)
                            .getResultList();

            // s = 관리자1,관리자2
            for (String s : result) {
                System.out.println("s = " + s);
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
