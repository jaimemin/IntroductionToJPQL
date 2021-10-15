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
            member.setMemberType(MemberType.ADMIN);

            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            String query = "SELECT m.username, 'HELLO', true FROM Member m " +
                    "WHERE m.memberType = :userType";
            List<Object[]> result = entityManager.createQuery(query)
                            .setParameter("userType", MemberType.ADMIN) // 하드코딩 시 패키지명 다 넣어야함
                            .getResultList();

            for (Object[] objects: result) {
                System.out.println("objects[0] = " + objects[0]);
                System.out.println("objects[1] = " + objects[1]);
                System.out.println("objects[2] = " + objects[2]);
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
