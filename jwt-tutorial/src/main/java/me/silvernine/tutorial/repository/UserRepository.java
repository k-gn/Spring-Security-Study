package me.silvernine.tutorial.repository;

import me.silvernine.tutorial.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // @EntityGraph : JPA가 어떤 엔티티를 불러올 때 이 엔티티와 관계된 엔티티를 불러올 것인지에 대한 정보를 제공
    // 기본적으로는 FECTH 정책을 사용하고 있으며 이것은 설정한 엔티티 속성에는 EAGER 패치 나머지는 LAZY 패치를 하는 정책
    // 연관 관계의 Fetch 모드를 유연하게 설정할 수 있다.
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByUsername(String username);

    // findOne은  Returns a single entity 의 의미이고, With는 @EntityGraph 어노테이션과 관계가 있다. authorities도 함께 Fetch 하라는 의미.
}