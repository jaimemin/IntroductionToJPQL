package com.tistory.jaimemin.jpql;

import javax.persistence.*;

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

            // TypeQuery: 반환 타입 명확
            TypedQuery<Member> query
                    = entityManager.createQuery("SELECT m FROM Member m", Member.class);
            TypedQuery<String> query2
                    = entityManager.createQuery("SELECT m.username from Member m", String.class);
            // Query: 반환 타입이 명확하지 않을 때
            Query query3 
                    = entityManager.createQuery("SELECT m.username, m.age from Member m");


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
