package controler;

import com.google.gson.Gson;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Cart;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "RemoveCartItem", urlPatterns = {"/RemoveCartItem"})
public class RemoveCartItem extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        Response_DTO response_DTO = new Response_DTO();

        try {
            // Start the transaction
            transaction = session.beginTransaction();

            // Get the logged-in user from the session
//            User_DTO loggedUser = (User_DTO) request.getSession().getAttribute("email");
//            Criteria criteria1 =session.createCriteria(User.class);
//            criteria1.add(Restrictions.eq("email", loggedUser));
//           User user = (User) criteria1.uniqueResult();
            if (request.getSession().getAttribute("user") == null) {
                response_DTO.setContent("User not logged in.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {

                User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user"); //get log user

                //get db User
                Criteria criteria1 = session.createCriteria(User.class); //select * user
                criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));// where email = session user dto . email?
                User user = (User) criteria1.uniqueResult();
                // Get the product ID from the request (item to be removed)
                String productId = request.getParameter("productId");

                if (!Validations.isInteger(productId)) {
                    response_DTO.setContent("Invalid product ID.");
                } else {
                    // Find the cart item associated with the user and the product
                    //Check in db cart
                    Criteria criteria2 = session.createCriteria(Cart.class);
                    criteria2.add(Restrictions.eq("user", user));
                    criteria2.add(Restrictions.eq("product.id", Integer.parseInt(productId)));

                    Cart cartItem = (Cart) criteria2.uniqueResult();

                    if (cartItem != null) {
                        // Remove the item from the cart
                        session.delete(cartItem);
                        transaction.commit();
                        response_DTO.setContent("Item removed successfully.");
                    } else {
                        response_DTO.setContent("Cart item not found.");
                    }
                }
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            response_DTO.setContent("Error removing item from cart.");
            e.printStackTrace();
        } finally {
            session.close();
        }

        // Send the response back to the client
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
    }
}
