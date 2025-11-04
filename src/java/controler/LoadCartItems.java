package controler;

import com.google.gson.Gson;
import dto.Cart_DTO;
import dto.User_DTO;
import entity.Cart;
import entity.Product;
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

@WebServlet(name = "LoadCartItems", urlPatterns = {"/LoadCartItems"})
public class LoadCartItems extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        HttpSession httpsession = request.getSession();
        ArrayList<Cart_DTO> cart_DTO_List = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {

            if (httpsession.getAttribute("user") != null) {
                //Db cart

                User_DTO user_DTO = (User_DTO) httpsession.getAttribute("user");

                Criteria criteria1 = session.createCriteria(User.class);
                criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
                User user = (User) criteria1.uniqueResult();

                Criteria criteria2 = session.createCriteria(Cart.class);
                criteria2.add(Restrictions.eq("user", user));
                List<Cart> cartList = criteria2.list();

                //db Cart Item read
                for (Cart cart : cartList) {

                    Cart_DTO cart_DTO = new Cart_DTO(); //create cart dto

                    Product product = cart.getProduct(); //get product in cart
                    product.setUser(null);

                    cart_DTO.setProduct(product);
                    cart_DTO.setQty(cart.getQty());

                    cart_DTO_List.add(cart_DTO);
                }

            } else {
                //session Cart

                if (httpsession.getAttribute("sessionCart") != null) {

                    cart_DTO_List = (ArrayList<Cart_DTO>) httpsession.getAttribute("sessionCart");

                    for (Cart_DTO cart_DTO : cart_DTO_List) {
                        cart_DTO.getProduct().setUser(null);
                    }

                } else {
                    //cart empty

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        session.close();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(cart_DTO_List));
    }

}
