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

            // QueryDSL로는 쉽게 작성 가능
            // String add는 힘듬
            String query = "SELECT " +
                    "CASE WHEN m.age <= 10 then '학생요금' " +
                    "WHEN m.age >= 60 then '경로요금' " +
                    "ELSE '일반요금' " +
                    "END " +
                    "FROM Member m";
            List<String> result = entityManager.createQuery(query, String.class)
                    .getResultList();

            for (String s : result) {
                System.out.println("s = " + s);
            }

            // coalsece
            String query2 = "SELECT COALESCE(m.username, '이름 없는 회원') as username FROM Member m";
            List<String> result2 = entityManager.createQuery(query2, String.class)
                            .getResultList();

            for (String s : result2) {
                System.out.println("s2 = " + s);
            }

            // NULLIF
            String query3 = "SELECT NULLIF(m.username, 'member') FROM Member m";
            List<String> result3 = entityManager.createQuery(query3, String.class)
                            .getResultList();

            for (String s : result3) {
                System.out.println("s3 = " + s);
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
