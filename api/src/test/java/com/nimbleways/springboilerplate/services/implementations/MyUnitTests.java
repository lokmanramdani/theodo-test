package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@UnitTest
public class MyUnitTests {

    @Mock
    private NotificationService notificationService;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks 
    private ProductService productService;

    @Test
    public void test() {
        // GIVEN
        Product product =new Product(null, 15, 0, "NORMAL", "RJ45 Cable", null, null, null, null, null, null, null);

        Mockito.when(productRepository.save(product)).thenReturn(product);

        // WHEN
        productService.notifyDelay(product.getLeadTime(), product);

        // THEN
        assertEquals(0, product.getAvailable());
        assertEquals(15, product.getLeadTime());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService, Mockito.times(1)).sendDelayNotification(product.getLeadTime(), product.getName());
    }

    @Test
    public void testSuccessfulFlashsale() {
        // GIVEN
        Product product = Product.builder().available(10).flashSaleSoldQuantity(0).flashSaleMaxQuantity(10).flashSaleEndDate(LocalDate.now().plusDays(2)).build();
        Mockito.when(productRepository.save(product)).thenReturn(product);

        // THEN
        productService.handleFlashSaleProduct(product);

        // ASSERT
        assertEquals(9, product.getAvailable());
        assertEquals(1, product.getFlashSaleSoldQuantity());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
    }

    @Test
    public void testFailedFlashsaleMaxQuantity() {
        // GIVEN
        Product product = Product.builder().available(20).flashSaleSoldQuantity(10).flashSaleMaxQuantity(10).flashSaleEndDate(LocalDate.now().plusDays(10)).build();
        Mockito.when(productRepository.save(product)).thenReturn(product);

        // THEN
        productService.handleFlashSaleProduct(product);

        // ASSERT
        assertEquals(0, product.getAvailable());
        assertEquals(10, product.getFlashSaleSoldQuantity());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
    }

    @Test
    public void testFailedFlashsaleEndDate() {
        // GIVEN
        Product product = Product.builder()
                .available(10)
                .flashSaleSoldQuantity(0)
                .flashSaleMaxQuantity(10)
                .flashSaleEndDate(LocalDate.now().minusDays(2))
            .build();

        Mockito.when(productRepository.save(product)).thenReturn(product);

        // THEN
        productService.handleFlashSaleProduct(product);

        // ASSERT
        assertEquals(0, product.getAvailable());
        assertEquals(0, product.getFlashSaleSoldQuantity());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
    }



}