package space.gavinklfong.demo.streamapi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import space.gavinklfong.demo.streamapi.models.Customer;
import space.gavinklfong.demo.streamapi.models.Order;
import space.gavinklfong.demo.streamapi.models.Product;
import space.gavinklfong.demo.streamapi.repos.CustomerRepo;
import space.gavinklfong.demo.streamapi.repos.OrderRepo;
import space.gavinklfong.demo.streamapi.repos.ProductRepo;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@DataJpaTest
class StreamApiMyTest {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    @Test
    @DisplayName("Obtain a list of products belongs to category “Books” with price > 100")
    public void ex1(){
        List<Product> result = productRepo.findAll().stream()
                .filter(p -> p.getCategory().equals("Books"))
                .filter(p -> p.getPrice() > 100.0)
                .collect(Collectors.toList());
        result.forEach(System.out::println);
    }

    @Test
    @DisplayName("Obtain a list of order with products belong to category “Baby”")
    public void ex2(){
        List<Order> result = orderRepo.findAll().stream()
                .filter(o -> o.getProducts()
                        .stream()
                        .anyMatch(product -> product.getCategory().equalsIgnoreCase("Baby")))
                .collect(Collectors.toList());
        result.forEach(System.out::println);
    }

    @Test
    @DisplayName("Obtain a list of product with category = “Toys” and then apply 10% discount")
    public void ex3(){
        List<Product> result = productRepo.findAll().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Toys"))
                .map(product -> product.withPrice(product.getPrice() * 0.9))
                .collect(Collectors.toList());
        result.forEach(System.out::println);
    }

    @Test                                   //comprati
    @DisplayName(" Obtain a list of products ordered by customer of tier " +
            "2 between 01-Feb-2021 and 01-Apr-2021")
    public void ex4(){
        List<Product> result = orderRepo.findAll().stream()
                .filter(order -> order.getCustomer().getTier() == 2)
                .filter(order -> order.getOrderDate()
                        .compareTo(LocalDate.of(2021, 2, 1)) > 0)
                .filter(order -> order.getOrderDate()
                        .compareTo(LocalDate.of(2021, 4, 1)) < 0)
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .collect(Collectors.toList());
        result.forEach(System.out::println);
    }

    @Test
    @DisplayName("Get the cheapest products of “Books” category")
    public void ex5(){
        Optional<Product> result = productRepo.findAll().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Books"))
                .min(Comparator.comparing(Product::getPrice));
        System.out.println(result);
    }

    @Test
    @DisplayName("Get the 3 most recent placed order")
    public void ex6(){
        List<Order> result = orderRepo.findAll().stream()
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .limit(3)
                .collect(Collectors.toList());
        System.out.println(result);
    }

    @Test
    @DisplayName("Get a list of orders which were ordered on 15-Mar-2021, " +
            "log the order records to the console and then return its product list")
    public void ex7(){
        List<Product> result = orderRepo.findAll().stream()
                .filter(order -> order.getOrderDate()
                        .isEqual(LocalDate.of(2021, 3, 15)))
                .peek(System.out::println)
                .flatMap(order -> order.getProducts().stream())
                .collect(Collectors.toList());
        result.forEach(System.out::println);
    }

    @Test
    @DisplayName("Calculate total lump sum of all orders placed in Feb 2021")
    public void ex8(){
        double result = orderRepo.findAll().stream()
                .filter(order -> order.getOrderDate().compareTo(LocalDate.of(2021, 2, 1)) >= 0)
                .filter(order -> order.getOrderDate().compareTo(LocalDate.of(2021, 2, 28)) <= 0)
                .flatMap(order -> order.getProducts().stream())
                .mapToDouble(product -> product.getPrice())
                .sum();
        System.out.println(result);
    }

    @Test
    @DisplayName("Calculate order average payment placed on 14-Mar-2021")
    public void ex9(){
        double result = orderRepo.findAll().stream()
                .filter(order -> order.getOrderDate().isEqual(LocalDate.of(2021, 03, 15)))
                .flatMap(order -> order.getProducts().stream())
                .mapToDouble(product -> product.getPrice())
                .average()
                .getAsDouble();
        System.out.println(result);
    }

    @Test
    @DisplayName("Obtain a collection of statistic figures (i.e. sum, average, max, min, count) " +
            "for all products of category “Books”")
    public void ex10(){
        DoubleSummaryStatistics doubleSummaryStatistics = productRepo.findAll().stream()
                .mapToDouble(Product::getPrice)
                .summaryStatistics();
        System.out.println(doubleSummaryStatistics.getCount());
        System.out.println(doubleSummaryStatistics.getAverage());
        System.out.println(doubleSummaryStatistics.getSum());
        System.out.println(doubleSummaryStatistics.getMax());
        System.out.println(doubleSummaryStatistics.getMin());
    }

    @Test
    @DisplayName("Obtain a data map with order id and order’s product count")
    public void ex11(){
        Map<Long, Integer> result = orderRepo.findAll().stream()
                .collect(Collectors.toMap(
                        order -> order.getId(),
                        order -> order.getProducts().size()));
        System.out.println(result);
    }

    @Test
    @DisplayName("Produce a data map with order records grouped by customer")
    public void ex12(){
        Map<Customer, List<Order>> result = orderRepo.findAll().stream()
                .collect(Collectors.groupingBy(order -> order.getCustomer()));
        System.out.println(result);
    }

    @Test
    @DisplayName("Produce a data map with order record and product total sum")
    public void ex13(){
        Map<Order, Double> result = orderRepo.findAll().stream()
                .collect(Collectors.toMap(
                        Function.identity(), order -> order.getProducts().stream()
                                .mapToDouble(Product::getPrice).sum()
                ));
        System.out.println(result);
    }

    @Test
    @DisplayName("Obtain a data map with list of product name by category")
    public void ex14(){
        Map<String, List<String>> result = productRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.mapping(Product::getName, Collectors.toList())
                ));
    }

    @Test
    @DisplayName("Get the most expensive product by category")
    public void ex15(){
        Map<String, Optional<Product>> result = productRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.maxBy(Comparator.comparing(Product::getPrice))
                ));
        System.out.println(result);
    }


























}