
package controler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.Response_DTO;
import dto.User_DTO;
import entity.User;
import entity.User_Type;
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


@WebServlet(name = "SellerSignUp", urlPatterns = {"/SellerSignUp"})
public class SellerSignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest reqest, HttpServletResponse response) throws ServletException, IOException {
        Response_DTO response_DTO = new Response_DTO();

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        User_DTO user_DTO = gson.fromJson(reqest.getReader(), User_DTO.class);

        if (!Validations.isEmailValide(user_DTO.getEmail())) {
            response_DTO.setContent("Please enter a Valide Email");
        } else if (!Validations.isPasswordValide(user_DTO.getPassword())) {
            response_DTO.setContent("Please enter a valide Password ( password "
                    + "containing atleast 1 lower case letter, 1 upper case letter, "
                    + "number and one of the mentioned special characters and match 8 or more characters)");
        } else {

            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria criteria1 = session.createCriteria(User.class); //search user
            criteria1.add(Restrictions.eq("email", user_DTO.getEmail())); //where email=request email?

            if (!criteria1.list().isEmpty()) { //user allready exist
                response_DTO.setContent("User with Email already used!");
            } else {
                //generate verification code
                int code = (int) (Math.random() * 1000000);

                User_Type user_type = (User_Type) session.load(User_Type.class, 2);

                //save user
                final User user = new User();
                user.setEmail(user_DTO.getEmail());
                user.setFirst_name(user_DTO.getFirst_name());
                user.setLast_name(user_DTO.getLast_name());
                user.setPassword(user_DTO.getPassword());
                user.setVerification(String.valueOf(code));
                user.setUser_type(user_type);

                //send verification email
                Thread sendEmailThread = new Thread() {
                    @Override
                    public void run() {
                        Mail.sendMail(user.getEmail(), "CozaStore Seller Verification",
                                "<h1 style=\"color:#4d88ff;\">" + user.getVerification() + "</h1>"
                        );
                    }

                };
                sendEmailThread.start();

                session.save(user);
                session.beginTransaction().commit();

                reqest.getSession().setAttribute("email", user_DTO.getEmail()); //verifiction page want to identify email for who registered
                response_DTO.setSuccess(true);
                System.out.println(gson.toJson(response_DTO));
                response_DTO.setContent("Registation Compleate. Please Check your inbox for Verifiction Code!");
            }

            session.close();
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
        System.out.println(gson.toJson(response_DTO));
    }
}
