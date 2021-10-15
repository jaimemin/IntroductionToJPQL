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

            String query = "SELECT m FROM Member m INNER JOIN m.team t";
            List<Member> members = entityManager.createQuery(query, Member.class)
                            .getResultList();

            String query2 = "SELECT m FROM Member m LEFT OUTER JOIN m.team t";
            List<Member> leftMembers = entityManager.createQuery(query2, Member.class)
                    .getResultList();

            // cross join
            String query3 = "SELECT m FROM Member m, Team t WHERE m.username = t.name";
            List<Member> thetaMembers = entityManager.createQuery(query3, Member.class)
                    .getResultList();

            // 조인 대상 필터링
            String query4 = "SELECT m FROM Member m LEFT JOIN m.team t on t.name = 'teamA'";
            List<Member> filteringMembers = entityManager.createQuery(query4, Member.class)
                            .getResultList();

            // 연관관계가 없는 엔티티 외부 조인
            String query5 = "SELECT m FROM Member m LEFT JOIN Team t on m.username = t.name";
            List<Member> notRelatedMembers = entityManager.createQuery(query5, Member.class)
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
