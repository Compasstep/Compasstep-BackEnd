package com.fifo.compasstep.admin.service;

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

    public void sendInvitationEmail(String email, String temppassword) {
        String subject = "Flow 관리자 초대";
        String signupLink = invitationBaseUrl;

        String htmlTemplate = """
                <!doctype html>
                <html lang="und" dir="auto" xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">
                <head>
                  <title>Flow 관리자 초대</title>
                  <!--[if !mso]><!-->
                  <meta http-equiv="X-UA-Compatible" content="IE=edge">
                  <!--<![endif]-->
                  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <style type="text/css">
                    #outlook a { padding: 0; }
                    body { margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }
                    table, td { border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
                    img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; }
                    p { display: block; margin: 13px 0; }
                  </style>
                  <!--[if mso]>
                  <noscript>
                  <xml>
                  <o:OfficeDocumentSettings>
                    <o:AllowPNG/>
                    <o:PixelsPerInch>96</o:PixelsPerInch>
                  </o:OfficeDocumentSettings>
                  </xml>
                  </noscript>
                  <![endif]-->
                  <!--[if lte mso 11]>
                  <style type="text/css">
                    .mj-outlook-group-fix { width:100% !important; }
                  </style>
                  <![endif]-->
                  <!--[if !mso]><!-->
                  <link href="https://fonts.googleapis.com/css?family=Ubuntu:300,400,500,700" rel="stylesheet" type="text/css">
                  <style type="text/css">
                    @import url(https://fonts.googleapis.com/css?family=Ubuntu:300,400,500,700);
                  </style>
                  <!--<![endif]-->
                  <style type="text/css">
                    @media only screen and (min-width:480px) {
                      .mj-column-per-100 { width: 100% !important; max-width: 100%; }
                    }
                  </style>
                  <style media="screen and (min-width:480px)">
                    .moz-text-html .mj-column-per-100 { width: 100% !important; max-width: 100%; }
                  </style>
                  <style type="text/css">
                    @media only screen and (max-width:479px) {
                      table.mj-full-width-mobile { width: 100% !important; }
                      td.mj-full-width-mobile { width: auto !important; }
                    }
                  </style>
                  <style type="text/css">
                    @keyframes fadeInUp {
                      from { opacity: 0; transform: translateY(30px); }
                      to { opacity: 1; transform: translateY(0); }
                    }
                    @keyframes pulse {
                      0%, 100% { transform: scale(1); opacity: 1; }
                      50% { transform: scale(1.05); opacity: 0.8; }
                    }
                    .fade-in { animation: fadeInUp 1s ease-out; }
                    .logo-pulse { animation: pulse 2s infinite; }
                    .invite-button { transition: all 0.3s ease !important; position: relative !important; overflow: hidden !important; }
                  </style>
                </head>
                <body style="word-spacing:normal;">
                  <div style="display:none;font-size:1px;color:#ffffff;line-height:1px;max-height:0px;max-width:0px;opacity:0;overflow:hidden;">관리자 등록을 완료해주세요</div>
                  <div style="" lang="und" dir="auto">
                    <!--[if mso | IE]><table align="center" border="0" cellpadding="0" cellspacing="0" class="fade-in-outlook" role="presentation" style="width:600px;" width="600" bgcolor="#E7ECF5" ><tr><td style="line-height:0px;font-size:0px;mso-line-height-rule:exactly;"><![endif]-->
                    <div class="fade-in" style="background:#E7ECF5;background-color:#E7ECF5;margin:0px auto;border-radius:8px;max-width:600px;">
                      <table align="center" border="0" cellpadding="0" cellspacing="0" role="presentation" style="background:#E7ECF5;background-color:#E7ECF5;width:100%;border-radius:8px;">
                        <tbody>
                          <tr>
                            <td style="direction:ltr;font-size:0px;padding:80px 20px;text-align:center;">
                              <!--[if mso | IE]><table role="presentation" border="0" cellpadding="0" cellspacing="0"><tr><td class="" style="vertical-align:top;width:560px;" ><![endif]-->
                              <div class="mj-column-per-100 mj-outlook-group-fix" style="font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;">
                                <table border="0" cellpadding="0" cellspacing="0" role="presentation" style="vertical-align:top;" width="100%">
                                  <tbody>
                                    <tr>
                                      <td align="center" class="logo-pulse" style="font-size:0px;padding:10px 25px;padding-bottom:32px;word-break:break-word;">
                                        <table border="0" cellpadding="0" cellspacing="0" role="presentation" style="border-collapse:collapse;border-spacing:0px;">
                                          <tbody>
                                            <tr>
                                              <td style="width:160px;">
                                                <img alt="FLOW 아이콘" src="https://umc-perfume-bucket.s3.ap-northeast-1.amazonaws.com/FLOW_icon.png" style="border:0;display:block;outline:none;text-decoration:none;height:auto;width:100%;font-size:13px;" width="160" height="auto" />
                                              </td>
                                            </tr>
                                          </tbody>
                                        </table>
                                      </td>
                                    </tr>
                                    <tr>
                                      <td align="center" class="fade-in" style="font-size:0px;padding:10px 25px;padding-bottom:32px;word-break:break-word;">
                                        <div style="font-family:Ubuntu, Helvetica, Arial, sans-serif;font-size:32px;font-weight:700;line-height:1;text-align:center;color:#202124;">Flow 관리자 초대</div>
                                      </td>
                                    </tr>
                                    <tr>
                                      <td align="center" class="fade-in" style="font-size:0px;padding:10px 25px;padding-bottom:32px;word-break:break-word;">
                                        <div style="font-family:Ubuntu, Helvetica, Arial, sans-serif;font-size:20px;line-height:1.5;text-align:center;color:#5f6368;">아래 링크를 통해 관리자 등록을 완료해주세요.<br> (링크는 12시간 동안 유효합니다)</div>
                                      </td>
                                    </tr>
                                    <tr>
                                      <td align="center" class="invite-button" style="font-size:0px;padding:16px 32px;word-break:break-word;">
                                        <table border="0" cellpadding="0" cellspacing="0" role="presentation" style="border-collapse:separate;width:280px;line-height:100%;">
                                          <tbody>
                                            <tr>
                                              <td align="center" bgcolor="#0F429D" role="presentation" style="border:none;border-radius:8px;cursor:auto;mso-padding-alt:10px 25px;background:#0F429D;" valign="middle">
                                                <a href="PLACEHOLDER_SIGNUP_LINK" style="display:inline-block;width:230px;background:#0F429D;color:white;font-family:Ubuntu, Helvetica, Arial, sans-serif;font-size:16px;font-weight:600;line-height:120%;margin:0;text-decoration:none;text-transform:none;padding:10px 25px;mso-padding-alt:0px;border-radius:8px;" target="_blank"> 회원가입 하러가기 </a>
                                              </td>
                                            </tr>
                                          </tbody>
                                        </table>
                                      </td>
                                    </tr>
                                  </tbody>
                                </table>
                              </div>
                              <!--[if mso | IE]></td></tr></table><![endif]-->
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <!--[if mso | IE]></td></tr></table><![endif]-->
                  </div>
                  asdf
                </body>
                </html>
                """;

        String htmlContent0 = htmlTemplate.replace("PLACEHOLDER_SIGNUP_LINK", "http://localhost:8080");
        String htmlContent = htmlTemplate.replace("asdf", temppassword);

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
