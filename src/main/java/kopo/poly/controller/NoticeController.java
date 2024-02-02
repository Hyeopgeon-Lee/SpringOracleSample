package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.controller.response.ApiResponse;
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


/*
 * Controller 선언해야만 Spring 프레임워크에서 Controller인지 인식 가능
 * 자바 서블릿 역할 수행
 *
 * slf4j는 스프링 프레임워크에서 로그 처리하는 인터페이스 기술이며,
 * 로그처리 기술인 log4j와 logback과 인터페이스 역할 수행함
 * 스프링 프레임워크는 기본으로 logback을 채택해서 로그 처리함
 * */
@Slf4j
@RequestMapping(value = "/api/notice/v1")
@RequiredArgsConstructor
@RestController
public class NoticeController {

    // @RequiredArgsConstructor 를 통해 메모리에 올라간 서비스 객체를 Controller에서 사용할 수 있게 주입함
    private final INoticeService noticeService;

    /**
     * 게시판 리스트 보여주기
     * <p>
     * GetMapping(value = "list") =>  GET방식을 통해 접속되는 URL이 /api/v1/notice/list 경우 아래 함수를 실행함
     */
    @PostMapping(value = "list")
    public ResponseEntity<ApiResponse> list(HttpSession session)
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
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList));

    }

    /**
     * 게시판 글 등록
     * <p>
     * 게시글 등록은 Ajax로 호출되기 때문에 결과는 JSON 구조로 전달해야만 함
     * JSON 구조로 결과 메시지를 전송하기 위해 @ResponseBody 어노테이션 추가함
     */
    @PostMapping(value = "insert")
    public ResponseEntity<ApiResponse> insert(@RequestParam NoticeDTO pDTO, HttpSession session) {

        log.info(this.getClass().getName() + ".insert Start!");

        String msg = ""; // 메시지 내용

        MsgDTO dto = null; // 결과 메시지 구조

        try {
            // 로그인된 사용자 아이디를 가져오기
            // 로그인을 아직 구현하지 않았기에 공지사항 리스트에서 로그인 한 것처럼 Session 값을 저장함
            String userId = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ID"), "USER01");

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("session user_id : " + userId);
            // 데이터 저장하기 위해 DTO에 저장하기
            pDTO.setUserId(userId);

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

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.CREATED, HttpStatus.CREATED.series().name(), dto));
    }

    /**
     * 게시판 상세보기
     */
    @GetMapping(value = "info")
    public ResponseEntity noticeInfo(@RequestBody NoticeDTO pDTO, BindingResult bindingResult) throws Exception {

        log.info(this.getClass().getName() + ".info Start!");

        if (bindingResult.hasErrors()) {
            return getErrors(bindingResult);

        }

        log.info("pDTO : " + pDTO);

        // 공지사항 상세정보 가져오기
        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        NoticeDTO rDTO = Optional.ofNullable(noticeService.getNoticeInfo(pDTO, true))
                .orElseGet(NoticeDTO::new);

        log.info(this.getClass().getName() + ".info End!");

        // HTTP 상태 코드, 메시지, 결과값을 JSON 구조로 전달
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rDTO));
    }

    /**
     * 게시판 글 수정
     */
    @ResponseBody
    @PostMapping(value = "update")
    public ResponseEntity noticeUpdate(@RequestBody NoticeDTO pDTO, BindingResult bindingResult, HttpSession session) {

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
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), dto));
    }

    /**
     * 게시판 글 삭제
     */
    @DeleteMapping(value = "delete")
    public ResponseEntity noticeDelete(HttpServletRequest request, BindingResult bindingResult) {

        log.info(this.getClass().getName() + ".delete Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        if (bindingResult.hasErrors()) {
            return getErrors(bindingResult);
        }

        try {
            String nSeq = CmmUtil.nvl(request.getParameter("nSeq")); // 글번호(PK)

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("nSeq : " + nSeq);

            /*
             * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
             */
            NoticeDTO pDTO = new NoticeDTO();
            pDTO.setNoticeSeq(nSeq);

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

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), dto));
    }

    private static ResponseEntity<ApiResponse> getErrors(BindingResult bindingResult) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST,
                        HttpStatus.BAD_REQUEST.series().name(),
                        bindingResult.getAllErrors()));
    }

}
