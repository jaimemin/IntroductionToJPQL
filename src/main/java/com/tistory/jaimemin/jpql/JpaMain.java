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
                    = entityManager.createQuery("SELECT m FROM Member m WHERE m.id = 10", Member.class);
            // query.getSingleResult()

            Member result = query.getSingleResult();
            System.out.println("result = " + result);
            // -> 결과 없으면 NoResultException (Spring Data JPA에서는 이상 없음)
            // -> 결과 두개 이상이면 NonUniqueResultException

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
