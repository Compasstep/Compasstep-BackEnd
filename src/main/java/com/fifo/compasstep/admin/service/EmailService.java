package com.fifo.compasstep.admin.service;

import com.fifo.compasstep.apipayload.exceptions.handler.UserHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${app.invitation.base-url}")
    private String invitationBaseUrl;

    public void sendInvitationEmail(String email, String temppassword, String condition) {
        String subject = "compasstep 관리자 초대";
        String signupLink = invitationBaseUrl;
        String htmlTemplate;
        if(condition.equals("newuser")){
            htmlTemplate = """
                    <div style="font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #eee; padding: 20px;">
                            <p style="font-size: 14px; color: #555;">
                              <strong>From:</strong> Compassstep Admin &lt;no-reply@compasstep.com&gt;<br>
                              <strong>To:</strong> inviteEmail
                            </p>
                            <p>
                              <span style="background-color: #e0f2fe; color: #0c4a6e; padding: 5px 10px; border-radius: 15px; font-size: 12px; font-weight: bold; margin-right: 5px;">#관리자 초대</span>
                              <span style="background-color: #e0f2fe; color: #0c4a6e; padding: 5px 10px; border-radius: 15px; font-size: 12px; font-weight: bold;">#임시 PW</span>
                            </p>
                            <h2 style="font-size: 24px; color: #111;">안녕하세요,</h2>
                            <p style="font-size: 16px; line-height: 1.6;">
                              Compassstep 관리자 콘솔에 초대되었습니다.<br>
                              아래 임시 비밀번호로 로그인 후, 비밀번호를 변경해주세요.
                            </p>
                            <div style="background-color: #f3f4f6; padding: 20px; border-radius: 8px; text-align: left; margin: 24px 0;">
                              <p style="margin: 0; font-size: 14px; color: #6b7280;">임시 비밀번호</p>
                              <p style="margin: 10px 0 0; font-size: 24px; color: #111; font-weight: bold; letter-spacing: 2px;">newtemppassword</p>
                            </div>
                            <div style="text-align: center;">
                              <a href="PLACEHOLDER_SIGNUP_LINK" target="_blank" style="display: inline-block; background-color: #111; color: #ffffff; padding: 14px 24px; border-radius: 8px; text-decoration: none; font-weight: bold; margin-top: 12px;">비밀번호 변경하기</a>
                              <br>
                              <a href="PLACEHOLDER_SIGNUP_LINK" target="_blank" style="display: inline-block; color: #2563eb; text-decoration: none; margin-top: 16px; font-size: 14px;">브라우저에서 열기</a>
                            </div>
                            <hr style="border: none; border-top: 1px solid #eee; margin: 32px 0;">
                            <div style="background-color: #f3f4f6; padding: 15px; border-radius: 8px; text-align: left;">
                              <p><span style="background-color: #e5e7eb; color: #4b5563; padding: 4px 8px; border-radius: 15px; font-size: 12px; font-weight: bold;">보안 알림</span></p>
                              <p style="font-size: 12px; color: #6b7280; line-height: 1.6;">
                                이 메일은 발신 전용입니다. 문의: support@compasstep.com
                              </p>
                            </div>
                            <p style="text-align: center; font-size: 12px; color: #9ca3af; margin-top: 24px;">© 2025 Compassstep. All rights reserved.</p>
                          </div>""";

        }
        else{
            htmlTemplate = """
                    <div style="font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #eee; padding: 20px;">
                            <p style="font-size: 14px; color: #555;">
                              <strong>From:</strong> Compassstep Security &lt;no-reply@compasstep.com&gt;<br>
                              <strong>To:</strong> inviteEmail
                            </p>
                            <p>
                              <span style="background-color: #dcfce7; color: #166534; padding: 5px 10px; border-radius: 15px; font-size: 12px; font-weight: bold;">#비밀번호 재설정</span>
                            </p>
                            <h2 style="font-size: 24px; color: #111;">요청하신 계정의 임시 비밀번호를 발급했습니다.</h2>
                            <p style="font-size: 16px; line-height: 1.6;">
                              아래 정보를 사용해 로그인하세요.
                            </p>
                            <div style="background-color: #f3f4f6; padding: 20px; border-radius: 8px; text-align: left; margin: 24px 0;">
                              <p style="margin: 0 0 16px; font-size: 14px; color: #6b7280;">이메일</p>
                              <p style="margin: 0 0 16px; font-size: 16px; color: #111; font-weight: bold;">${adminToReset.id}</p>
                              <hr style="border: none; border-top: 1px solid #e5e7eb;">
                              <p style="margin: 16px 0; font-size: 14px; color: #6b7280;">임시 비밀번호</p>
                              <p style="margin: 0; font-size: 16px; color: #111; font-weight: bold; letter-spacing: 1px;">newtemppassword</p>
                            </div>
                            <div style="text-align: center;">
                              <a href="PLACEHOLDER_SIGNUP_LINK" target="_blank" style="display: inline-block; background-color: #111; color: #ffffff; padding: 14px 24px; border-radius: 8px; text-decoration: none; font-weight: bold; margin-top: 12px;">로그인 페이지</a>
                            </div>
                            <p style="text-align: center; font-size: 12px; color: #9ca3af; margin-top: 24px;">보안을 위해 최초 로그인 시 비밀번호 변경 절차가 진행됩니다.</p>
                            <hr style="border: none; border-top: 1px solid #eee; margin: 32px 0;">
                            <p style="text-align: center; font-size: 12px; color: #9ca3af;">
                              문제가 있나요? support@compasstep.com 으로 문의해주세요.<br>
                              © 2025 Compassstep. All rights reserved.
                            </p>
                          </div>""";
        }




        String htmlContent0 = htmlTemplate.replace("PLACEHOLDER_SIGNUP_LINK", signupLink);
        String htmlContent1 = htmlContent0.replace("newtemppassword", temppassword);
        String htmlContent = htmlContent1.replace("inviteEmail", email);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("초대 이메일 발송 성공. 수신자: {}", email);

        } catch (MessagingException e) {
            log.error("초대 이메일 발송 실패. 수신자: {}, 에러: {}", email, e.getMessage());
            throw new IllegalStateException("이메일 발송 중 오류가 발생했습니다.");
        }
    }
}
