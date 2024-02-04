package kopo.poly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * lombok은 코딩을 줄이기 위해 @어노테이션을 통한 자동 코드 완성기능임
 *
 * @Getter => getter 함수를 작성하지 않았지만, 자동 생성
 * @Setter => setter 함수를 작성하지 않았지만, 자동 생성
 */
@Getter
@Setter
@ToString
public class NoticeDTO {

    private Long noticeSeq; // 기본키, 순번

    @NotBlank(message = "제목은 필수 입력 사항입니다.")
    @Size(max = 1000, message = "제목의 길이는 1000글자까지 입력가능합니다.")
    private String title; // 제목
    private String noticeYn; // 공지글 여부

    @NotBlank(message = "글 내용은 필수 입력 사항입니다.")
    @Size(max = 1000, message = "글 내용의 길이는 2000글자까지 입력가능합니다.")
    private String contents; // 글 내용
    private String userId; // 작성자
    private Long readCnt; // 조회수
    private String regId; // 등록자 아이디
    private String regDt; // 등록일
    private String chgId; // 수정자 아이디
    private String chgDt; // 수정일

    private String userName; // 등록자명
    private String loginId; // 로그인한 사용자ID
    private String readCntYn; // 공지사항 조회수 증가여부(증가 : Y)

}

