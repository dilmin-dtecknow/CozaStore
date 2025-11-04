package controler;

import com.google.gson.Gson;
import dto.Cart_DTO;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Cart;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        Response_DTO response_DTO = new Response_DTO();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        try {

            String id = request.getParameter("id");
            String qty = request.getParameter("qty");

            if (!Validations.isInteger(id)) {
                //Product not found
                response_DTO.setContent("Product not found");

            } else if (!Validations.isInteger(qty)) {
                //Invalide Qty
                response_DTO.setContent("Invalide Qty");
            } else {

                int productId = Integer.parseInt(id); //string id convert to id
                int productQty = Integer.parseInt(qty);

                if (productQty <= 0) {
                    //Quantity must be greter than 0
                    response_DTO.setContent("Quantity must be greter than 0");
                } else {

                    Product product = (Product) session.get(Product.class, productId); //check this product in the dataBase
                    if (product != null) {
                        //Product found

                        if (request.getSession().getAttribute("user") != null) {
                            //Db Cart

                            User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user"); //get log user

                            //get db User
                            Criteria criteria1 = session.createCriteria(User.class); //select * user
                            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));// where email = session user dto . email?
                            User user = (User) criteria1.uniqueResult();

                            //Check in db cart
                            Criteria criteria2 = session.createCriteria(Cart.class);
                            criteria2.add(Restrictions.eq("user", user));
                            criteria2.add(Restrictions.eq("product", product));

                            if (criteria2.list().isEmpty()) {
                                //Cart Item Not Found

                                if (productQty <= product.getQty()) { //add qty <= db product qty
                                    //add Product into Cart

                                    Cart cart = new Cart();
                                    cart.setProduct(product);
                                    cart.setQty(productQty);
                                    cart.setUser(user);

                                    session.save(cart);
                                    transaction.commit();

                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Product Added to Cart");

                                } else {
                                    //quantity no available
                                    response_DTO.setContent("quantity no available");
                                }

                            } else {
                                //Cart Item Found

                                Cart cartItem = (Cart) criteria2.uniqueResult();

                                if ((cartItem.getQty() + productQty) <= product.getQty()) { //allready db cart qty + addqty <= db produc qty

                                    cartItem.setQty(cartItem.getQty() + productQty);

                                    session.update(cartItem);
                                    transaction.commit();

                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Product Updated to Cart");

                                } else {
                                    //Can't Update your cartItem. Quantity not available
                                    response_DTO.setContent("Can't Update your cartItem. Quantity not available");
                                }
                            }

                        } else {
                            //session Cart

                            HttpSession httpSession = request.getSession();
                            if (httpSession.getAttribute("sessionCart") != null) {
                                //session cart Found

                                ArrayList<Cart_DTO> sessionCart = (ArrayList<Cart_DTO>) httpSession.getAttribute("sessionCart");

//                                boolean isProductFound = false; //first 1 version
                                Cart_DTO foundCart_DTO = null;

                                for (Cart_DTO cart_DTO : sessionCart) {

                                    if (cart_DTO.getProduct().getId() == product.getId()) {
//                                        isProductFound = true; //first 1 version
                                        foundCart_DTO = cart_DTO;
                                        break;
                                    }

                                }

                                if (foundCart_DTO != null) { //isProductFound //first 1 verssion
                                    //product found in session cart

                                    if ((foundCart_DTO.getQty() + productQty) <= product.getQty()) { //allready session cart qty + addqty <= db produc qty
                                        //update qty

                                        foundCart_DTO.setQty(foundCart_DTO.getQty() + productQty); //session cart qty update (session cart qty + add qty)
                                        response_DTO.setSuccess(true);
                                        response_DTO.setContent("Product Updated to Cart");

                                    } else {
                                        //quantity not available
                                        response_DTO.setContent("quantity not available");
                                    }

                                } else {
                                    //product not found in session Cart

                                    if (productQty <= product.getQty()) {
                                        //add to session cart

                                        Cart_DTO cart_DTO = new Cart_DTO();
                                        cart_DTO.setProduct(product);
                                        cart_DTO.setQty(productQty);

                                        sessionCart.add(cart_DTO);

                                        response_DTO.setSuccess(true);
                                        response_DTO.setContent("Product Added to Cart");
                                    } else {
                                        //quantity not available
                                        response_DTO.setContent("quantity not available");
                                    }
                                }

                            } else {
                                //session cart notfound

                                if (productQty <= product.getQty()) { //addqty <= db product qty?
                                    //add to session cart

                                    ArrayList<Cart_DTO> sessionCart = new ArrayList<>();

                                    Cart_DTO cart_DTO = new Cart_DTO();
                                    cart_DTO.setProduct(product);
                                    cart_DTO.setQty(productQty);

                                    sessionCart.add(cart_DTO);

                                    httpSession.setAttribute("sessionCart", sessionCart);

                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Product Added to Cart");
                                } else {
                                    //quantity not available
                                    response_DTO.setContent("quantity not available");
                                }
                            }

                        }
                    } else {
                        //Product Not found
                        response_DTO.setContent("Product Not found");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

        session.close();
    }

}
