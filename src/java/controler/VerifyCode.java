package controler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import dto.User_DTO;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "VerifyCode", urlPatterns = {"/VerifyCode"})
public class VerifyCode extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Gson gson = new Gson();
        Response_DTO response_DTO = new Response_DTO();
//        User_DTO user_DTO = gson.fromJson(req.getReader(), User_DTO.class);
//        int enteredCode = Integer.parseInt(user_DTO.getCode());
        JsonObject jsonObject = gson.fromJson(req.getReader(), JsonObject.class);
        String enteredCode = jsonObject.get("verification").getAsString();
        if (!enteredCode.isEmpty()) {

//            int sessionCode = (int) req.getSession().getAttribute("verificationCode");
            int sessionCode = (int) req.getSession().getAttribute("verificationCode");
//            Response_DTO response_DTO = new Response_DTO();
            if (Integer.parseInt(enteredCode) == sessionCode) {
                response_DTO.setSuccess(true);
            } else {
                response_DTO.setSuccess(false);
                response_DTO.setContent("Invalid verification code.");
            }
        } else {
            response_DTO.setSuccess(false);
            response_DTO.setContent("Empty verification code.");
        }
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(response_DTO));
    }
}
