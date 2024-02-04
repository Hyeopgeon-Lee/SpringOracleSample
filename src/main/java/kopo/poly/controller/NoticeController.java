package kopo.poly.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kopo.poly.controller.response.CommonResponse;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.service.INoticeService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name = "공지사항 서비스", description = "공지사항 구현을 위한 API")
@Slf4j
@RequestMapping(value = "/api/notice/v1")
@RequiredArgsConstructor
@RestController
public class NoticeController {

    // @RequiredArgsConstructor 를 통해 메모리에 올라간 서비스 객체를 Controller에서 사용할 수 있게 주입함
    private final INoticeService noticeService;

    @Operation(summary = "공지사항 리스트 API", description = "공지사항 리스트 정보 제공하는 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!"),})
    @PostMapping(value = "list")
    public ResponseEntity<CommonResponse> list(HttpSession session)
            throws Exception {

        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".noticeList Start!");

        // 로그인된 사용자 아이디는 Session에 저장함
        // 교육용으로 아직 로그인을 구현하지 않았기 때문에 Session에 데이터를 저장하지 않았음
        // 추후 로그인을 구현할 것으로 가정하고, 공지사항 리스트 출력하는 함수에서 로그인 한 것처럼 Session 값을 생성함
        session.setAttribute("SESSION_USER_ID", "USER01");

        // 공지사항 리스트 조회하기
        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<NoticeDTO> rList = Optional.ofNullable(noticeService.getNoticeList())
                .orElseGet(ArrayList::new);

        // 로그 찍기(추후 찍은 로그를 통해 이 함수 호출이 끝났는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".noticeList End!");

        // HTTP 상태 코드, 메시지, 결과값을 JSON 구조로 전달
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList));

    }

    @Operation(summary = "공지사항 등록 API", description = "공지사항 등록 및 등록결과를 제공하는 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!"),})
    @PostMapping(value = "insert")
    public ResponseEntity<CommonResponse> insert(@Valid @RequestBody NoticeDTO pDTO,
                                                 HttpSession session, BindingResult bindingResult) {

        log.info(this.getClass().getName() + ".insert Start!");

        if (bindingResult.hasErrors()) { // Spring Validation 맞춰 잘 바인딩되었는지 체크
            return getErrors(bindingResult);

        }

        String msg = ""; // 메시지 내용

        MsgDTO dto = null; // 결과 메시지 구조

        try {
            // 로그인된 사용자 아이디를 가져오기
            // 로그인을 아직 구현하지 않았기에 공지사항 리스트에서 로그인 한 것처럼 Session 값을 저장함
            String loginId = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ID"), "USER01");

            // 데이터 저장하기 위해 DTO에 저장하기
            pDTO.setUserId(loginId);
            log.info("session loginId : " + loginId);
            log.info("pDTO : " + pDTO);

            /*
             * 게시글 등록하기위한 비즈니스 로직을 호출
             */
            noticeService.insertNoticeInfo(pDTO);

            // 저장이 완료되면 사용자에게 보여줄 메시지
            msg = "등록되었습니다.";

        } catch (Exception e) {

            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            // 결과 메시지 전달하기
            dto = new MsgDTO();
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".insert End!");
        }

        return ResponseEntity.ok(CommonResponse.of(HttpStatus.CREATED, HttpStatus.CREATED.series().name(), dto));
    }

    @Operation(summary = "공지사항 상세보기 결과제공 API", description = "공지사항 상세보기 결과 및 조회수 증가 API",
            parameters = {
                    @Parameter(name = "noticeSeq", description = "공지사항 글번호"),
                    @Parameter(name = "readCntYn", description = "조회수 증가여부")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!"),})
    @PostMapping(value = "info")
    public ResponseEntity info(@RequestBody NoticeDTO pDTO, BindingResult bindingResult, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".info Start!");

        if (bindingResult.hasErrors()) {
            return getErrors(bindingResult);

        }

        log.info("pDTO : " + pDTO.toString());

        // 로그인된 사용자 아이디를 가져오기
        // 로그인을 아직 구현하지 않았기에 공지사항 리스트에서 로그인 한 것처럼 Session 값을 저장함
        String loginId = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ID"), "USER01");

        // 공지사항 상세정보 가져오기
        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        NoticeDTO rDTO = Optional.ofNullable(noticeService.getNoticeInfo(pDTO,
                        CmmUtil.nvl(pDTO.getReadCntYn()).equals("Y")) // 조회수 증가여부(증가 : true)
                )
                .orElseGet(NoticeDTO::new);

        rDTO.setLoginId(CmmUtil.nvl(loginId));

        log.info(this.getClass().getName() + ".info End!");

        // HTTP 상태 코드, 메시지, 결과값을 JSON 구조로 전달
        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rDTO));
    }

    @Operation(summary = "공지사항 수정 API", description = "공지사항 수정 및 수정결과를 제공하는 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!"),})
    @PostMapping(value = "update")
    public ResponseEntity update(@Valid @RequestBody NoticeDTO pDTO,
                                 BindingResult bindingResult, HttpSession session) {

        log.info(this.getClass().getName() + ".update Start!");

        if (bindingResult.hasErrors()) {
            return getErrors(bindingResult);

        }

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ID")); // 아이디
            log.info("userId : " + userId);
            log.info("pDTO : " + pDTO);


            // 게시글 수정하기 DB
            noticeService.updateNoticeInfo(pDTO);

            msg = "수정되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            // 결과 메시지 전달하기
            dto = new MsgDTO();
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".update End!");

        }

        // HTTP 상태 코드, 메시지, 결과값을 JSON 구조로 전달
        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), dto));
    }

    @Operation(summary = "공지사항 삭제 API", description = "공지사항 삭제 및 삭제결과를 제공하는 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!"),})
    @DeleteMapping(value = "delete")
    public ResponseEntity delete(@RequestBody NoticeDTO pDTO,
                                 BindingResult bindingResult) {

        log.info(this.getClass().getName() + ".delete Start!");

        if (bindingResult.hasErrors()) {
            return getErrors(bindingResult);
        }

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            // 게시글 삭제하기 DB
            noticeService.deleteNoticeInfo(pDTO);

            msg = "삭제되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            // 결과 메시지 전달하기
            dto = new MsgDTO();
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".delete End!");

        }

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), dto));
    }

    private static ResponseEntity<CommonResponse> getErrors(BindingResult bindingResult) {
        return ResponseEntity.badRequest()
                .body(CommonResponse.of(HttpStatus.BAD_REQUEST,
                        HttpStatus.BAD_REQUEST.series().name(),
                        bindingResult.getAllErrors()));
    }

}
