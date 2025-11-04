package controler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.Cart_DTO;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Cart;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    private static final int MAX_LOGIN_ATTEMPTS = 3; // Maximum allowed attempts
    private static final long LOCK_TIME_MILLIS = 10 * 60 * 1000; // 10 minutes in milliseconds

    @Override
    protected void doPost(HttpServletRequest reqest, HttpServletResponse response) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        User_DTO user_DTO = gson.fromJson(reqest.getReader(), User_DTO.class);

        // Retrieve the session to track login attempts
        HttpSession httpSession = reqest.getSession();
        Integer loginAttempts = (Integer) httpSession.getAttribute("loginAttempts");
        Long lastFailedAttemptTime = (Long) httpSession.getAttribute("lastFailedAttemptTime");

        if (loginAttempts == null) {
            loginAttempts = 0; // Initialize login attempts if not yet set
        }

        if (lastFailedAttemptTime != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFailedAttemptTime >= LOCK_TIME_MILLIS) {
                // Reset login attempts if 10 minutes have passed since the last failed attempt
                loginAttempts = 0;
                httpSession.setAttribute("loginAttempts", loginAttempts);
                httpSession.removeAttribute("lastFailedAttemptTime");
            }
        }

        if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {

            long currentTime = System.currentTimeMillis();
            long timeRemaining = LOCK_TIME_MILLIS - (currentTime - lastFailedAttemptTime);

            if (timeRemaining > 0) {
                long minutesRemaining = timeRemaining / 60000;
                long secondsRemaining = (timeRemaining % 60000) / 1000;
                response_DTO.setContent("Your account is temporarily locked. Please try again in " + minutesRemaining + " minutes and " + secondsRemaining + " seconds.");
                response_DTO.setSuccess(false);
            } else {
                httpSession.setAttribute("loginAttempts", 0);
                httpSession.removeAttribute("lastFailedAttemptTime");
            }

        } else {

            if (user_DTO.getEmail().isEmpty()) {
                response_DTO.setContent("Please enter your Email");
            } else if (user_DTO.getPassword().isEmpty()) {
                response_DTO.setContent("Please enter your Password");
            } else {

                Session session = HibernateUtil.getSessionFactory().openSession();

                Criteria criteria1 = session.createCriteria(User.class); //search user
                criteria1.add(Restrictions.eq("email", user_DTO.getEmail())); //and
                criteria1.add(Restrictions.eq("password", user_DTO.getPassword()));

                if (!criteria1.list().isEmpty()) {

                    User user = (User) criteria1.uniqueResult();

                    if (user.getVerification().equals("Verified")) {
                        //verified

                        // Reset login attempts on successful login
                        httpSession.setAttribute("loginAttempts", 0);
                        httpSession.removeAttribute("lastFailedAttemptTime");

                        user_DTO.setFirst_name(user.getFirst_name());
                        user_DTO.setLast_name(user.getLast_name());
                        user_DTO.setPassword(null);
                        user_DTO.setUser_type(user.getUser_type());
                        reqest.getSession().setAttribute("user", user_DTO);

                        //Transfer Session Cart to DB Cart
                        if (reqest.getSession().getAttribute("sessionCart") != null) {
                            //session cart found

                            ArrayList<Cart_DTO> sessionCart = (ArrayList<Cart_DTO>) reqest.getSession().getAttribute("sessionCart"); //get session cart

                            //serach db Cart
                            Criteria criteria2 = session.createCriteria(Cart.class);
                            criteria2.add(Restrictions.eq("user", user));
                            List<Cart> dbCart = criteria2.list();

                            if (dbCart.isEmpty()) {
                                //db Cart empty
                                //add all Session Cart items in to DB Cart one by one

                                for (Cart_DTO cart_DTO : sessionCart) {
                                    Cart cart = new Cart();
                                    cart.setProduct(cart_DTO.getProduct());
                                    cart.setQty(cart_DTO.getQty());
                                    cart.setUser(user);

                                    session.save(cart);
                                }

                            } else {
                                //found items in DB cart

                                //compare session cart item is in db cart
                                for (Cart_DTO cart_DTO : sessionCart) { //get session cart item

                                    boolean isFoundDBCart = false;

                                    for (Cart cart : dbCart) { //get db cart item

                                        if (cart_DTO.getProduct().getId() == cart.getProduct().getId()) {
                                            //same item found in Session & DB Cart
                                            isFoundDBCart = true;

                                            if ((cart_DTO.getQty() + cart.getQty()) <= cart.getProduct().getQty()) {
                                                //quantity available
                                                cart.setQty(cart_DTO.getQty() + cart.getQty()); //session cart qty + cart qty
                                                session.update(cart);

                                            } else {
                                                //quantity not available
                                                //set max available qty
                                                cart.setQty(cart.getProduct().getQty());
                                                session.update(cart);
                                            }
                                        }
                                    }

                                    if (!isFoundDBCart) {
                                        //not Found session Cart Item in DB cart
                                        Cart cart = new Cart();
                                        cart.setProduct(cart_DTO.getProduct());
                                        cart.setQty(cart_DTO.getQty());
                                        cart.setUser(user);

                                        session.save(cart);
                                    }
                                }
                            }
                            reqest.getSession().removeAttribute("sessionCart");
                            session.beginTransaction().commit();
                        }

                        response_DTO.setSuccess(true);
                        response_DTO.setContent("Sigin success");

                    } else {
                        //not verified

                        reqest.getSession().setAttribute("email", user_DTO.getEmail());// want to know who unverifide, so set unverifid user email
                        response_DTO.setContent("UnVerified");

                    }

                } else {
                    //invalide login Details

                    loginAttempts++; // Increment the failed login attempts
                    httpSession.setAttribute("loginAttempts", loginAttempts);
                    httpSession.setAttribute("lastFailedAttemptTime", System.currentTimeMillis());

                    if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                        response_DTO.setContent("Your account is temporarily locked for 10 minutes due to too many failed login attempts.");
                    } else {
                        response_DTO.setContent("Invalid details! Please try again. Attempt " + loginAttempts + " of " + MAX_LOGIN_ATTEMPTS);
                    }
                }
                session.close();
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
        System.out.println(gson.toJson(response_DTO));
    }

}
