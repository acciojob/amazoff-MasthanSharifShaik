package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository(){
        this.orderMap = new HashMap<String, Order>();
        this.partnerMap = new HashMap<String, DeliveryPartner>();
        this.partnerToOrderMap = new HashMap<String, HashSet<String>>();
        this.orderToPartnerMap = new HashMap<String, String>();
    }

    public void saveOrder(Order order){
        // your code here
        if (order != null) {
            if (!orderMap.containsKey(order.getId())) {
                orderMap.put(order.getId(), order);
            }
        }
    }

    public void savePartner(String partnerId){
        // your code here
        // create a new partner with given partnerId and save it
        if(partnerId != null && !partnerMap.containsKey(partnerId)){
            DeliveryPartner partner = new DeliveryPartner(partnerId);
            partnerMap.put(partnerId, partner);
        }
    }

    public void saveOrderPartnerMap(String orderId, String partnerId){
        if(orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)){
            // your code here
            //add order to given partner's order list
            //increase order count of partner
            //assign partner to this order
            HashSet<String> orders = partnerToOrderMap.getOrDefault(partnerId, new HashSet<>());
            orders.add(orderId);
            partnerToOrderMap.put(partnerId, orders);
            orderToPartnerMap.put(orderId, partnerId);
            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.setNumberOfOrders(orders.size());
            partnerMap.put(partnerId, partner);
        }
    }

    public Order findOrderById(String orderId){
        // your code here
        return orderMap.get(orderId);
    }

    public DeliveryPartner findPartnerById(String partnerId){
        // your code here
        return partnerMap.get(partnerId);
    }

    public Integer findOrderCountByPartnerId(String partnerId){
        // your code here
        if(partnerMap.containsKey(partnerId)){
            return partnerMap.get(partnerId).getNumberOfOrders();
        }
        return 0;
    }

    public List<String> findOrdersByPartnerId(String partnerId){
        // your code here
        if(partnerToOrderMap.containsKey(partnerId)){
            return new ArrayList<>(partnerToOrderMap.get(partnerId));
        }
        return new ArrayList<>();
    }

    public List<String> findAllOrders(){
        // your code here
        // return list of all orders
        return new ArrayList<>(orderMap.keySet());
    }

    public void deletePartner(String partnerId){
        // your code here
        // delete partner by ID
        if(partnerMap.containsKey(partnerId)){
            partnerMap.remove(partnerId);
        }
        if(partnerToOrderMap.containsKey(partnerId)){
            HashSet<String> orders = partnerToOrderMap.get(partnerId);
            for(String orderId : orders) {
                orderToPartnerMap.remove(orderId);

            }
        partnerToOrderMap.remove(partnerId);}
    }

    public void deleteOrder(String orderId){
        // your code here
        // delete order by ID
        if(orderMap.containsKey(orderId)){
            orderMap.remove(orderId);
        }
        if(orderToPartnerMap.containsKey(orderId)){
            String partnerId = orderToPartnerMap.get(orderId);
            orderToPartnerMap.remove(orderId);
            if(partnerToOrderMap.containsKey(partnerId)){
                HashSet<String> orders = partnerToOrderMap.get(partnerId);
                orders.remove(orderId);
                partnerToOrderMap.put(partnerId, orders);
                DeliveryPartner partner = partnerMap.get(partnerId);
                partner.setNumberOfOrders(orders.size());
                partnerMap.put(partnerId, partner);
            }
        }
    }

    public Integer findCountOfUnassignedOrders(){
        // your code here
        return orderMap.size() - orderToPartnerMap.size();
    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId){
        // your code here
        if(!partnerToOrderMap.containsKey(partnerId)){
            return 0;
        }
        String[] timeParts = timeString.split(":");
        int givenTime = Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);
        int count = 0;
        HashSet<String> orders = partnerToOrderMap.get(partnerId);
        for(String orderId : orders){
            Order order = orderMap.get(orderId);
            if(order.getDeliveryTime() > givenTime) {
                count++;

            }
        }
        return count;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId){
        // your code here
        // code should return string in format HH:MM
        if(!partnerToOrderMap.containsKey(partnerId)){
            return "00:00";
        }
        int lastTime = 0;
        HashSet<String> orders = partnerToOrderMap.get(partnerId);
        for(String orderId : orders) {
            Order order = orderMap.get(orderId);
            if (order.getDeliveryTime() > lastTime) {
                lastTime = order.getDeliveryTime();
            }
        }
        int hours = lastTime / 60;
        int minutes = lastTime % 60;
        String timeString = String.format("%02d:%02d", hours, minutes);
        return timeString;
    }
}