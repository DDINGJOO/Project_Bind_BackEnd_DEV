package security.jwt;


import lombok.Builder;

@Builder
public record TokenParam(String userId, String role) {

}
