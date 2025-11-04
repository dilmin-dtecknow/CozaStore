package controler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.User_DTO;
import entity.Address;
import entity.Cart;
import entity.City;
import entity.Order_Item;
import entity.Order_Status;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.PayHere;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "Checkout", urlPatterns = {"/Checkout"})
public class Checkout extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);
        Session session = HibernateUtil.getSessionFactory().openSession();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        HttpSession httpSession = request.getSession();
        Transaction transaction = session.beginTransaction();

        boolean isCurrentAddress = requestJsonObject.get("isCurentAddressCheckbox").getAsBoolean();
        String first_name = requestJsonObject.get("first_name").getAsString();
        String last_name = requestJsonObject.get("last_name").getAsString();
        String city_id = requestJsonObject.get("city_id").getAsString();
        String address1 = requestJsonObject.get("address1").getAsString();
        String address2 = requestJsonObject.get("address2").getAsString();
        String postal_code = requestJsonObject.get("postal_code").getAsString();
        String mobile = requestJsonObject.get("mobile").getAsString();

        if (httpSession.getAttribute("user") != null) {
            //user found(sign in)

            //get user from db
            User_DTO user_DTO = (User_DTO) httpSession.getAttribute("user");
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
            User user = (User) criteria1.uniqueResult();

            if (isCurrentAddress) {
                //get curent address
                Criteria criteria2 = session.createCriteria(Address.class);
                criteria2.add(Restrictions.eq("user", user));
                criteria2.addOrder(Order.desc("id"));
                criteria2.setMaxResults(1);

                if (criteria2.list().isEmpty()) {
                    //current address not found
                    responseJsonObject.addProperty("message", "current address not found. Please create a new one");
                } else {
                    //current address found
                    //get the current address
                    Address address = (Address) criteria2.list().get(0);
                    //compleate the check out process
                    Criteria criteria4 = session.createCriteria(Cart.class);
                    criteria4.add(Restrictions.eq("user", user));
                    List<Cart> cartList = criteria4.list();
                    if (!cartList.isEmpty()) {
                        //compleate checkout process
                        saveOrders(session, transaction, user, address, responseJsonObject);
                    } else {
                        responseJsonObject.addProperty("message", "You Have not Item");
                    }
//                    saveOrders(session, transaction, user, address, responseJsonObject);
                }

            } else {

                //create new address
                if (first_name.isEmpty()) {
                    responseJsonObject.addProperty("message", "Please fill first name");
                } else if (last_name.isEmpty()) {
                    responseJsonObject.addProperty("message", "Please fill last name");

                } else if (!Validations.isInteger(city_id)) {
                    responseJsonObject.addProperty("message", "Inavalide city");

                } else {
                    //check city from city

                    Criteria criteria3 = session.createCriteria(City.class);
                    criteria3.add(Restrictions.eq("id", Integer.parseInt(city_id)));

                    if (criteria3.list().isEmpty()) {
                        responseJsonObject.addProperty("message", "Inavalide city select");

                    } else {
                        //city found
                        City city = (City) criteria3.list().get(0);

                        if (address1.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill addresss line1");
                        } else if (address2.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill addresss line2");
                        } else if (postal_code.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill postal code");
                        } else if (postal_code.length() != 5) {
                            responseJsonObject.addProperty("message", "Please fill invalide postal code");
                        } else if (!Validations.isInteger(postal_code)) {
                            responseJsonObject.addProperty("message", "Inavalide postal code");
                        } else if (mobile.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill mobile");
                        } else if (!Validations.isMobileNumberValide(mobile)) {
                            responseJsonObject.addProperty("message", "Invalide mobile number");
                        } else {
                            //Create new Address
                            Address address = new Address();
                            address.setCity(city);
                            address.setFirst_name(first_name);
                            address.setLast_name(last_name);
                            address.setLine1(address1);
                            address.setLine2(address2);
                            address.setMobile(mobile);
                            address.setPostal_code(postal_code);
                            address.setUser(user);

                            session.save(address);
//                            System.out.println("ok");
//get cart Items
                            Criteria criteria4 = session.createCriteria(Cart.class);
                            criteria4.add(Restrictions.eq("user", user));
                            List<Cart> cartList = criteria4.list();
                            if (!cartList.isEmpty()) {
                                //compleate checkout process
                                saveOrders(session, transaction, user, address, responseJsonObject);
                            } else {
                                responseJsonObject.addProperty("message", "You Have not Item");
                            }

                        }
                    }

                }
            }

        } else {
            //user not signin
            responseJsonObject.addProperty("message", "User Not signIn");
        }

        //send response
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJsonObject));
    }

    private void saveOrders(Session session, Transaction transaction, User user, Address address, JsonObject responseJsonObject) {
        try {
            //create order in db
            entity.Orders order = new entity.Orders();
            order.setAddress(address);
            order.setDate_time(new Date());
            order.setUser(user);

            int order_id = (int) session.save(order);

            //get cart Items
            Criteria criteria4 = session.createCriteria(Cart.class);
            criteria4.add(Restrictions.eq("user", user));
            List<Cart> cartList = criteria4.list();

            //get order status (5.Pending) from db
            Order_Status order_Status = (Order_Status) session.get(Order_Status.class, 1);

            //create order items in db
            double amount = 0;
            String items = "";
            for (Cart cartItem : cartList) {

                //calculate amount
                amount += cartItem.getQty() * cartItem.getProduct().getPrice();
//                if (address.getCity().getId() == 1) {
//                    amount += 1000;
//                } else {
//                    amount += 2500;
//                }
                amount += address.getCity().getShipingCharge();
                //calculate amount

                //get item
                items += cartItem.getProduct().getTitle() + " x" + cartItem.getQty() + " ";

                //get product in cart
                Product product = cartItem.getProduct();

                Order_Item order_Item = new Order_Item();
                order_Item.setOder(order);
                order_Item.setOrder_status(order_Status);
                order_Item.setProduct(product);
                order_Item.setQty(cartItem.getQty());
                session.save(order_Item);

                //Update Product qty in db
                product.setQty(product.getQty() - cartItem.getQty());
                session.update(product);

                //Delete Cart Item from db
                session.delete(cartItem);
            }

            transaction.commit();

            //Start:set Payment data
            String merchant_id = "1223369";
            String formatedAmount = new DecimalFormat("0.00").format(amount);
            String curency = "LKR";
            String merchant_secret = "MzI2NTA5ODMxMzQwNDEwNzIwODkxNTQ1NzY4NDg3NDIwMTk0MzY3MA==";
            String merchantSecretMd5Hash = PayHere.generateMD5(merchant_secret);

            JsonObject payhere = new JsonObject();
            payhere.addProperty("merchant_id", merchant_id);

            payhere.addProperty("return_url", "");
            payhere.addProperty("cancel_url", "");
            payhere.addProperty("notify_url", "");

            payhere.addProperty("first_name", user.getFirst_name());
            payhere.addProperty("last_name", user.getLast_name());
            payhere.addProperty("email", user.getEmail());
            payhere.addProperty("phone", "0773245698");
            payhere.addProperty("address", "20,Havloke");
            payhere.addProperty("city", "Colombo");
            payhere.addProperty("country", "Sri Lanka");
            payhere.addProperty("order_id", String.valueOf(order_id));
            payhere.addProperty("items", items);
            payhere.addProperty("currency", curency);
            payhere.addProperty("amount", formatedAmount);
            payhere.addProperty("sandbox", true);

            //Payhere (merchant_id ,order_id  payhere_amount payhere_currency merchant_secret)
            String md5hash = PayHere.generateMD5(merchant_id + order_id + formatedAmount + curency + merchantSecretMd5Hash);
            payhere.addProperty("hash", md5hash);

            //End:set Payment data
            responseJsonObject.addProperty("success", true);
            responseJsonObject.addProperty("message", "All ready Done your checkout");

            Gson gson = new Gson();
            responseJsonObject.add("payherJson", gson.toJsonTree(payhere));
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        }
    }
}
