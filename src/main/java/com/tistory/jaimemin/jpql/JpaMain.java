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

            // LAZY이므로 일단 Member만 (Team X)
            String query = "SELECT m FROM Member m";
            List<Member> members = entityManager.createQuery(query, Member.class)
                            .getResultList();

            for (Member m : members) {
                // LAZY이므로 getTeam()이 호출될 때마다 아래의 쿼리가 호출됨
                /**
                 * select
                 *         team0_.id as id1_3_0_,
                 *         team0_.name as name2_3_0_
                 *     from
                 *         Team team0_
                 *     where
                 *         team0_.id=?
                 */
                System.out.println("member = " + m.getUsername() + ", " + m.getTeam());
                /**
                 * 회원1, 팀A(SQL)
                 * 회원2, 팀A(PersistenceContext의 1차 캐시에서 불러옴, 즉 별도 쿼리 X)
                 * 회원3, 팀B(다른 팀이므로 SQL 또 날림)
                 *
                 * 회원 100명일 경우 최악의 경우 별도 쿼리 100번 (성능 이슈)
                 * -> N + 1 문제 발생 [회원을 가져오기 위한 쿼리 1번 + 별도 쿼리 N번]
                 * 즉시 로딩을 하던 Lazy 로딩을 하던 전부 발생 => 이는 fetch join으로 해결해야함
                 */
            }

            /**
             * select
             *             member0_.id as id1_0_0_,
             *             team1_.id as id1_3_1_,
             *             member0_.age as age2_0_0_,
             *             member0_.memberType as memberty3_0_0_,
             *             member0_.TEAM_ID as team_id5_0_0_,
             *             member0_.username as username4_0_0_,
             *             team1_.name as name2_3_1_ 
             *         from
             *             Member member0_ 
             *         inner join
             *             Team team1_ 
             *                 on member0_.TEAM_ID=team1_.id
             *                 
             *   JOIN을 통해 한번에 다 가져옴
             */
            String fetchQuery = "SELECT m FROM Member m JOIN FETCH m.team";
            List<Member> fetchMembers = entityManager.createQuery(fetchQuery, Member.class)
                            .getResultList();

            for (Member m : fetchMembers) {
                // 루프를 돌 떄 프록시가 아닌 진짜 데이터가 존재
                // 따라서, 지연로딩 없이 깔끔하게 쿼리 1번으로 필요한 데이터 들고 옴
                System.out.println("m = " + m.getUsername() + ", " + m.getTeam());
            }

            /**
             * select
             *             team0_.id as id1_3_0_,
             *             members1_.id as id1_0_1_,
             *             team0_.name as name2_3_0_,
             *             members1_.age as age2_0_1_,
             *             members1_.memberType as memberty3_0_1_,
             *             members1_.TEAM_ID as team_id5_0_1_,
             *             members1_.username as username4_0_1_,
             *             members1_.TEAM_ID as team_id5_0_0__,
             *             members1_.id as id1_0_0__
             *         from
             *             Team team0_
             *         inner join
             *             Member members1_
             *                 on team0_.id=members1_.TEAM_ID
             */
            // 1:다 join은 뻥튀기될 가능성이 있음
            String teamQuery = "SELECT t FROM Team t JOIN FETCH t.members";
            List<Team> teams = entityManager.createQuery(teamQuery, Team.class)
                            .getResultList();

            /**
             * team = 팀A|members = 2
             * m = Member{id=4, username='회원1', age=0, team=com.tistory.jaimemin.jpql.Team@57540fd0, memberType=null}
             * m = Member{id=5, username='회원2', age=0, team=com.tistory.jaimemin.jpql.Team@57540fd0, memberType=null}
             * team = 팀A|members = 2
             * m = Member{id=4, username='회원1', age=0, team=com.tistory.jaimemin.jpql.Team@57540fd0, memberType=null}
             * m = Member{id=5, username='회원2', age=0, team=com.tistory.jaimemin.jpql.Team@57540fd0, memberType=null}
             * team = 팀B|members = 1
             * m = Member{id=6, username='회원3', age=0, team=com.tistory.jaimemin.jpql.Team@3a627c80, memberType=null}
             */
            // 왜 teamA가 두 번 출력될까?
            for (Team team : teams) {
                System.out.println("team = " + team.getName() + "|members = " + team.getMembers().size());

                for (Member m : team.getMembers()) {
                    System.out.println("m = " + m);
                }
            }

            /**
             * select
             *             distinct team0_.id as id1_3_0_,
             *             members1_.id as id1_0_1_,
             *             team0_.name as name2_3_0_,
             *             members1_.age as age2_0_1_,
             *             members1_.memberType as memberty3_0_1_,
             *             members1_.TEAM_ID as team_id5_0_1_,
             *             members1_.username as username4_0_1_,
             *             members1_.TEAM_ID as team_id5_0_0__,
             *             members1_.id as id1_0_0__
             *         from
             *             Team team0_
             *         inner join
             *             Member members1_
             *                 on team0_.id=members1_.TEAM_ID
             */
            // SQL의 DISTINCT 키워드 만으로는 distinct 안됨
            // JPA에서는 같은 식별자를 가진 Team 엔티티 제거
            String distinctQuery = "SELECT DISTINCT t FROM Team t JOIN FETCH t.members";
            List<Team> distinctTeams = entityManager.createQuery(distinctQuery, Team.class)
                            .getResultList();

            /**
             * team = 팀A|members = 2
             * m = Member{id=4, username='회원1', age=0, team=com.tistory.jaimemin.jpql.Team@57540fd0, memberType=null}
             * m = Member{id=5, username='회원2', age=0, team=com.tistory.jaimemin.jpql.Team@57540fd0, memberType=null}
             * team = 팀B|members = 1
             * m = Member{id=6, username='회원3', age=0, team=com.tistory.jaimemin.jpql.Team@3a627c80, memberType=null}
             */
            for (Team team : distinctTeams) {
                System.out.println("team = " + team.getName() + "|members = " + team.getMembers().size());

                for (Member m : team.getMembers()) {
                    System.out.println("m = " + m);
                }
            }

            /**
             * select
             *             team0_.id as id1_3_,
             *             team0_.name as name2_3_
             *         from
             *             Team team0_
             *         inner join
             *             Member members1_
             *                 on team0_.id=members1_.TEAM_ID
             */
            // LAZY이므로 Team만 불러옴 (Member x)
            String withoutFetchQuery = "SELECT t FROM Team t join t.members m";
            List<Team> withoutFetchTeams = entityManager.createQuery(withoutFetchQuery, Team.class)
                            .getResultList();

            // fetch join의 경우 페이징 API 적용이 안됨
            String pagingQuery = "SELECT t FROM Team t";
            List<Team> pagingTeams = entityManager.createQuery(pagingQuery, Team.class)
                    .setFirstResult(0)
                    .setMaxResults(2)
                    .getResultList();

            for (Team team : pagingTeams) {
                System.out.println("team = " + team.getName() + "|members = " + team.getMembers().size());

                // LAZY여서 성능이 안 나옴
                // @BatchSize 어노테이션을 통해 LAZY 로딩 시 BatchSize만큼 넘기기 때문에
                // 쿼리 3개가 아닌 2개만 날림 (TeamA와 TeamB에 대해)
                // [N + 1] 문제 해결법 중 하나
                for (Member m : team.getMembers()) {
                    System.out.println("m = " + m);
                }
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
