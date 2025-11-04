package controler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

@WebServlet(name = "Verification", urlPatterns = {"/Verification"})
public class Verification extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest reqest, HttpServletResponse response) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new Gson();
        JsonObject code_dto = gson.fromJson(reqest.getReader(), JsonObject.class);

        String verification = code_dto.get("verification").getAsString();

        if (reqest.getSession().getAttribute("email") != null) {
            //allready user in the session

            String email = reqest.getSession().getAttribute("email").toString();

            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria criteria1 = session.createCriteria(User.class); //search user
            criteria1.add(Restrictions.eq("email", email));
            //and
            criteria1.add(Restrictions.eq("verification", verification));

            if (!criteria1.list().isEmpty()) {
                //all done

                User user = (User) criteria1.uniqueResult();
                user.setVerification("Verified");

                session.update(user);
                session.beginTransaction().commit();

                User_DTO user_DTO = new User_DTO();
                user_DTO.setFirst_name(user.getFirst_name());
                user_DTO.setLast_name(user.getLast_name());
                user_DTO.setEmail(email);
                user_DTO.setUser_type(user.getUser_type());
                reqest.getSession().removeAttribute("email");
                reqest.getSession().setAttribute("user", user_DTO);
                
                System.out.println(user_DTO);
                
                response_DTO.setSuccess(true);
                response_DTO.setContent("Verification success");

            } else {
                //invalide code
                response_DTO.setContent("Invalide Verification code!");
            }

        } else {
            //no user
            response_DTO.setContent("Please LogIn or SignUp!");
        }
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
        System.out.println(gson.toJson(response_DTO));
    }

}
