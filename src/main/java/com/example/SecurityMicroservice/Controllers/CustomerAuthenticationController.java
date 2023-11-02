package com.example.SecurityMicroservice.Controllers;

import com.example.SecurityMicroservice.Response.JwtAuthenticationResponse;
import com.example.SecurityMicroservice.DTO.SignInRequest;
import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Response.ResponseHandler;
import com.example.SecurityMicroservice.Services.AuthenticationService;
import com.example.SecurityMicroservice.Services.JwtService;
import com.example.SecurityMicroservice.Utils.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/customers")
@RequiredArgsConstructor
public class CustomerAuthenticationController {

    @Autowired
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody SignInRequest request) {
        try {
            JwtAuthenticationResponse response = authenticationService.login(request);
            return ResponseHandler.generateResponse("login success", HttpStatus.OK,response);
        }catch (Exception e){
            return ResponseHandler.generateResponse(e.getMessage(),HttpStatus.UNAUTHORIZED,null );
        }
    }

    @Autowired
    private EmailSenderService senderService;
//    @PostMapping()
//    public ResponseEntity<Object> createCustomerAccount (@RequestBody User user){
//
//        try{
//            JwtAuthenticationResponse response = authenticationService.addUser(user,"CUSTOMER");
//            return ResponseHandler.generateResponse("customer created successfully", HttpStatus.OK,response);
//        }catch (Exception e){
//            return ResponseHandler.generateResponse(e.getMessage(),HttpStatus.BAD_REQUEST,null);
//        }
//    }

    @Autowired
    private JwtService jwtService;

    @PostMapping()
    public ResponseEntity<Object> createCustomerAccount(@RequestBody User user) {
        try {
            JwtAuthenticationResponse response = authenticationService.addUser(user, "CUSTOMER");

            String token = response.getToken();
            String userId = jwtService.extractUserId(token);

            // Generate the validation link with the correct user ID
            String validationLink = "http://localhost:8080/v1/customers/validate/" + userId;

            String emailBody = "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                    "\n" +
                    "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                    "\n" +
                    "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                    "    <tbody><tr>\n" +
                    "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                    "        \n" +
                    "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                    "          <tbody><tr>\n" +
                    "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                    "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                    "                  <tbody><tr>\n" +
                    "                    <td style=\"padding-left:10px\">\n" +
                    "                  \n" +
                    "                    </td>\n" +
                    "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                    "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                    "                    </td>\n" +
                    "                  </tr>\n" +
                    "                </tbody></table>\n" +
                    "              </a>\n" +
                    "            </td>\n" +
                    "          </tr>\n" +
                    "        </tbody></table>\n" +
                    "        \n" +
                    "      </td>\n" +
                    "    </tr>\n" +
                    "  </tbody></table>\n" +
                    "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                    "    <tbody><tr>\n" +
                    "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                    "      <td>\n" +
                    "        \n" +
                    "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                    "                  <tbody><tr>\n" +
                    "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                    "                  </tr>\n" +
                    "                </tbody></table>\n" +
                    "        \n" +
                    "      </td>\n" +
                    "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                    "    </tr>\n" +
                    "  </tbody></table>\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                    "    <tbody><tr>\n" +
                    "      <td height=\"30\"><br></td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                    "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                    "        \n" +
                    "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + user.getUserName() + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + validationLink + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                    "        \n" +
                    "      </td>\n" +
                    "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <td height=\"30\"><br></td>\n" +
                    "    </tr>\n" +
                    "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                    "\n" +
                    "</div></div>";

            senderService.sendEmail(user.getEmail(),
                    emailBody,
                    "Validate your account");

            return ResponseHandler.generateResponse("Customer created successfully", HttpStatus.OK, response);
        } catch (Exception e) {
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }


}








