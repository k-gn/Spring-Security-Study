package me.silvernine.tutorial.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    // @JsonProperty : 객체의 JSON 변환시 key의 이름을 개발자가 원하는대로 설정 + 접근 제어 가능
    // AUTO, READ_ONLY, WRITE_ONLY, READ_WRITE
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // 값을 쓰는 경우에만 접근이 허용 + 응답결과를 생성할 때는 해당 필드는 제외
    @NotNull
    @Size(min = 3, max = 100)
    private String password;

    @NotNull
    @Size(min = 3, max = 50)
    private String nickname;
}