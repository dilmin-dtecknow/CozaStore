package controler;

import com.google.gson.Gson;
import dto.Response_DTO;
import dto.User_DTO;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Mail;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "ForgotPassword", urlPatterns = {"/ForgotPassword"})
public class ForgotPassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Gson gson = new Gson();
        User_DTO user_DTO = gson.fromJson(req.getReader(), User_DTO.class);
        String email = user_DTO.getEmail();

//        System.out.println(email);
        Response_DTO response_DTO = new Response_DTO();

        if (user_DTO.getEmail().isEmpty()) {
            response_DTO.setContent("Please enter a Email");
        } else if (!Validations.isEmailValide(user_DTO.getEmail())) {
            response_DTO.setContent("Please enter a Valide Email");
        } else {
            // Check if email exists
            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", email));

            if (!criteria.list().isEmpty()) {
                // Generate and send verification code via email
                int verificationCode = (int) (Math.random() * 1000000);
                req.getSession().setAttribute("verificationCode", verificationCode);
                req.getSession().setAttribute("email", email);

                // Send email with verification code
                Mail.sendMail(email, "Password Reset Verification",
                        "<h1>Your verification code: " + verificationCode + "</h1>");

                response_DTO.setSuccess(true);
                response_DTO.setContent("Verification code sent to your email.");
            } else {
                response_DTO.setSuccess(false);
                response_DTO.setContent("Email does not exist.");
            }
        }
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(response_DTO));
    }
}
