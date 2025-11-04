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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "ResetPassword", urlPatterns = {"/ResetPassword"})
public class ResetPassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Gson gson = new Gson();
        User_DTO user_DTO = gson.fromJson(req.getReader(), User_DTO.class);
//        String email = user_DTO.getEmail();

        String newPassword = user_DTO.getPassword();

        Response_DTO response_DTO = new Response_DTO();

        if (req.getSession().getAttribute("email") != null) {
            String email = req.getSession().getAttribute("email").toString();

            // Update password in the database
            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", email));

            if (!criteria.list().isEmpty()) {
                User user = (User) criteria.uniqueResult();
                user.setPassword(newPassword);
                session.beginTransaction();
                session.update(user);
                session.getTransaction().commit();

                response_DTO.setSuccess(true);
                response_DTO.setContent("Password reset successfully.");
            } else {
                response_DTO.setSuccess(false);
                response_DTO.setContent("User not found.");
            }
        } else {
            response_DTO.setSuccess(false);
            response_DTO.setContent("User not found.");
        }
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(response_DTO));
    }
}
