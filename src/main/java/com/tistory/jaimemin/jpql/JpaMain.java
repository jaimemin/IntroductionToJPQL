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
            Team team = new Team();
            team.setName("team");

            entityManager.persist(team);

            Member member1 = new Member();
            member1.setUsername("관리자1");
            member1.setTeam(team);

            entityManager.persist(member1);

            Member member2 = new Member();
            member2.setUsername("관리자2");
            member2.setTeam(team);

            entityManager.persist(member2);

            entityManager.flush();
            entityManager.clear();

            /**
             * select
             *             member0_.username as col_0_0_
             *         from
             *             Member member0_
             */
            // 상태 필드: username 이후로 탐색할 곳이 없음 (더 이상 탐색 X)
            String query = "SELECT m.username FROM Member m";
            List<String> result = entityManager.createQuery(query, String.class)
                            .getResultList();

            // s = 관리자1,관리자2
            for (String s : result) {
                System.out.println("s = " + s);
            }

            /**
             * select
             *             team1_.id as id1_3_,
             *             team1_.name as name2_3_
             *         from
             *             Member member0_
             *         inner join
             *             Team team1_
             *                 on member0_.TEAM_ID=team1_.id
             */
            // 단일 값 연관 경로 (@ManyToOne, @OneToOne)
            // 묵시적 INNER JOIN 발생
            // team 이후 name으로도 접근 가능 (name은 상태 필드)
            String query2 = "SELECT m.team FROM Member m";
            List<Team> teams = entityManager.createQuery(query2, Team.class)
                            .getResultList();

            for (Team t : teams) {
                System.out.println("team = " + t);
            }

            /**
             * select
             *             members1_.id as id1_0_,
             *             members1_.age as age2_0_,
             *             members1_.memberType as memberty3_0_,
             *             members1_.TEAM_ID as team_id5_0_,
             *             members1_.username as username4_0_
             *         from
             *             Team team0_
             *         inner join
             *             Member members1_
             *                 on team0_.id=members1_.TEAM_ID
             */
            // 컬렉션 값 연관 경로 (@OneToMany, @ManyToMany)
            // 묵시적 INNER JOIN 발생
            // 추가 탐색 X
            String query3 = "SELECT t.members FROM Team t";
            Collection members = entityManager.createQuery(query3, Collection.class)
                            .getResultList();

            System.out.println("members = " + members);

            /**
             * select
             *             (select
             *                 count(members1_.TEAM_ID)
             *             from
             *                 Member members1_
             *             where
             *                 team0_.id=members1_.TEAM_ID) as col_0_0_
             *         from
             *             Team team0_
             */
            // membersSize = 2
            // 탐색 X, 리스트의 메서드
            String query4 = "SELECT t.members.size FROM Team t";
            Integer membersSize = entityManager.createQuery(query4, Integer.class)
                            .getSingleResult();

            System.out.println("membersSize = " + membersSize);

            /**
             * select
             *             members1_.username as col_0_0_
             *         from
             *             Team team0_
             *         inner join
             *             Member members1_
             *                 on team0_.id=members1_.TEAM_ID
             */
            // FROM 절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능
            String query5 = "SELECT m.username FROM Team t join t.members m";
            List<String> usernames = entityManager.createQuery(query5, String.class)
                            .getResultList();

            System.out.println("usernames = " + usernames);

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
